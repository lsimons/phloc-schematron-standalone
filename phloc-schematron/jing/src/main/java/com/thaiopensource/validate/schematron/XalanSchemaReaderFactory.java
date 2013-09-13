package com.thaiopensource.validate.schematron;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;

import org.apache.xalan.processor.TransformerFactoryImpl;

public class XalanSchemaReaderFactory extends SchematronSchemaReaderFactory
{
  @Override
  public SAXTransformerFactory newTransformerFactory ()
  {
    return new TransformerFactoryImpl ();
  }

  @Override
  public void initTransformerFactory (final TransformerFactory factory)
  {
    factory.setAttribute (TransformerFactoryImpl.FEATURE_SOURCE_LOCATION, Boolean.TRUE);
  }
}
