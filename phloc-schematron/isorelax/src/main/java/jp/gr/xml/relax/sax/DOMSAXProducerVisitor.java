package jp.gr.xml.relax.sax;

import jp.gr.xml.relax.dom.DOMVisitorException;
import jp.gr.xml.relax.dom.IDOMVisitor;
import jp.gr.xml.relax.dom.UDOM;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * DOMVisitor of SAX event producer from DOM tree
 * 
 * @since Feb. 18, 2001
 * @version Feb. 24, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class DOMSAXProducerVisitor implements IDOMVisitor
{
  private String systemID_;
  private String publicID_;
  private DTDHandler dtd_;
  private ContentHandler content_;
  private DeclHandler decl_;
  private LexicalHandler lexical_;
  private ErrorHandler error_;
  private final NamespaceSupport namespace_;
  private boolean throwException_;

  public DOMSAXProducerVisitor ()
  {
    final DefaultHandler handler = new DefaultHandler ();
    dtd_ = handler;
    content_ = handler;
    error_ = handler;
    lexical_ = new LexicalHandlerBase ();
    decl_ = new DeclHandlerBase ();
    namespace_ = new NamespaceSupport ();
    throwException_ = false;
  }

  public void setSystemID (final String id)
  {
    systemID_ = id;
  }

  public void setPublicID (final String id)
  {
    publicID_ = id;
  }

  public void setDTDHandler (final DTDHandler dtd)
  {
    dtd_ = dtd;
  }

  public void setContentHandler (final ContentHandler content)
  {
    content_ = content;
  }

  public void setLexicalHandler (final LexicalHandler lexical)
  {
    lexical_ = lexical;
  }

  public void setDeclHandler (final DeclHandler decl)
  {
    decl_ = decl;
  }

  public void setErrorHandler (final ErrorHandler error)
  {
    error_ = error;
  }

  public void emulateStartDocument ()
  {
    try
    {
      _handleLocator ();
      content_.startDocument ();
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  public void emulateEndDocument ()
  {
    try
    {
      content_.endDocument ();
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  public void throwException (final boolean useException)
  {
    throwException_ = useException;
  }

  public boolean enter (final Element element)
  {
    try
    {
      namespace_.pushContext ();
      String namespaceURI = element.getNamespaceURI ();
      if (namespaceURI == null)
      {
        namespaceURI = "";
      }
      final String localName = element.getLocalName ();
      final String qName = element.getTagName ();
      final NamedNodeMap attrMap = element.getAttributes ();
      final AttributesImpl attrs = new AttributesImpl ();
      final int size = attrMap.getLength ();
      for (int i = 0; i < size; i++)
      {
        final Attr attr = (Attr) attrMap.item (i);
        String attrNamespaceURI = attr.getNamespaceURI ();
        if (attrNamespaceURI == null)
        {
          attrNamespaceURI = "";
        }
        final String attrLocalName = attr.getLocalName ();
        final String attrQName = attr.getName ();
        final String attrValue = attr.getValue ();
        if (attrQName.startsWith ("xmlns"))
        {
          String prefix;
          final int index = attrQName.indexOf (':');
          if (index == -1)
          {
            prefix = "";
          }
          else
          {
            prefix = attrQName.substring (index + 1);
          }
          if (!namespace_.declarePrefix (prefix, attrValue))
          {
            _errorReport ("bad prefix = " + prefix);
          }
          else
          {
            content_.startPrefixMapping (prefix, attrValue);
          }
        }
        else
        {
          attrs.addAttribute (attrNamespaceURI, attrLocalName, attrQName, "CDATA", attrValue);
        }
      }
      content_.startElement (namespaceURI, localName, qName, attrs);
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (true);
  }

  public boolean enter (final Attr attr)
  {
    return (false);
  }

  public boolean enter (final Text text)
  {
    try
    {
      final String data = text.getData ();
      content_.characters (data.toCharArray (), 0, data.length ());
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (false);
  }

  public boolean enter (final CDATASection cdata)
  {
    try
    {
      lexical_.startCDATA ();
      final String data = cdata.getData ();
      content_.characters (data.toCharArray (), 0, data.length ());
      lexical_.endCDATA ();
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (false);
  }

  public boolean enter (final EntityReference entityRef)
  {
    try
    {
      lexical_.startEntity (entityRef.getNodeName ());
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (true);
  }

  public boolean enter (final Entity entity)
  {
    return (false);
  }

  public boolean enter (final ProcessingInstruction pi)
  {
    try
    {
      content_.processingInstruction (pi.getTarget (), pi.getData ());
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (false);
  }

  public boolean enter (final Comment comment)
  {
    try
    {
      final String data = comment.getData ();
      lexical_.comment (data.toCharArray (), 0, data.length ());
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (false);
  }

  public boolean enter (final Document doc)
  {
    try
    {
      _handleLocator ();
      content_.startDocument ();
      _handleDoctype (doc.getDoctype ());
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
    return (true);
  }

  private void _handleLocator ()
  {
    if (systemID_ == null && publicID_ == null)
    {
      return;
    }
    _locatorEvent ();
  }

  private void _locatorEvent ()
  {
    final LocatorImpl locator = new LocatorImpl ();
    locator.setSystemId (systemID_);
    locator.setPublicId (publicID_);
    locator.setLineNumber (-1);
    locator.setColumnNumber (-1);
    content_.setDocumentLocator (locator);
  }

  private void _handleDoctype (final DocumentType doctype)
  {
    try
    {
      if (doctype == null)
      {
        return;
      }
      final String systemID = doctype.getSystemId ();
      final String publicID = doctype.getPublicId ();
      final String internalSubset = doctype.getInternalSubset ();
      if (systemID != null)
      {
        lexical_.startDTD (doctype.getName (), publicID, systemID);
        if (internalSubset == null)
        {
          lexical_.endDTD ();
          _handleEntities (doctype);
        }
        else
        {
          _handleEntities (doctype);
          lexical_.endDTD ();
        }
      }
      else
      {
        _handleEntities (doctype);
      }
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  private void _handleEntities (final DocumentType doctype)
  {
    try
    {
      final NamedNodeMap entities = doctype.getEntities ();
      final int nEntities = entities.getLength ();
      for (int i = 0; i < nEntities; i++)
      {
        final Entity entity = (Entity) entities.item (i);
        final String publicID = entity.getPublicId ();
        final String systemID = entity.getSystemId ();
        final String notationName = entity.getNotationName ();
        if (publicID != null || systemID != null)
        {
          _handleExternalEntity (entity.getNodeName (), publicID, systemID, notationName);
        }
        else
        {
          _handleInternalEntity (entity);
        }
      }
      final NamedNodeMap notations = doctype.getNotations ();
      final int nNotations = notations.getLength ();
      for (int i = 0; i < nNotations; i++)
      {
        final Notation notation = (Notation) notations.item (i);
        final String publicID = notation.getPublicId ();
        final String systemID = notation.getSystemId ();
        dtd_.notationDecl (notation.getNodeName (), publicID, systemID);
      }
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  private void _handleExternalEntity (final String name,
                                      final String publicID,
                                      final String systemID,
                                      final String notationName)
  {
    try
    {
      if (notationName == null)
      {
        decl_.externalEntityDecl (name, publicID, systemID);
      }
      else
      {
        dtd_.unparsedEntityDecl (name, publicID, systemID, notationName);
      }
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  private void _handleInternalEntity (final Entity entity)
  {
    try
    {
      decl_.internalEntityDecl (entity.getNodeName (), UDOM.getXMLText (entity));
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  public boolean enter (final DocumentType doctype)
  {
    return (false);
  }

  public boolean enter (final DocumentFragment docfrag)
  {
    return (true);
  }

  public boolean enter (final Notation notation)
  {
    return (false);
  }

  public boolean enter (final Node node)
  {
    return (false);
  }

  public void leave (final Element element)
  {
    try
    {
      String namespaceURI = element.getNamespaceURI ();
      if (namespaceURI == null)
      {
        namespaceURI = "";
      }
      final String localName = element.getLocalName ();
      final String qName = element.getTagName ();
      content_.endElement (namespaceURI, localName, qName);
      namespace_.popContext ();
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  public void leave (final Attr attr)
  {}

  public void leave (final Text text)
  {}

  public void leave (final CDATASection cdata)
  {}

  public void leave (final EntityReference entityRef)
  {
    try
    {
      lexical_.endEntity (entityRef.getNodeName ());
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  public void leave (final Entity entity)
  {}

  public void leave (final ProcessingInstruction pi)
  {}

  public void leave (final Comment comment)
  {}

  public void leave (final Document doc)
  {
    try
    {
      content_.endDocument ();
    }
    catch (final SAXException e)
    {
      _errorReport (e);
    }
  }

  public void leave (final DocumentType doctype)
  {}

  public void leave (final DocumentFragment docfrag)
  {}

  public void leave (final Notation notation)
  {}

  public void leave (final Node node)
  {}

  private void _errorReport (final String message) throws DOMVisitorException
  {
    _errorReport (new SAXParseException (message, publicID_, systemID_, -1, -1));
  }

  private void _errorReport (final SAXException e) throws DOMVisitorException
  {
    try
    {
      SAXParseException parseException;
      if (e instanceof SAXParseException)
      {
        parseException = (SAXParseException) e;
      }
      else
      {
        parseException = new SAXParseException (e.getMessage (), publicID_, systemID_, -1, -1, e);
      }
      error_.fatalError (parseException);
      if (throwException_)
      {
        throw (new DOMVisitorException (e));
      }
    }
    catch (final SAXException ee)
    {}
  }
}
