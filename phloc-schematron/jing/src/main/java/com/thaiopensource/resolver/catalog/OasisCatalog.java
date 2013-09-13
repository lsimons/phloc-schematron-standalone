package com.thaiopensource.resolver.catalog;

import java.io.IOException;
import java.net.URL;

import javax.xml.transform.sax.SAXSource;

import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.readers.OASISXMLCatalogReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.resolver.xml.XMLDocumentIdentifier;
import com.thaiopensource.resolver.xml.sax.SAXResolver;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

/**
 * A catalog with customized parsing of catalog files. In particular, it only
 * supports OASIS XML Catalogs and it uses a SAXResolver for access to the
 * catalog URIs.
 */
class OasisCatalog extends Catalog
{
  private final SAXResolver saxResolver;

  OasisCatalog (final CatalogManager catalogManager, final SAXResolver saxResolver)
  {
    super (catalogManager);
    this.saxResolver = saxResolver;
    // don't call setupReaders; since we use our own parseCatalogFile
    // we'll load the catalogs lazily
  }

  @SuppressWarnings ("sync-override")
  @Override
  protected void parseCatalogFile (final String uri) throws IOException
  {
    final OASISXMLCatalogReader catalogReader = new OASISXMLCatalogReader ();
    try
    {
      final SAXSource source = saxResolver.resolve (new XMLDocumentIdentifier (uri,
                                                                               null,
                                                                               OASISXMLCatalogReader.namespaceName));
      String systemId = source.getInputSource ().getSystemId ();
      if (systemId == null)
        systemId = uri;
      base = new URL (systemId);
      catalogReader.setCatalog (this);
      final XMLReader xmlReader = source.getXMLReader ();
      xmlReader.setEntityResolver (new CatalogEntityResolver (xmlReader.getEntityResolver ()));
      xmlReader.setContentHandler (catalogReader);
      xmlReader.setErrorHandler (new DraconianErrorHandler ());
      xmlReader.parse (source.getInputSource ());
    }
    catch (final SAXException e)
    {
      final Exception wrapped = e.getException ();
      // this will get unwrapped by CatalogResolver
      throw new ResolverIOException (wrapped instanceof ResolverException ? (ResolverException) wrapped
                                                                         : new ResolverException (e));
    }
  }
}
