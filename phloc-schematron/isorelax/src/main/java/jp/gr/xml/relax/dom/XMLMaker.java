package jp.gr.xml.relax.dom;

import jp.gr.xml.relax.xml.UXML;

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

/**
 * XMLMaker
 * 
 * @since Oct. 27, 2000
 * @version Feb. 24, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class XMLMaker implements IDOMVisitor
{
  protected StringBuilder buffer_;
  protected String encoding_ = "UTF-8";
  protected boolean dom2_ = false;
  protected boolean expandEntityReference_ = false;
  protected boolean emptyElementTag_ = false;

  public XMLMaker ()
  {
    buffer_ = new StringBuilder ();
  }

  public void setEncoding (final String encoding)
  {
    encoding_ = encoding;
  }

  public void setDOM2 (final boolean dom2)
  {
    dom2_ = dom2;
  }

  public void setExpandEntityReference (final boolean expand)
  {
    expandEntityReference_ = expand;
  }

  public void setEmptyElementTag (final boolean empty)
  {
    emptyElementTag_ = empty;
  }

  public String getText ()
  {
    return (new String (buffer_));
  }

  public boolean enter (final Element element)
  {
    final String tag = element.getTagName ();
    buffer_.append ("<");
    buffer_.append (tag);
    final NamedNodeMap attrs = element.getAttributes ();
    final int nAttrs = attrs.getLength ();
    for (int i = 0; i < nAttrs; i++)
    {
      final Attr attr = (Attr) attrs.item (i);
      if (attr.getSpecified ())
      {
        buffer_.append (' ');
        enter (attr);
        leave (attr);
      }
    }
    buffer_.append (">");
    return (true);
  }

  public void leave (final Element element)
  {
    final String tag = element.getTagName ();
    buffer_.append ("</" + tag + ">");
  }

  public boolean enter (final Attr attr)
  {
    buffer_.append (attr.getName ());
    buffer_.append ("=\"");
    buffer_.append (UXML.escapeAttrQuot (attr.getValue ()));
    buffer_.append ('\"');
    return (true);
  }

  public void leave (final Attr attr)
  {
    // do nothing
  }

  public boolean enter (final Text text)
  {
    buffer_.append (UXML.escapeCharData (text.getData ()));
    return (true);
  }

  public void leave (final Text text)
  {
    // do nothing
  }

  public boolean enter (final CDATASection cdata)
  {
    buffer_.append ("<![CDATA[");
    buffer_.append (cdata.getData ());
    buffer_.append ("]]>");
    return (true);
  }

  public void leave (final CDATASection cdata)
  {
    // do nothing
  }

  public boolean enter (final EntityReference entityRef)
  {
    buffer_.append ("&");
    buffer_.append (entityRef.getNodeName ());
    buffer_.append (";");
    return (false);
  }

  public void leave (final EntityReference entityRef)
  {
    // do nothing
  }

  public boolean enter (final Entity entity)
  {
    final String name = entity.getNodeName ();
    final String pid = entity.getPublicId ();
    final String sid = entity.getSystemId ();
    final String notation = entity.getNotationName ();
    buffer_.append ("<!ENTITY ");
    buffer_.append (name);
    if (sid != null)
    {
      if (pid != null)
      {
        buffer_.append (" PUBLIC \"");
        buffer_.append (pid);
        buffer_.append ("\" \"");
        buffer_.append (UXML.escapeSystemQuot (sid));
        buffer_.append ("\">");
      }
      else
      {
        buffer_.append (" SYSTEM \"");
        buffer_.append (UXML.escapeSystemQuot (sid));
        buffer_.append ("\">");
      }
      if (notation != null)
      {
        buffer_.append (" NDATA ");
        buffer_.append (notation);
        buffer_.append (">");
      }
    }
    else
    {
      buffer_.append (" \"");
      final XMLMaker entityMaker = new XMLMaker ();
      UDOMVisitor.traverseChildren (entity, entityMaker);
      buffer_.append (UXML.escapeEntityQuot (entityMaker.getText ()));
      buffer_.append ("\"");
      buffer_.append (">");
    }
    return (false);
  }

  public void leave (final Entity entity)
  {
    // do nothing
  }

  public boolean enter (final ProcessingInstruction pi)
  {
    buffer_.append ("<?");
    buffer_.append (pi.getTarget ());
    buffer_.append (" ");
    buffer_.append (pi.getData ());
    buffer_.append ("?>");
    return (true);
  }

  public void leave (final ProcessingInstruction pi)
  {
    // do nothing
  }

  public boolean enter (final Comment comment)
  {
    buffer_.append ("<!--");
    buffer_.append (comment.getData ());
    buffer_.append ("-->");
    return (true);
  }

  public void leave (final Comment comment)
  {
    // do nothing
  }

  public boolean enter (final Document doc)
  {
    buffer_.append ("<?xml version=\"1.0\" encoding=\"");
    buffer_.append (encoding_);
    buffer_.append ("\" ?>\n");
    return (true);
  }

  public void leave (final Document doc)
  {
    // do nothing
  }

  public boolean enter (final DocumentType doctype)
  {
    if (dom2_)
    {
      final String name = doctype.getName ();
      final String publicId = doctype.getPublicId ();
      final String systemId = doctype.getSystemId ();
      final String internalSubset = doctype.getInternalSubset ();
      buffer_.append ("<!DOCTYPE ");
      buffer_.append (name);
      if (publicId != null)
      {
        buffer_.append (" PUBLIC \"");
        buffer_.append (publicId);
        buffer_.append ("\"");
      }
      if (systemId != null)
      {
        buffer_.append (" SYSTEM \"");
        buffer_.append (systemId);
        buffer_.append ("\"");
      }
      if (internalSubset != null)
      {
        buffer_.append (" [");
        buffer_.append (internalSubset);
        buffer_.append ("]");
      }
      buffer_.append (">\n");
      return (true);
    }

    {
      final String name = doctype.getName ();
      final NamedNodeMap entities = doctype.getEntities ();
      final NamedNodeMap notations = doctype.getNotations ();
      buffer_.append ("<!DOCTYPE ");
      buffer_.append (name);
      if (entities != null && entities.getLength () > 0 || notations != null && notations.getLength () > 0)
      {

        buffer_.append (" [");
        final int nEntities = entities.getLength ();
        for (int i = 0; i < nEntities; i++)
        {
          final XMLMaker entityMaker = new XMLMaker ();
          UDOMVisitor.traverse (entities.item (i), entityMaker);
          buffer_.append (entityMaker.getText ());
        }
        final int nNotations = notations.getLength ();
        for (int i = 0; i < nNotations; i++)
        {
          enter ((Notation) notations.item (i));
          leave ((Notation) notations.item (i));
        }
        buffer_.append ("]");
      }
      buffer_.append (">\n");
      return (true);
    }
  }

  public void leave (final DocumentType doctype)
  {
    // do nothing
  }

  public boolean enter (final DocumentFragment docfrag)
  {
    // do nothing
    return (true);
  }

  public void leave (final DocumentFragment docfrag)
  {
    // do nothing
  }

  public boolean enter (final Notation notation)
  {
    final String name = notation.getNodeName ();
    final String pid = notation.getPublicId ();
    final String sid = notation.getSystemId ();
    buffer_.append ("<!NOTATION ");
    buffer_.append (name);
    if (pid != null)
    {
      buffer_.append (" PUBLIC \"");
      buffer_.append (pid);
      buffer_.append ("\"");
      if (sid != null)
      {
        buffer_.append (" \"");
        buffer_.append (UXML.escapeSystemQuot (sid));
        buffer_.append ("\"");
      }
    }
    else
      if (sid != null)
      {
        buffer_.append (" SYSTEM \"");
        buffer_.append (UXML.escapeSystemQuot (sid));
        buffer_.append ("\"");
      }
    buffer_.append (">");
    return (true);
  }

  public void leave (final Notation notation)
  {
    // do nothing
  }

  public boolean enter (final Node node)
  {
    throw (new InternalError (node.toString ()));
  }

  public void leave (final Node node)
  {
    throw (new InternalError (node.toString ()));
  }

  public boolean isParsedEntity (final EntityReference entityRef)
  {
    final String name = entityRef.getNodeName ();
    final Document doc = entityRef.getOwnerDocument ();
    final DocumentType doctype = doc.getDoctype ();
    if (doctype == null)
    {
      return (false);
    }
    final NamedNodeMap entities = doctype.getEntities ();
    final Entity entity = (Entity) entities.getNamedItem (name);
    if (entity == null)
    {
      return (false);
    }
    return (entity.getNotationName () == null);
  }
}
