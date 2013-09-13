package com.thaiopensource.resolver.catalog;

import java.io.IOException;
import java.util.List;

import org.apache.xml.resolver.Catalog;

import com.thaiopensource.resolver.AbstractResolver;
import com.thaiopensource.resolver.BasicResolver;
import com.thaiopensource.resolver.Identifier;
import com.thaiopensource.resolver.Input;
import com.thaiopensource.resolver.Resolver;
import com.thaiopensource.resolver.ResolverException;
import com.thaiopensource.resolver.xml.ExternalDTDSubsetIdentifier;
import com.thaiopensource.resolver.xml.ExternalEntityIdentifier;
import com.thaiopensource.resolver.xml.ExternalIdentifier;
import com.thaiopensource.resolver.xml.sax.SAXResolver;

/**
 * A Resolver that uses OASIS XML catalogs.
 */
public class CatalogResolver extends AbstractResolver
{
  private final Catalog catalog;
  private boolean catalogLoaded = false;
  private boolean hadCatalogError = false;

  // Allow somebody to customize in a different way, but still use our resolve
  // logic.
  public CatalogResolver (final Catalog catalog)
  {
    this.catalog = catalog;
  }

  public CatalogResolver (final List <String> catalogUris, final SAXResolver resolver)
  {
    this (new OasisCatalog (new SimpleCatalogManager (catalogUris), resolver));
  }

  public CatalogResolver (final List <String> catalogUris, final Resolver resolver)
  {
    this (catalogUris, new SAXResolver (resolver));
  }

  public CatalogResolver (final List <String> catalogUris)
  {
    this (catalogUris, new SAXResolver ());
  }

  @Override
  public synchronized void resolve (final Identifier id, final Input input) throws IOException, ResolverException
  {
    if (input.isResolved ())
      return;
    if (hadCatalogError)
      return;
    String absoluteUri = null;
    try
    {
      absoluteUri = BasicResolver.resolveUri (id);
      if (id.getUriReference ().equals (absoluteUri))
        absoluteUri = null;
    }
    catch (final ResolverException e)
    {
      // ignore
    }
    String resolved = null;
    final boolean isExternalIdentifier = (id instanceof ExternalIdentifier);
    try
    {
      if (!catalogLoaded)
      {
        catalogLoaded = true;
        catalog.loadSystemCatalogs ();
      }
      if (absoluteUri != null)
        resolved = isExternalIdentifier ? catalog.resolveSystem (absoluteUri) : catalog.resolveURI (absoluteUri);
      if (resolved == null)
      {
        if (!isExternalIdentifier)
          resolved = catalog.resolveURI (id.getUriReference ());
        else
          if (id instanceof ExternalEntityIdentifier)
          {
            final ExternalEntityIdentifier xid = (ExternalEntityIdentifier) id;
            resolved = catalog.resolveEntity (xid.getEntityName (), xid.getPublicId (), xid.getUriReference ());
          }
          else
            if (id instanceof ExternalDTDSubsetIdentifier)
            {
              final ExternalDTDSubsetIdentifier xid = (ExternalDTDSubsetIdentifier) id;
              resolved = catalog.resolveDoctype (xid.getDoctypeName (), xid.getPublicId (), xid.getUriReference ());
            }
            else
            {
              final ExternalIdentifier xid = (ExternalIdentifier) id;
              resolved = catalog.resolvePublic (xid.getPublicId (), xid.getUriReference ());
            }
      }
    }
    catch (final ResolverIOException e)
    {
      hadCatalogError = true;
      throw e.getResolverException ();
    }
    if (resolved != null)
      input.setUri (resolved);
  }
}
