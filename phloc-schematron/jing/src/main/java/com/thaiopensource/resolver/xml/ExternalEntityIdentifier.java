package com.thaiopensource.resolver.xml;

/**
 *
 */
public class ExternalEntityIdentifier extends ExternalIdentifier
{
  private final String entityName;

  /**
   * @param href
   * @param base
   * @param publicId
   *        maybe null
   * @param entityName
   *        starts with a % for a parameter entity, may be null
   */
  public ExternalEntityIdentifier (final String href, final String base, final String publicId, final String entityName)
  {
    super (href, base, publicId);
    this.entityName = entityName;
  }

  public String getEntityName ()
  {
    return entityName;
  }
}
