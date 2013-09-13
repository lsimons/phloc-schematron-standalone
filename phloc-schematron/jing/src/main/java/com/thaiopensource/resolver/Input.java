package com.thaiopensource.resolver;

import java.io.InputStream;
import java.io.Reader;

/**
 *
 */
public class Input
{
  private String uri;
  private String encoding;
  private InputStream byteStream;
  private Reader characterStream;

  // XXX add media type

  public Input ()
  {}

  public String getUri ()
  {
    return uri;
  }

  public void setUri (final String uri)
  {
    this.uri = uri;
  }

  public String getEncoding ()
  {
    return encoding;
  }

  public void setEncoding (final String encoding)
  {
    this.encoding = encoding;
  }

  public InputStream getByteStream ()
  {
    return byteStream;
  }

  public void setByteStream (final InputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  public Reader getCharacterStream ()
  {
    return characterStream;
  }

  public void setCharacterStream (final Reader charStream)
  {
    this.characterStream = charStream;
  }

  public boolean isResolved ()
  {
    return isOpen () || uri != null;
  }

  public boolean isOpen ()
  {
    return byteStream != null || characterStream != null;
  }

  public boolean isUriDefinitive ()
  {
    return !isOpen () && uri != null;
  }
}
