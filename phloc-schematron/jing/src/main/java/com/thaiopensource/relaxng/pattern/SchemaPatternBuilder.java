package com.thaiopensource.relaxng.pattern;

import java.util.List;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

import com.thaiopensource.xml.util.Name;

public class SchemaPatternBuilder extends PatternBuilder
{
  private boolean idTypes;
  private final UnexpandedNotAllowedPattern unexpandedNotAllowed = new UnexpandedNotAllowedPattern ();
  private final TextPattern text = new TextPattern ();
  private final PatternInterner schemaInterner = new PatternInterner ();

  public SchemaPatternBuilder ()
  {}

  public boolean hasIdTypes ()
  {
    return idTypes;
  }

  Pattern makeElement (final NameClass nameClass, final Pattern content, final Locator loc)
  {
    final Pattern p = new ElementPattern (nameClass, content, loc);
    return schemaInterner.intern (p);
  }

  Pattern makeAttribute (final NameClass nameClass, final Pattern value, final Locator loc)
  {
    if (value == notAllowed)
      return value;
    final Pattern p = new AttributePattern (nameClass, value, loc);
    return schemaInterner.intern (p);
  }

  Pattern makeData (final Datatype dt, final Name dtName, final List <String> params)
  {
    noteDatatype (dt);
    final Pattern p = new DataPattern (dt, dtName, params);
    return schemaInterner.intern (p);
  }

  Pattern makeDataExcept (final Datatype dt,
                          final Name dtName,
                          final List <String> params,
                          final Pattern except,
                          final Locator loc)
  {
    noteDatatype (dt);
    final Pattern p = new DataExceptPattern (dt, dtName, params, except, loc);
    return schemaInterner.intern (p);
  }

  Pattern makeValue (final Datatype dt, final Name dtName, final Object value, final String stringValue)
  {
    noteDatatype (dt);
    final Pattern p = new ValuePattern (dt, dtName, value, stringValue);
    return schemaInterner.intern (p);
  }

  Pattern makeText ()
  {
    return text;
  }

  @Override
  Pattern makeOneOrMore (final Pattern p)
  {
    if (p == text)
      return p;
    return super.makeOneOrMore (p);
  }

  Pattern makeUnexpandedNotAllowed ()
  {
    return unexpandedNotAllowed;
  }

  Pattern makeError ()
  {
    final Pattern p = new ErrorPattern ();
    return schemaInterner.intern (p);
  }

  @Override
  Pattern makeChoice (final Pattern p1, final Pattern p2)
  {
    if (p1 == notAllowed || p1 == p2)
      return p2;
    if (p2 == notAllowed)
      return p1;
    return super.makeChoice (p1, p2);
  }

  Pattern makeList (final Pattern p, final Locator loc)
  {
    if (p == notAllowed)
      return p;
    final Pattern p1 = new ListPattern (p, loc);
    return schemaInterner.intern (p1);
  }

  Pattern makeMixed (final Pattern p)
  {
    return makeInterleave (text, p);
  }

  private void noteDatatype (final Datatype dt)
  {
    if (dt.getIdType () != Datatype.ID_TYPE_NULL)
      idTypes = true;
  }
}
