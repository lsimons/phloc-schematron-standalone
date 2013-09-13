package com.thaiopensource.validate.schematron;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.lib.FeatureKeys;

public class NewSaxonSchemaReaderFactory extends SchematronSchemaReaderFactory
{
  @Override
  public SAXTransformerFactory newTransformerFactory ()
  {
    return new TransformerFactoryImpl ();
  }

  @Override
  public void initTransformerFactory (final TransformerFactory factory)
  {
    try
    {
      factory.setAttribute (FeatureKeys.XSLT_VERSION, "2.0");
    }
    catch (final IllegalArgumentException e)
    {
      // The old Saxon 9 (pre HE/PE/EE) throws this exception.
    }
    factory.setAttribute (FeatureKeys.LINE_NUMBERING, Boolean.TRUE);
    factory.setAttribute (FeatureKeys.VERSION_WARNING, Boolean.FALSE);
  }
}
