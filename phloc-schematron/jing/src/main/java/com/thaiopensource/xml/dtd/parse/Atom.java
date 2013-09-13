package com.thaiopensource.xml.dtd.parse;

class Atom
{
  private final int tokenType;
  private final String token;
  private final Entity entity;

  Atom (final Entity entity)
  {
    this.entity = entity;
    this.tokenType = -1;
    this.token = null;
  }

  Atom (final int tokenType, final String token)
  {
    this.tokenType = tokenType;
    this.token = token;
    this.entity = null;
  }

  Atom (final int tokenType, final String token, final Entity entity)
  {
    this.tokenType = tokenType;
    this.token = token;
    this.entity = entity;
  }

  final int getTokenType ()
  {
    return tokenType;
  }

  final String getToken ()
  {
    return token;
  }

  final Entity getEntity ()
  {
    return entity;
  }

  @Override
  public int hashCode ()
  {
    return token.hashCode ();
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof Atom))
      return false;
    final Atom other = (Atom) obj;
    if (this.entity != null)
      return this.entity == other.entity;
    else
      return this.tokenType == other.tokenType && this.token.equals (other.token);
  }
}
