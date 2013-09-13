package com.thaiopensource.xml.em;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileEntityManager extends EntityManager
{
  @Override
  public OpenEntity open (final ExternalId xid, final boolean isParameterEntity, final String entityName) throws IOException
  {
    final String systemId = xid.getSystemId ();
    File file = new File (systemId);
    if (!file.isAbsolute ())
    {
      final String baseUri = xid.getBaseUri ();
      if (baseUri != null)
      {
        final String dir = new File (baseUri).getParent ();
        if (dir != null)
          file = new File (dir, systemId);
      }
    }
    return openFile (file);
  }

  @Override
  public OpenEntity open (final String systemId) throws IOException
  {
    return openFile (new File (systemId));
  }

  private OpenEntity openFile (final File file) throws IOException
  {
    return detectEncoding (new FileInputStream (file), file.toString ());
  }

}
