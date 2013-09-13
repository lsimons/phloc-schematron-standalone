package com.thaiopensource.resolver.catalog;

import java.util.List;
import java.util.Vector;

import org.apache.xml.resolver.CatalogManager;

/**
 * A very simple CatalogManager that does not use use property file/system
 * property customization.
 */
class SimpleCatalogManager extends CatalogManager
{
  private final Vector <String> catalogUris;

  SimpleCatalogManager (final List <String> catalogUris)
  {
    this.catalogUris = new Vector <String> ();
    this.catalogUris.addAll (catalogUris);
    // disable printing to System.out
    setVerbosity (0);
  }

  @Override
  public Vector <String> getCatalogFiles ()
  {
    return catalogUris;
  }

  @Override
  public boolean getRelativeCatalogs ()
  {
    return false;
  }

  @Override
  public boolean getPreferPublic ()
  {
    return true;
  }

  @Override
  public boolean getIgnoreMissingProperties ()
  {
    return true;
  }

  @Override
  public boolean getAllowOasisXMLCatalogPI ()
  {
    return false;
  }

  @Override
  public boolean getUseStaticCatalog ()
  {
    return false;
  }

  @Override
  public String getCatalogClassName ()
  {
    return null;
  }
}
