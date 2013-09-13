package com.thaiopensource.xml.em;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.thaiopensource.xml.util.EncodingMap;

/**
 * This class is used by the parser to access external entities.
 */
public abstract class EntityManager
{
  /**
   * Opens an external entity with the specified external identifier.
   */
  public abstract OpenEntity open (ExternalId xid, boolean isParameterEntity, String entityName) throws IOException;

  /**
   * Open the top-level entity.
   * 
   * @param systemId
   * @return
   * @throws IOException
   */
  public abstract OpenEntity open (String systemId) throws IOException;

  protected OpenEntity detectEncoding (final InputStream input, final String systemId) throws IOException
  {
    final EncodingDetectInputStream in = new EncodingDetectInputStream (input);
    final String enc = in.detectEncoding ();
    final String javaEnc = EncodingMap.getJavaName (enc);
    return new OpenEntity (new BufferedReader (new InputStreamReader (in, javaEnc)), systemId, systemId, enc);
  }
}
