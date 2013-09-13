package com.thaiopensource.validation;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * A straightforward default implementation of LSInput.
 * 
 * @see LSInput
 */
public class LSInputImpl implements LSInput
{
  private Reader characterStream;
  private InputStream byteStream;
  private String systemId;
  private String publicId;
  private String baseURI;
  private String encoding;
  private boolean certifiedText;
  private String stringData;

  public Reader getCharacterStream ()
  {
    return characterStream;
  }

  public void setCharacterStream (final Reader characterStream)
  {
    this.characterStream = characterStream;
  }

  public InputStream getByteStream ()
  {
    return byteStream;
  }

  public void setByteStream (final InputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  public String getSystemId ()
  {
    return systemId;
  }

  public void setSystemId (final String systemId)
  {
    this.systemId = systemId;
  }

  public String getPublicId ()
  {
    return publicId;
  }

  public void setPublicId (final String publicId)
  {
    this.publicId = publicId;
  }

  public String getBaseURI ()
  {
    return baseURI;
  }

  public void setBaseURI (final String baseURI)
  {
    this.baseURI = baseURI;
  }

  public String getEncoding ()
  {
    return encoding;
  }

  public void setEncoding (final String encoding)
  {
    this.encoding = encoding;
  }

  public boolean getCertifiedText ()
  {
    return certifiedText;
  }

  public void setCertifiedText (final boolean certifiedText)
  {
    this.certifiedText = certifiedText;
  }

  public String getStringData ()
  {
    return stringData;
  }

  public void setStringData (final String stringData)
  {
    this.stringData = stringData;
  }
}
