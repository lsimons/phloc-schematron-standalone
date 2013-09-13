package com.thaiopensource.validate.nrl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;
import com.thaiopensource.xml.util.Name;

class ValidatorImpl extends DefaultHandler implements Validator
{
  static final Name OWNER_NAME = new Name ("http://www.thaiopensource.com/validate/nrl/instance", "owner");
  private static final String NO_NS = "\0";
  private final ErrorHandler eh;
  private final PropertyMap properties;
  private Locator locator;
  private Section currentSection;
  private PrefixMapping prefixMapping = null;
  private final Hashtable <Schema, Stack> validatorHandlerCache = new Hashtable <Schema, Stack> ();
  private final Localizer localizer = new Localizer (ValidatorImpl.class);
  private final Hashset noResultActions = new Hashset ();
  private final Hashtable <String, IntSet> attributeNamespaceIndexSets = new Hashtable <String, IntSet> ();
  private final Vector <IntSet> activeHandlersAttributeIndexSets = new Vector <IntSet> ();
  private final Hashset attributeSchemas = new Hashset ();
  private boolean attributeNamespaceRejected;
  private Attributes filteredAttributes;
  private final Mode startMode;

  static private class PrefixMapping
  {
    final String prefix;
    final String uri;
    final PrefixMapping parent;

    PrefixMapping (final String prefix, final String uri, final PrefixMapping parent)
    {
      this.prefix = prefix;
      this.uri = uri;
      this.parent = parent;
    }
  }

  private class Section implements SectionState
  {
    final Section parent;
    /**
     * Namespace of this section. Empty string for absent.
     */
    final String ns;
    /**
     * Number of open elements in this section.
     */
    int depth = 0;
    /**
     * List of the Validators rooted in this section
     */
    final Vector <Validator> validators = new Vector <Validator> ();
    final Vector <Schema> schemas = new Vector <Schema> ();
    /**
     * List of the ContentHandlers that want to see the elements in this section
     */
    final Vector <ContentHandler> activeHandlers = new Vector <ContentHandler> ();
    final Vector <ModeUsage> activeHandlersAttributeModeUsage = new Vector <ModeUsage> ();
    final Vector <ModeUsage> attributeValidationModeUsages = new Vector <ModeUsage> ();
    /**
     * List of Programs saying what to do with child sections
     */
    final Vector <Program> childPrograms = new Vector <Program> ();
    final Stack <String> context = new Stack <String> ();
    boolean contextDependent = false;
    int attributeProcessing = Mode.ATTRIBUTE_PROCESSING_NONE;

    Section (final String ns, final Section parent)
    {
      this.ns = ns;
      this.parent = parent;
    }

    public void addChildMode (final ModeUsage modeUsage, final ContentHandler handler)
    {
      childPrograms.addElement (new Program (modeUsage, handler));
      if (modeUsage.isContextDependent ())
        contextDependent = true;
    }

    public void addValidator (final Schema schema, final ModeUsage modeUsage)
    {
      schemas.addElement (schema);
      final Validator validator = createValidator (schema);
      validators.addElement (validator);
      activeHandlers.addElement (validator.getContentHandler ());
      activeHandlersAttributeModeUsage.addElement (modeUsage);
      attributeProcessing = Math.max (attributeProcessing, modeUsage.getAttributeProcessing ());
      childPrograms.addElement (new Program (modeUsage, validator.getContentHandler ()));
      if (modeUsage.isContextDependent ())
        contextDependent = true;
    }

    public void addActiveHandler (final ContentHandler handler, final ModeUsage attributeModeUsage)
    {
      activeHandlers.addElement (handler);
      activeHandlersAttributeModeUsage.addElement (attributeModeUsage);
      attributeProcessing = Math.max (attributeProcessing, attributeModeUsage.getAttributeProcessing ());
      if (attributeModeUsage.isContextDependent ())
        contextDependent = true;
    }

    public void addAttributeValidationModeUsage (final ModeUsage modeUsage)
    {
      final int ap = modeUsage.getAttributeProcessing ();
      if (ap != Mode.ATTRIBUTE_PROCESSING_NONE)
      {
        attributeValidationModeUsages.addElement (modeUsage);
        attributeProcessing = Math.max (ap, attributeProcessing);
        if (modeUsage.isContextDependent ())
          contextDependent = true;
      }
    }

    public void reject () throws SAXException
    {
      if (eh != null)
        eh.error (new SAXParseException (localizer.message ("reject_element", ns), locator));
    }

  }

  static private class Program
  {
    final ModeUsage modeUsage;
    final ContentHandler handler;

    Program (final ModeUsage modeUsage, final ContentHandler handler)
    {
      this.modeUsage = modeUsage;
      this.handler = handler;
    }
  }

  ValidatorImpl (final Mode mode, final PropertyMap properties)
  {
    this.properties = properties;
    this.eh = properties.get (ValidateProperty.ERROR_HANDLER);
    this.startMode = mode;
    initCurrentSection ();
  }

  private void initCurrentSection ()
  {
    currentSection = new Section (NO_NS, null);
    currentSection.addChildMode (new ModeUsage (startMode, startMode), null);
  }

  @Override
  public void setDocumentLocator (final Locator locator)
  {
    this.locator = locator;
  }

  @Override
  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    for (int i = 0, len = currentSection.activeHandlers.size (); i < len; i++)
      (currentSection.activeHandlers.elementAt (i)).characters (ch, start, length);

  }

  @Override
  public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
  {
    for (int i = 0, len = currentSection.activeHandlers.size (); i < len; i++)
      (currentSection.activeHandlers.elementAt (i)).ignorableWhitespace (ch, start, length);
  }

  @Override
  public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
  {
    if (!uri.equals (currentSection.ns))
      startSection (uri);
    currentSection.depth++;
    if (currentSection.contextDependent)
      currentSection.context.push (localName);
    final boolean transformAttributes = processAttributes (attributes);
    for (int i = 0, len = currentSection.activeHandlers.size (); i < len; i++)
    {
      final ContentHandler handler = (currentSection.activeHandlers.elementAt (i));
      handler.startElement (uri,
                            localName,
                            qName,
                            transformAttributes
                                               ? filterAttributes (activeHandlersAttributeIndexSets.elementAt (i),
                                                                   attributes) : attributes);
    }
  }

  private static Attributes filterAttributes (final IntSet indexSet, final Attributes attributes)
  {
    if (indexSet.size () == attributes.getLength ())
      return attributes;
    return new FilteredAttributes (indexSet, attributes);
  }

  private boolean processAttributes (final Attributes attributes) throws SAXException
  {
    if (currentSection.attributeProcessing == Mode.ATTRIBUTE_PROCESSING_NONE || attributes.getLength () == 0)
      return false;
    attributeNamespaceIndexSets.clear ();
    for (int i = 0, len = attributes.getLength (); i < len; i++)
    {
      final String ns = attributes.getURI (i);
      IntSet indexSet = attributeNamespaceIndexSets.get (ns);
      if (indexSet == null)
      {
        indexSet = new IntSet ();
        attributeNamespaceIndexSets.put (ns, indexSet);
      }
      indexSet.add (i);
    }
    if (currentSection.attributeProcessing == Mode.ATTRIBUTE_PROCESSING_QUALIFIED &&
        attributeNamespaceIndexSets.size () == 1 &&
        attributeNamespaceIndexSets.get ("") != null)
      return false;
    final Vector <ModeUsage> handlerModes = currentSection.activeHandlersAttributeModeUsage;
    activeHandlersAttributeIndexSets.setSize (handlerModes.size ());
    for (int i = 0, len = handlerModes.size (); i < len; i++)
      activeHandlersAttributeIndexSets.setElementAt (new IntSet (), i);
    boolean transform = false;
    final Vector <ModeUsage> validationModes = currentSection.attributeValidationModeUsages;
    for (final Enumeration <String> e = attributeNamespaceIndexSets.keys (); e.hasMoreElements ();)
    {
      final String ns = e.nextElement ();
      final IntSet indexSet = attributeNamespaceIndexSets.get (ns);
      attributeSchemas.clear ();
      filteredAttributes = null;
      attributeNamespaceRejected = false;
      for (int i = 0, len = handlerModes.size (); i < len; i++)
      {
        final ModeUsage modeUsage = handlerModes.elementAt (i);
        final AttributeActionSet actions = processAttributeSection (modeUsage, ns, indexSet, attributes);
        if (actions.getAttach ())
          activeHandlersAttributeIndexSets.get (i).addAll (indexSet);
        else
          transform = true;
      }
      for (int i = 0, len = validationModes.size (); i < len; i++)
      {
        final ModeUsage modeUsage = validationModes.elementAt (i);
        processAttributeSection (modeUsage, ns, indexSet, attributes);
      }
    }
    return transform;
  }

  private AttributeActionSet processAttributeSection (final ModeUsage modeUsage,
                                                      final String ns,
                                                      final IntSet indexSet,
                                                      final Attributes attributes) throws SAXException
  {
    final Mode mode = modeUsage.getMode (currentSection.context);
    final AttributeActionSet actions = mode.getAttributeActions (ns);
    if (actions.getReject () && !attributeNamespaceRejected)
    {
      attributeNamespaceRejected = true;
      if (eh != null)
        eh.error (new SAXParseException (localizer.message ("reject_attribute", ns), locator));
    }
    final Schema [] schemas = actions.getSchemas ();
    for (final Schema schema : schemas)
    {
      if (attributeSchemas.contains (schema))
        continue;
      attributeSchemas.add (schema);
      if (filteredAttributes == null)
        filteredAttributes = filterAttributes (indexSet, attributes);
      validateAttributes (schema, filteredAttributes);
    }
    return actions;
  }

  private void validateAttributes (final Schema schema, final Attributes attributes) throws SAXException
  {
    final Validator validator = createValidator (schema);
    final ContentHandler ch = validator.getContentHandler ();
    initHandler (ch);
    ch.startElement (OWNER_NAME.getNamespaceUri (), OWNER_NAME.getLocalName (), OWNER_NAME.getLocalName (), attributes);
    ch.endElement (OWNER_NAME.getNamespaceUri (), OWNER_NAME.getLocalName (), OWNER_NAME.getLocalName ());
    cleanupHandler (ch);
    releaseValidator (schema, validator);
  }

  private void startSection (final String uri) throws SAXException
  {
    final Section section = new Section (uri, currentSection);
    final Vector <Program> childPrograms = currentSection.childPrograms;
    noResultActions.clear ();
    for (int i = 0, len = childPrograms.size (); i < len; i++)
    {
      final Program program = childPrograms.elementAt (i);
      final ActionSet actions = program.modeUsage.getMode (currentSection.context).getElementActions (uri);
      final ResultAction resultAction = actions.getResultAction ();
      if (resultAction != null)
        resultAction.perform (program.handler, section);
      final NoResultAction [] nra = actions.getNoResultActions ();
      for (final NoResultAction element : nra)
      {
        final NoResultAction tem = element;
        if (!noResultActions.contains (tem))
        {
          element.perform (section);
          noResultActions.add (tem);
        }
      }
    }
    for (int i = 0, len = section.validators.size (); i < len; i++)
      initHandler (section.validators.elementAt (i).getContentHandler ());
    currentSection = section;
  }

  private void initHandler (final ContentHandler ch) throws SAXException
  {
    if (locator != null)
      ch.setDocumentLocator (locator);
    ch.startDocument ();
    for (PrefixMapping pm = prefixMapping; pm != null; pm = pm.parent)
      ch.startPrefixMapping (pm.prefix, pm.uri);
  }

  @Override
  public void endElement (final String uri, final String localName, final String qName) throws SAXException
  {
    for (int i = 0, len = currentSection.activeHandlers.size (); i < len; i++)
      (currentSection.activeHandlers.elementAt (i)).endElement (uri, localName, qName);
    currentSection.depth--;
    if (currentSection.contextDependent)
      currentSection.context.pop ();
    if (currentSection.depth == 0)
      endSection ();
  }

  private void endSection () throws SAXException
  {
    for (int i = 0, len = currentSection.validators.size (); i < len; i++)
    {
      final Validator validator = currentSection.validators.elementAt (i);
      cleanupHandler (validator.getContentHandler ());
      releaseValidator (currentSection.schemas.elementAt (i), validator);
      // endDocument() on one of the validators may throw an exception
      // in this case we don't want to release the validator twice
      currentSection.validators.setElementAt (null, i);
    }
    currentSection = currentSection.parent;
  }

  private void cleanupHandler (final ContentHandler vh) throws SAXException
  {
    for (PrefixMapping pm = prefixMapping; pm != null; pm = pm.parent)
      vh.endPrefixMapping (pm.prefix);
    vh.endDocument ();
  }

  @Override
  public void endDocument () throws SAXException
  {}

  @Override
  public void startPrefixMapping (final String prefix, final String uri) throws SAXException
  {
    super.startPrefixMapping (prefix, uri);
    prefixMapping = new PrefixMapping (prefix, uri, prefixMapping);
  }

  @Override
  public void endPrefixMapping (final String prefix) throws SAXException
  {
    super.endPrefixMapping (prefix);
    prefixMapping = prefixMapping.parent;
  }

  private Validator createValidator (final Schema schema)
  {
    Stack stack = validatorHandlerCache.get (schema);
    if (stack == null)
    {
      stack = new Stack ();
      validatorHandlerCache.put (schema, stack);
    }
    if (stack.empty ())
      return schema.createValidator (properties);
    return (Validator) stack.pop ();
  }

  private void releaseValidator (final Schema schema, final Validator vh)
  {
    if (vh == null)
      return;
    vh.reset ();
    validatorHandlerCache.get (schema).push (vh);
  }

  public void reset ()
  {
    for (; currentSection != null; currentSection = currentSection.parent)
    {
      for (int i = 0, len = currentSection.validators.size (); i < len; i++)
        releaseValidator (currentSection.schemas.elementAt (i),
                          currentSection.validators.elementAt (i));
    }
    initCurrentSection ();
  }

  public ContentHandler getContentHandler ()
  {
    return this;
  }

  public DTDHandler getDTDHandler ()
  {
    return this;
  }
}
