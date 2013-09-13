package com.thaiopensource.xml.dtd.om;

import com.thaiopensource.xml.em.ExternalId;

public class ExternalIdRef extends TopLevel
{

  private final String name;
  private final ExternalId externalId;
  private final String uri;
  private final String encoding;
  private final TopLevel [] contents;

  public ExternalIdRef (final String name,
                        final ExternalId externalId,
                        final String uri,
                        final String encoding,
                        final TopLevel [] contents)
  {
    this.name = name;
    this.externalId = externalId;
    this.uri = uri;
    this.encoding = encoding;
    this.contents = contents;
  }

  @Override
  public int getType ()
  {
    return EXTERNAL_ID_REF;
  }

  public String getName ()
  {
    return name;
  }

  public ExternalId getExternalId ()
  {
    return externalId;
  }

  public String getUri ()
  {
    return uri;
  }

  public String getEncoding ()
  {
    return encoding;
  }

  public TopLevel [] getContents ()
  {
    final TopLevel [] tem = new TopLevel [contents.length];
    System.arraycopy (contents, 0, tem, 0, contents.length);
    return tem;
  }

  @Override
  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    visitor.externalIdRef (name, externalId, uri, encoding, getContents ());
  }

}
