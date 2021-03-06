package com.thaiopensource.validate.mns;

import java.util.Hashtable;
import java.util.Stack;

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
  static final Name OWNER_NAME = new Name ("http://www.thaiopensource.com/ns/mns/instance", "owner");
  private SchemaImpl.Mode currentMode;
  private int laxDepth = 0;
  private final ErrorHandler eh;
  private final PropertyMap properties;
  private Locator locator;
  private Subtree subtrees = null;
  private final Hashset attributeNamespaces = new Hashset ();
  private PrefixMapping prefixMapping = null;
  private final Localizer localizer = new Localizer (ValidatorImpl.class);
  private final Hashtable <Schema, Stack> validatorCache = new Hashtable <Schema, Stack> ();

  static private class Subtree
  {
    final Subtree parent;
    final Validator validator;
    final Schema schema;
    final Hashset coveredNamespaces;
    final ElementsOrAttributes prune;
    final SchemaImpl.Mode parentMode;
    final int parentLaxDepth;
    final Stack <Name> context = new Stack <Name> ();
    final ContextMap contextMap;

    Subtree (final Hashset coveredNamespaces,
             final ContextMap contextMap,
             final ElementsOrAttributes prune,
             final Validator validator,
             final Schema schema,
             final SchemaImpl.Mode parentMode,
             final int parentLaxDepth,
             final Subtree parent)
    {
      this.coveredNamespaces = coveredNamespaces;
      this.contextMap = contextMap;
      this.prune = prune;
      this.validator = validator;
      this.schema = schema;
      this.parentMode = parentMode;
      this.parentLaxDepth = parentLaxDepth;
      this.parent = parent;
    }
  }

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

  ValidatorImpl (final SchemaImpl.Mode mode, final PropertyMap properties)
  {
    this.currentMode = mode;
    this.properties = properties;
    this.eh = properties.get (ValidateProperty.ERROR_HANDLER);
  }

  @Override
  public void setDocumentLocator (final Locator locator)
  {
    this.locator = locator;
  }

  @Override
  public void characters (final char ch[], final int start, final int length) throws SAXException
  {
    for (Subtree st = subtrees; wantsEvent (st); st = st.parent)
      st.validator.getContentHandler ().characters (ch, start, length);
  }

  @Override
  public void ignorableWhitespace (final char ch[], final int start, final int length) throws SAXException
  {
    for (Subtree st = subtrees; wantsEvent (st); st = st.parent)
      st.validator.getContentHandler ().ignorableWhitespace (ch, start, length);
  }

  private SchemaImpl.Mode getMode ()
  {
    if (subtrees != null)
    {
      final SchemaImpl.Mode mode = (SchemaImpl.Mode) subtrees.contextMap.get (subtrees.context);
      if (mode != null)
        return mode;
    }
    return currentMode;
  }

  @Override
  public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
  {
    if (namespaceCovered (uri))
      subtrees.context.push (new Name (uri, localName));
    else
    {
      final SchemaImpl.Mode mode = getMode ();
      final SchemaImpl.ElementAction elementAction = mode.getElementAction (uri);
      if (elementAction == null)
      {
        if (laxDepth == 0 && !mode.getLax ().containsElements ())
          error ("element_undeclared_namespace", uri);
        laxDepth++;
      }
      else
      {
        subtrees = new Subtree (elementAction.getCoveredNamespaces (),
                                elementAction.getContextMap (),
                                elementAction.getPrune (),
                                createValidator (elementAction.getSchema ()),
                                elementAction.getSchema (),
                                currentMode,
                                laxDepth,
                                subtrees);
        subtrees.context.push (new Name (uri, localName));
        currentMode = elementAction.getMode ();
        laxDepth = 0;
        startSubtree (subtrees.validator.getContentHandler ());
      }
    }
    for (Subtree st = subtrees; wantsEvent (st); st = st.parent)
    {
      Attributes prunedAtts;
      if (st.prune.containsAttributes ())
        prunedAtts = new NamespaceFilteredAttributes (uri, true, attributes);
      else
        prunedAtts = attributes;
      st.validator.getContentHandler ().startElement (uri, localName, qName, prunedAtts);
    }
    for (int i = 0, len = attributes.getLength (); i < len; i++)
    {
      final String ns = attributes.getURI (i);
      if (!ns.equals ("") && !ns.equals (uri) && !namespaceCovered (ns) && !attributeNamespaces.contains (ns))
      {
        attributeNamespaces.add (ns);
        validateAttributes (ns, attributes);
      }
    }
    attributeNamespaces.clear ();
  }

  private boolean namespaceCovered (final String ns)
  {
    return (laxDepth == 0 && subtrees != null && subtrees.coveredNamespaces.contains (ns));
  }

  private boolean wantsEvent (final Subtree st)
  {
    return st != null && (!st.prune.containsElements () || (laxDepth == 0 && st == subtrees));
  }

  private void validateAttributes (final String ns, final Attributes attributes) throws SAXException
  {
    final SchemaImpl.Mode mode = getMode ();
    final Schema attributesSchema = mode.getAttributesSchema (ns);
    if (attributesSchema == null)
    {
      if (!mode.getLax ().containsAttributes ())
        error ("attributes_undeclared_namespace", ns);
      return;
    }
    final Validator validator = createValidator (attributesSchema);
    final ContentHandler ch = validator.getContentHandler ();
    startSubtree (ch);
    ch.startElement (OWNER_NAME.getNamespaceUri (),
                     OWNER_NAME.getLocalName (),
                     OWNER_NAME.getLocalName (),
                     new NamespaceFilteredAttributes (ns, false, attributes));
    ch.endElement (OWNER_NAME.getNamespaceUri (), OWNER_NAME.getLocalName (), OWNER_NAME.getLocalName ());
    endSubtree (ch);
    releaseValidator (attributesSchema, validator);
  }

  private void startSubtree (final ContentHandler ch) throws SAXException
  {
    if (locator != null)
      ch.setDocumentLocator (locator);
    ch.startDocument ();
    for (PrefixMapping pm = prefixMapping; pm != null; pm = pm.parent)
      ch.startPrefixMapping (pm.prefix, pm.uri);
  }

  private void endSubtree (final ContentHandler ch) throws SAXException
  {
    for (PrefixMapping pm = prefixMapping; pm != null; pm = pm.parent)
      ch.endPrefixMapping (pm.prefix);
    ch.endDocument ();
  }

  @Override
  public void endElement (final String uri, final String localName, final String qName) throws SAXException
  {
    for (Subtree st = subtrees; wantsEvent (st); st = st.parent)
      st.validator.getContentHandler ().endElement (uri, localName, qName);
    if (laxDepth > 0)
      laxDepth--;
    else
      if (!subtrees.context.empty ())
      {
        subtrees.context.pop ();
        if (subtrees.context.empty ())
        {
          endSubtree (subtrees.validator.getContentHandler ());
          releaseValidator (subtrees.schema, subtrees.validator);
          currentMode = subtrees.parentMode;
          laxDepth = subtrees.parentLaxDepth;
          subtrees = subtrees.parent;
        }
      }
  }

  private Validator createValidator (final Schema schema)
  {
    Stack stack = validatorCache.get (schema);
    if (stack == null)
    {
      stack = new Stack ();
      validatorCache.put (schema, stack);
    }
    if (stack.empty ())
      return schema.createValidator (properties);
    return (Validator) stack.pop ();
  }

  private void releaseValidator (final Schema schema, final Validator validator)
  {
    validator.reset ();
    validatorCache.get (schema).push (validator);
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

  public void reset ()
  {
    subtrees = null;
    locator = null;
  }

  public ContentHandler getContentHandler ()
  {
    return this;
  }

  public DTDHandler getDTDHandler ()
  {
    return null;
  }

  private void error (final String key, final String arg) throws SAXException
  {
    eh.error (new SAXParseException (localizer.message (key, arg), locator));
  }
}
