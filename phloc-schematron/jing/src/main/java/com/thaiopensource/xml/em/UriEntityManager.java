package com.thaiopensource.xml.em;

import java.io.IOException;
import java.net.URL;

public class UriEntityManager extends EntityManager
{
  @Override
  public OpenEntity open (final ExternalId xid, final boolean isParameterEntity, final String entityName) throws IOException
  {
    final String systemId = xid.getSystemId ();
    final String baseUri = xid.getBaseUri ();
    URL u;
    if (baseUri != null)
      u = new URL (new URL (baseUri), systemId);
    else
      u = new URL (systemId);
    return open (u);
  }

  @Override
  public OpenEntity open (final String uri) throws IOException
  {
    return open (new URL (uri));
  }

  private OpenEntity open (final URL u) throws IOException
  {
    return detectEncoding (u.openStream (), u.toString ());
  }
}
