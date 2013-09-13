package com.thaiopensource.relaxng.edit;

import java.util.List;
import java.util.Vector;

public class DataPattern extends Pattern
{
  private String datatypeLibrary;
  private String type;
  private final List <Param> params = new Vector <Param> ();
  private Pattern except;

  public DataPattern (final String datatypeLibrary, final String type)
  {
    this.datatypeLibrary = datatypeLibrary;
    this.type = type;
  }

  public String getType ()
  {
    return type;
  }

  public void setType (final String type)
  {
    this.type = type;
  }

  public String getDatatypeLibrary ()
  {
    return datatypeLibrary;
  }

  public void setDatatypeLibrary (final String datatypeLibrary)
  {
    this.datatypeLibrary = datatypeLibrary;
  }

  public List <Param> getParams ()
  {
    return params;
  }

  public Pattern getExcept ()
  {
    return except;
  }

  public void setExcept (final Pattern except)
  {
    this.except = except;
  }

  @Override
  public <T> T accept (final PatternVisitor <T> visitor)
  {
    return visitor.visitData (this);
  }
}
