package com.thaiopensource.relaxng.translate.test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.xml.sax.AbstractLexicalHandler;

public class Compare
{
  static public boolean compare (final File file1, final File file2, final SAXResolver resolver) throws SAXException,
                                                                                                IOException
  {
    return load (resolver, file1).equals (load (resolver, file2));
  }

  static private List <Event> load (final SAXResolver resolver, final File file) throws SAXException, IOException
  {
    final InputSource in = new InputSource (UriOrFile.fileToUri (file));
    final Saver saver = new Saver ();
    final XMLReader xr = resolver.createXMLReader ();

    try
    {
      xr.setFeature ("http://xml.org/sax/features/namespace-prefixes", true);
    }
    catch (final SAXNotRecognizedException e)
    {
      throw new SAXException ("support for namespaces-prefixes feature required");
    }
    catch (final SAXNotSupportedException e)
    {
      throw new SAXException ("support for namespaces-prefixes feature required");
    }
    xr.setContentHandler (saver);
    try
    {
      xr.setProperty ("http://xml.org/sax/properties/lexical-handler", new CommentSaver (saver));
    }
    catch (final SAXNotRecognizedException e)
    {}
    catch (final SAXNotSupportedException e)
    {}
    xr.parse (in);
    return saver.getEventList ();
  }

  static abstract class Event
  {
    boolean merge (final char [] chars, final int start, final int count)
    {
      return false;
    }

    boolean isWhitespace ()
    {
      return false;
    }
  }

  static class StartElement extends Event
  {
    private final String qName;

    StartElement (final String qName)
    {
      this.qName = qName;
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof StartElement))
        return false;
      return qName.equals (((StartElement) obj).qName);
    }
  }

  static class Attribute extends Event
  {
    private final String qName;
    private final String value;

    Attribute (final String qName, final String value)
    {
      this.qName = qName;
      this.value = value;
    }

    String getQName ()
    {
      return qName;
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof Attribute))
        return false;
      final Attribute other = (Attribute) obj;
      return qName.equals (other.qName) && value.equals (other.value);
    }
  }

  private static class EndElement extends Event
  {
    @Override
    public boolean equals (final Object obj)
    {
      return obj instanceof EndElement;
    }
  }

  static class Comment extends Event
  {
    private final String value;

    Comment (final String value)
    {
      this.value = value;
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof Comment))
        return false;
      return value.equals (((Comment) obj).value);
    }
  }

  static class Text extends Event
  {
    private String value;

    Text (final String value)
    {
      this.value = value;
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof Text))
        return false;
      return value.equals (((Text) obj).value);
    }

    @Override
    boolean isWhitespace ()
    {
      for (int i = 0, len = value.length (); i < len; i++)
      {
        switch (value.charAt (i))
        {
          case '\r':
          case '\n':
          case '\t':
          case ' ':
            break;
          default:
            return false;
        }
      }
      return true;
    }

    @Override
    boolean merge (final char [] chars, final int start, final int count)
    {
      final StringBuffer buf = new StringBuffer (value);
      buf.append (chars, start, count);
      value = buf.toString ();
      return true;
    }
  }

  static class Saver extends DefaultHandler
  {
    private final List <Event> eventList = new Vector <Event> ();
    private final List <Attribute> attributeList = new Vector <Attribute> ();

    List <Event> getEventList ()
    {
      return eventList;
    }

    void flushWhitespace (final boolean endElement)
    {
      final int len = eventList.size ();
      if (len == 0)
        return;
      if (!(eventList.get (len - 1)).isWhitespace ())
        return;
      if (endElement && len > 1 && eventList.get (len - 2) instanceof StartElement)
        return;
      eventList.remove (len - 1);
    }

    @Override
    public void startElement (final String ns, final String localName, final String qName, final Attributes attributes)
    {
      flushWhitespace (false);
      eventList.add (new StartElement (qName));
      for (int i = 0, len = attributes.getLength (); i < len; i++)
        attributeList.add (new Attribute (attributes.getQName (i), attributes.getValue (i)));
      Collections.sort (attributeList, new Comparator <Attribute> ()
      {
        public int compare (final Attribute a1, final Attribute a2)
        {
          return a1.getQName ().compareTo (a2.getQName ());
        }
      });
      eventList.addAll (attributeList);
      attributeList.clear ();
    }

    @Override
    public void endElement (final String ns, final String localName, final String qName)
    {
      flushWhitespace (true);
      eventList.add (new EndElement ());
    }

    @Override
    public void characters (final char [] chars, final int start, final int length)
    {
      final int len = eventList.size ();
      if (len == 0 || !(eventList.get (len - 1)).merge (chars, start, length))
        eventList.add (new Text (new String (chars, start, length)));
    }

    @Override
    public void ignorableWhitespace (final char [] chars, final int start, final int length)
    {
      characters (chars, start, length);
    }

    @Override
    public void endDocument ()
    {
      flushWhitespace (false);
    }

    void comment (final String value)
    {
      flushWhitespace (false);
      eventList.add (new Comment (value));
    }
  }

  static class CommentSaver extends AbstractLexicalHandler
  {
    private final Saver saver;

    CommentSaver (final Saver saver)
    {
      this.saver = saver;
    }

    @Override
    public void comment (final char [] chars, final int start, final int length) throws SAXException
    {
      saver.comment (new String (chars, start, length));
    }
  }

  static public void main (final String [] args) throws SAXException, IOException
  {
    System.err.println (compare (new File (args[0]), new File (args[1]), new SAXResolver ()));
  }
}
