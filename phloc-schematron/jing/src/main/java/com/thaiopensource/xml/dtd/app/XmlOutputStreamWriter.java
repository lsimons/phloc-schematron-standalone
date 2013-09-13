package com.thaiopensource.xml.dtd.app;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import com.thaiopensource.xml.out.CharRepertoire;
import com.thaiopensource.xml.out.XmlWriter;
import com.thaiopensource.xml.util.EncodingMap;

public class XmlOutputStreamWriter extends XmlWriter
{
  public XmlOutputStreamWriter (final OutputStream out, final String enc) throws UnsupportedEncodingException
  {
    this (EncodingMap.getJavaName (enc), out);
  }

  private XmlOutputStreamWriter (final String jEnc, final OutputStream out) throws UnsupportedEncodingException
  {
    super (new BufferedWriter (new OutputStreamWriter (out, jEnc)), CharRepertoire.getInstance (jEnc));
  }
}
