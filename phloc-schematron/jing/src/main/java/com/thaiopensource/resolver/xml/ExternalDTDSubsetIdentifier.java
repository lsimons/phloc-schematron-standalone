package com.thaiopensource.resolver.xml;

/**
 *
 */
public class ExternalDTDSubsetIdentifier extends ExternalIdentifier
{
  private final String doctypeName;

  public ExternalDTDSubsetIdentifier (final String href,
                                      final String base,
                                      final String publicId,
                                      final String doctypeName)
  {
    super (href, base, publicId);
    this.doctypeName = doctypeName;
  }

  public String getDoctypeName ()
  {
    return doctypeName;
  }
}
