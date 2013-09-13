package com.thaiopensource.relaxng.edit;

import java.util.HashMap;
import java.util.Map;

public class ValuePattern extends Pattern
{
  private String datatypeLibrary;
  private String type;
  private String value;
  private final Map <String, String> prefixMap = new HashMap <String, String> ();

  public ValuePattern (final String datatypeLibrary, final String type, final String value)
  {
    this.datatypeLibrary = datatypeLibrary;
    this.type = type;
    this.value = value;
  }

  public String getDatatypeLibrary ()
  {
    return datatypeLibrary;
  }

  public void setDatatypeLibrary (final String datatypeLibrary)
  {
    this.datatypeLibrary = datatypeLibrary;
  }

  public String getType ()
  {
    return type;
  }

  public void setType (final String type)
  {
    this.type = type;
  }

  public String getValue ()
  {
    return value;
  }

  public void setValue (final String value)
  {
    this.value = value;
  }

  @Override
  public boolean mayContainText ()
  {
    return true;
  }

  public Map <String, String> getPrefixMap ()
  {
    return prefixMap;
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitValue (this);
  }
}
