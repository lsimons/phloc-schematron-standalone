package com.thaiopensource.resolver;

/**
 *
 */
public class MediaTypedIdentifier extends Identifier
{
  private final String mediaType;

  public MediaTypedIdentifier (final String href, final String base, final String mediaType)
  {
    super (href, base);
    this.mediaType = mediaType;
  }

  @Override
  public String getMediaType ()
  {
    return mediaType;
  }
}
