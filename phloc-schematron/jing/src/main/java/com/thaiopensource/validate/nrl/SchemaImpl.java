package com.thaiopensource.validate.nrl;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;

import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.util.Uri;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.validate.Option;
import com.thaiopensource.validate.OptionArgumentException;
import com.thaiopensource.validate.OptionArgumentPresenceException;
import com.thaiopensource.validate.ResolverFactory;
import com.thaiopensource.validate.Schema;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;
import com.thaiopensource.validate.auto.SchemaFuture;
import com.thaiopensource.validate.prop.wrap.WrapProperty;
import com.thaiopensource.xml.sax.CountingErrorHandler;
import com.thaiopensource.xml.sax.DelegatingContentHandler;
import com.thaiopensource.xml.sax.XmlBaseHandler;
import com.thaiopensource.xml.util.WellKnownNamespaces;

class SchemaImpl extends AbstractSchema
{
  static private final String IMPLICIT_MODE_NAME = "#implicit";
  static private final String WRAPPER_MODE_NAME = "#wrapper";
  static final String NRL_URI = SchemaReader.BASE_URI + "nrl";
  private final Hashtable <String, Mode> modeMap = new Hashtable <String, Mode> ();
  private Mode startMode;
  private final Mode defaultBaseMode;
  private final boolean attributesSchema;

  static private final class WrappedIOException extends RuntimeException
  {
    private final IOException exception;

    private WrappedIOException (final IOException exception)
    {
      this.exception = exception;
    }

    private IOException getException ()
    {
      return exception;
    }
  }

  static private class MustSupportOption
  {
    private final String name;
    private final PropertyId pid;
    private final Locator locator;

    MustSupportOption (final String name, final PropertyId pid, final Locator locator)
    {
      this.name = name;
      this.pid = pid;
      this.locator = locator;
    }
  }

  private class Handler extends DelegatingContentHandler implements SchemaFuture
  {
    private final SchemaReceiverImpl sr;
    private boolean hadError = false;
    private final ErrorHandler eh;
    private final SAXResolver resolver;
    private final CountingErrorHandler ceh;
    private final Localizer localizer = new Localizer (SchemaImpl.class);
    private Locator locator;
    private final XmlBaseHandler xmlBaseHandler = new XmlBaseHandler ();
    private int foreignDepth = 0;
    private Mode currentMode = null;
    private String defaultSchemaType;
    private Validator validator;
    private ElementsOrAttributes match;
    private ActionSet actions;
    private AttributeActionSet attributeActions;
    private String schemaUri;
    private String schemaUriBase;
    private String schemaType;
    private PropertyMapBuilder options;
    private final Vector <MustSupportOption> mustSupportOptions = new Vector <MustSupportOption> ();
    private ModeUsage modeUsage;
    private boolean anyNamespace;

    Handler (final SchemaReceiverImpl sr)
    {
      this.sr = sr;
      this.eh = sr.getProperties ().get (ValidateProperty.ERROR_HANDLER);
      this.ceh = new CountingErrorHandler (this.eh);
      this.resolver = ResolverFactory.createResolver (sr.getProperties ());
    }

    @Override
    public void setDocumentLocator (final Locator locator)
    {
      xmlBaseHandler.setLocator (locator);
      this.locator = locator;
    }

    @Override
    public void startDocument () throws SAXException
    {
      try
      {
        final PropertyMapBuilder builder = new PropertyMapBuilder (sr.getProperties ());
        builder.put (ValidateProperty.ERROR_HANDLER, ceh);
        validator = sr.getNrlSchema ().createValidator (builder.toPropertyMap ());
      }
      catch (final IOException e)
      {
        throw new WrappedIOException (e);
      }
      catch (final IncorrectSchemaException e)
      {
        throw new RuntimeException ("internal error in RNG schema for NRL");
      }
      setDelegate (validator.getContentHandler ());
      if (locator != null)
        super.setDocumentLocator (locator);
      super.startDocument ();
    }

    public Schema getSchema () throws IncorrectSchemaException, SAXException
    {
      if (validator == null || ceh.getHadErrorOrFatalError ())
        throw new IncorrectSchemaException ();
      final Hashset openModes = new Hashset ();
      final Hashset checkedModes = new Hashset ();
      for (final Enumeration <String> e = modeMap.keys (); e.hasMoreElements ();)
      {
        final String modeName = e.nextElement ();
        final Mode mode = modeMap.get (modeName);
        if (!mode.isDefined ())
          error ("undefined_mode", modeName, mode.getWhereUsed ());
        for (Mode tem = mode; tem != null; tem = tem.getBaseMode ())
        {
          if (checkedModes.contains (tem))
            break;
          if (openModes.contains (tem))
          {
            error ("mode_cycle", tem.getName (), tem.getWhereDefined ());
            break;
          }
          openModes.add (tem);
        }
        checkedModes.addAll (openModes);
        openModes.clear ();
      }
      if (hadError)
        throw new IncorrectSchemaException ();
      return SchemaImpl.this;
    }

    public RuntimeException unwrapException (final RuntimeException e) throws SAXException,
                                                                      IOException,
                                                                      IncorrectSchemaException
    {
      if (e instanceof WrappedIOException)
        throw ((WrappedIOException) e).getException ();
      return e;
    }

    @Override
    public void startElement (final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException
    {
      super.startElement (uri, localName, qName, attributes);
      xmlBaseHandler.startElement ();
      final String xmlBase = attributes.getValue (WellKnownNamespaces.XML, "base");
      if (xmlBase != null)
        xmlBaseHandler.xmlBaseAttribute (xmlBase);
      if (!NRL_URI.equals (uri) || foreignDepth > 0)
      {
        foreignDepth++;
        return;
      }
      if (ceh.getHadErrorOrFatalError ())
        return;
      if (localName.equals ("rules"))
        parseRules (attributes);
      else
        if (localName.equals ("mode"))
          parseMode (attributes);
        else
          if (localName.equals ("namespace"))
            parseNamespace (attributes);
          else
            if (localName.equals ("anyNamespace"))
              parseAnyNamespace (attributes);
            else
              if (localName.equals ("validate"))
                parseValidate (attributes);
              else
                if (localName.equals ("reject"))
                  parseReject (attributes);
                else
                  if (localName.equals ("attach"))
                    parseAttach (attributes);
                  else
                    if (localName.equals ("unwrap"))
                      parseUnwrap (attributes);
                    else
                      if (localName.equals ("allow"))
                        parseAllow (attributes);
                      else
                        if (localName.equals ("context"))
                          parseContext (attributes);
                        else
                          if (localName.equals ("option"))
                            parseOption (attributes);
                          else
                            throw new RuntimeException ("unexpected element \"" + localName + "\"");
    }

    @Override
    public void endElement (final String namespaceURI, final String localName, final String qName) throws SAXException
    {
      super.endElement (namespaceURI, localName, qName);
      xmlBaseHandler.endElement ();
      if (foreignDepth > 0)
      {
        foreignDepth--;
        return;
      }
      if (ceh.getHadErrorOrFatalError ())
        return;
      if (localName.equals ("validate"))
        finishValidate ();
    }

    private void parseRules (final Attributes attributes)
    {
      startMode = getModeAttribute (attributes, "startMode");
      if (startMode == null)
      {
        startMode = lookupCreateMode (IMPLICIT_MODE_NAME);
        currentMode = startMode;
        startMode.noteDefined (null);
      }
      startMode.noteUsed (locator);
      if (attributesSchema)
      {
        final Mode wrapper = lookupCreateMode (WRAPPER_MODE_NAME);
        final ActionSet actions = new ActionSet ();
        actions.addNoResultAction (new AllowAction (new ModeUsage (startMode, startMode)));
        wrapper.bindElement (Mode.ANY_NAMESPACE, actions);
        wrapper.noteDefined (null);
        startMode = wrapper;
      }
      defaultSchemaType = getSchemaType (attributes);
    }

    private void parseMode (final Attributes attributes) throws SAXException
    {
      currentMode = getModeAttribute (attributes, "name");
      if (currentMode.isDefined ())
      {
        error ("duplicate_mode", currentMode.getName ());
        error ("first_mode", currentMode.getName (), currentMode.getWhereDefined ());
      }
      else
      {
        final Mode base = getModeAttribute (attributes, "extends");
        if (base != null)
          currentMode.setBaseMode (base);
        currentMode.noteDefined (locator);
      }
    }

    private void parseNamespace (final Attributes attributes) throws SAXException
    {
      anyNamespace = false;
      parseRule (getNs (attributes), attributes);
    }

    private void parseAnyNamespace (final Attributes attributes) throws SAXException
    {
      anyNamespace = true;
      parseRule (Mode.ANY_NAMESPACE, attributes);
    }

    private void parseRule (final String ns, final Attributes attributes) throws SAXException
    {
      match = toElementsOrAttributes (attributes.getValue ("", "match"), ElementsOrAttributes.ELEMENTS);
      if (match.containsAttributes ())
      {
        attributeActions = new AttributeActionSet ();
        if (!currentMode.bindAttribute (ns, attributeActions))
        {
          if (ns.equals (Mode.ANY_NAMESPACE))
            error ("duplicate_attribute_action_any_namespace");
          else
            error ("duplicate_attribute_action", ns);
        }
      }
      if (match.containsElements ())
      {
        actions = new ActionSet ();
        if (!currentMode.bindElement (ns, actions))
        {
          if (ns.equals (Mode.ANY_NAMESPACE))
            error ("duplicate_element_action_any_namespace");
          else
            error ("duplicate_element_action", ns);
        }
      }
      else
        actions = null;
    }

    private void parseValidate (final Attributes attributes) throws SAXException
    {
      schemaUri = getSchema (attributes);
      schemaUriBase = xmlBaseHandler.getBaseUri ();
      schemaType = getSchemaType (attributes);
      if (schemaType == null)
        schemaType = defaultSchemaType;
      if (actions != null)
        modeUsage = getModeUsage (attributes);
      else
        modeUsage = null;
      options = new PropertyMapBuilder ();
      mustSupportOptions.clear ();
    }

    private void finishValidate () throws SAXException
    {
      try
      {
        if (attributeActions != null)
        {
          final Schema schema = createSubSchema (true);
          attributeActions.addSchema (schema);
        }
        if (actions != null)
        {
          final Schema schema = createSubSchema (false);
          actions.addNoResultAction (new ValidateAction (modeUsage, schema));
        }
      }
      catch (final IncorrectSchemaException e)
      {
        hadError = true;
      }
      catch (final IOException e)
      {
        throw new WrappedIOException (e);
      }
    }

    private Schema createSubSchema (final boolean isAttributesSchema) throws IOException,
                                                                     IncorrectSchemaException,
                                                                     SAXException
    {
      final PropertyMap requestedProperties = options.toPropertyMap ();
      final Schema schema = sr.createChildSchema (resolver.resolve (schemaUri, schemaUriBase),
                                                  schemaType,
                                                  requestedProperties,
                                                  isAttributesSchema);
      final PropertyMap actualProperties = schema.getProperties ();
      for (final Enumeration <MustSupportOption> e = mustSupportOptions.elements (); e.hasMoreElements ();)
      {
        final MustSupportOption mso = e.nextElement ();
        final Object actualValue = actualProperties.get (mso.pid);
        if (actualValue == null)
          error ("unsupported_option", mso.name, mso.locator);
        else
          if (!actualValue.equals (requestedProperties.get (mso.pid)))
            error ("unsupported_option_arg", mso.name, mso.locator);
      }
      return schema;
    }

    private void parseOption (final Attributes attributes) throws SAXException
    {
      boolean mustSupport;
      String mustSupportValue = attributes.getValue ("", "mustSupport");
      if (mustSupportValue != null)
      {
        mustSupportValue = mustSupportValue.trim ();
        mustSupport = mustSupportValue.equals ("1") || mustSupportValue.equals ("true");
      }
      else
        mustSupport = false;
      final String name = Uri.resolve (NRL_URI, attributes.getValue ("", "name"));
      final Option option = sr.getOption (name);
      if (option == null)
      {
        if (mustSupport)
          error ("unknown_option", name);
      }
      else
      {
        final String arg = attributes.getValue ("", "arg");
        try
        {
          final PropertyId pid = option.getPropertyId ();
          Object value = option.valueOf (arg);
          final Object oldValue = options.get (pid);
          if (oldValue != null)
          {
            value = option.combine (new Object [] { oldValue, value });
            if (value == null)
              error ("duplicate_option", name);
            else
              options.put (pid, value);
          }
          else
          {
            options.put (pid, value);
            mustSupportOptions.addElement (new MustSupportOption (name, pid, locator == null ? null
                                                                                            : new LocatorImpl (locator)));
          }
        }
        catch (final OptionArgumentPresenceException e)
        {
          error (arg == null ? "option_requires_argument" : "option_unexpected_argument", name);
        }
        catch (final OptionArgumentException e)
        {
          if (arg == null)
            error ("option_requires_argument", name);
          else
            error ("option_bad_argument", name, arg);
        }
      }
    }

    private void parseAttach (final Attributes attributes)
    {
      if (attributeActions != null)
        attributeActions.setAttach (true);
      if (actions != null)
      {
        modeUsage = getModeUsage (attributes);
        actions.setResultAction (new AttachAction (modeUsage));
      }
      else
        modeUsage = null;
    }

    private void parseUnwrap (final Attributes attributes)
    {
      if (actions != null)
      {
        modeUsage = getModeUsage (attributes);
        actions.setResultAction (new UnwrapAction (modeUsage));
      }
      else
        modeUsage = null;
    }

    private void parseAllow (final Attributes attributes)
    {
      if (actions != null)
      {
        modeUsage = getModeUsage (attributes);
        actions.addNoResultAction (new AllowAction (modeUsage));
      }
      else
        modeUsage = null;
    }

    private void parseReject (final Attributes attributes)
    {
      if (actions != null)
      {
        modeUsage = getModeUsage (attributes);
        actions.addNoResultAction (new RejectAction (modeUsage));
      }
      else
        modeUsage = null;
      if (attributeActions != null)
        attributeActions.setReject (true);
    }

    private void parseContext (final Attributes attributes) throws SAXException
    {
      if (anyNamespace)
      {
        error ("context_any_namespace");
        return;
      }
      final Mode mode = getUseMode (attributes);
      try
      {
        final Vector paths = Path.parse (attributes.getValue ("", "path"));
        // XXX warning if modeUsage is null
        if (modeUsage != null)
        {
          for (int i = 0, len = paths.size (); i < len; i++)
          {
            final Path path = (Path) paths.elementAt (i);
            if (!modeUsage.addContext (path.isRoot (), path.getNames (), mode))
              error ("duplicate_path", path.toString ());
          }
        }
      }
      catch (final Path.ParseException e)
      {
        error (e.getMessageKey ());
      }
    }

    private String getSchema (final Attributes attributes) throws SAXException
    {
      final String schemaUri = attributes.getValue ("", "schema");
      if (Uri.hasFragmentId (schemaUri))
        error ("schema_fragment_id");
      return schemaUri;
    }

    private String getSchemaType (final Attributes attributes)
    {
      return attributes.getValue ("", "schemaType");
    }

    private ElementsOrAttributes toElementsOrAttributes (final String value, final ElementsOrAttributes defaultValue)
    {
      if (value == null)
        return defaultValue;
      ElementsOrAttributes eoa = ElementsOrAttributes.NEITHER;
      if (value.indexOf ("elements") >= 0)
        eoa = eoa.addElements ();
      if (value.indexOf ("attributes") >= 0)
        eoa = eoa.addAttributes ();
      return eoa;
    }

    private ModeUsage getModeUsage (final Attributes attributes)
    {
      return new ModeUsage (getUseMode (attributes), currentMode);
    }

    private Mode getUseMode (final Attributes attributes)
    {
      final Mode mode = getModeAttribute (attributes, "useMode");
      if (mode == null)
        return Mode.CURRENT;
      mode.noteUsed (locator);
      return mode;
    }

    private String getNs (final Attributes attributes) throws SAXException
    {
      final String ns = attributes.getValue ("", "ns");
      if (ns != null && !Uri.isAbsolute (ns) && !ns.equals (""))
        error ("ns_absolute");
      return ns;
    }

    void error (final String key) throws SAXException
    {
      hadError = true;
      if (eh == null)
        return;
      eh.error (new SAXParseException (localizer.message (key), locator));
    }

    void error (final String key, final String arg) throws SAXException
    {
      hadError = true;
      if (eh == null)
        return;
      eh.error (new SAXParseException (localizer.message (key, arg), locator));
    }

    void error (final String key, final String arg, final Locator locator) throws SAXException
    {
      hadError = true;
      if (eh == null)
        return;
      eh.error (new SAXParseException (localizer.message (key, arg), locator));
    }

    void error (final String key, final String arg1, final String arg2) throws SAXException
    {
      hadError = true;
      if (eh == null)
        return;
      eh.error (new SAXParseException (localizer.message (key, arg1, arg2), locator));
    }

  }

  SchemaImpl (final PropertyMap properties)
  {
    super (properties);
    this.attributesSchema = properties.contains (WrapProperty.ATTRIBUTE_OWNER);
    makeBuiltinMode ("#allow", AllowAction.class);
    makeBuiltinMode ("#attach", AttachAction.class);
    makeBuiltinMode ("#unwrap", UnwrapAction.class);
    defaultBaseMode = makeBuiltinMode ("#reject", RejectAction.class);
  }

  private Mode makeBuiltinMode (final String name, final Class cls)
  {
    final Mode mode = lookupCreateMode (name);
    final ActionSet actions = new ActionSet ();
    final ModeUsage modeUsage = new ModeUsage (Mode.CURRENT, mode);
    if (cls == AttachAction.class)
      actions.setResultAction (new AttachAction (modeUsage));
    else
      if (cls == AllowAction.class)
        actions.addNoResultAction (new AllowAction (modeUsage));
      else
        if (cls == UnwrapAction.class)
          actions.setResultAction (new UnwrapAction (modeUsage));
        else
          actions.addNoResultAction (new RejectAction (modeUsage));
    mode.bindElement (Mode.ANY_NAMESPACE, actions);
    mode.noteDefined (null);
    final AttributeActionSet attributeActions = new AttributeActionSet ();
    if (attributesSchema)
      attributeActions.setReject (true);
    else
      attributeActions.setAttach (true);
    mode.bindAttribute (Mode.ANY_NAMESPACE, attributeActions);
    return mode;
  }

  SchemaFuture installHandlers (final XMLReader in, final SchemaReceiverImpl sr)
  {
    final Handler h = new Handler (sr);
    in.setContentHandler (h);
    return h;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    return new ValidatorImpl (startMode, properties);
  }

  private Mode getModeAttribute (final Attributes attributes, final String localName)
  {
    return lookupCreateMode (attributes.getValue ("", localName));
  }

  private Mode lookupCreateMode (String name)
  {
    if (name == null)
      return null;
    name = name.trim ();
    Mode mode = modeMap.get (name);
    if (mode == null)
    {
      mode = new Mode (name, defaultBaseMode);
      modeMap.put (name, mode);
    }
    return mode;
  }

}
