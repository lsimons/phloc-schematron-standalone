package com.thaiopensource.xml.infer;

import java.util.HashMap;
import java.util.Map;

import com.thaiopensource.xml.util.Name;

public class Schema
{
  private final Map <Name, ElementDecl> elementDecls = new HashMap <Name, ElementDecl> ();
  private Particle start;
  private final Map <String, String> prefixMap = new HashMap <String, String> ();

  public Map <Name, ElementDecl> getElementDecls ()
  {
    return elementDecls;
  }

  public Map <String, String> getPrefixMap ()
  {
    return prefixMap;
  }

  public ElementDecl getElementDecl (final Name name)
  {
    return elementDecls.get (name);
  }

  public Particle getStart ()
  {
    return start;
  }

  public void setStart (final Particle start)
  {
    this.start = start;
  }
}
