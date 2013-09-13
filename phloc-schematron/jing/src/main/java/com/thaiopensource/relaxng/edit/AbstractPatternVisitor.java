package com.thaiopensource.relaxng.edit;

public abstract class AbstractPatternVisitor <T> implements PatternVisitor <T>
{
  public T visitElement (final ElementPattern p)
  {
    return visitNameClassed (p);
  }

  public T visitAttribute (final AttributePattern p)
  {
    return visitNameClassed (p);
  }

  public T visitOneOrMore (final OneOrMorePattern p)
  {
    return visitUnary (p);
  }

  public T visitZeroOrMore (final ZeroOrMorePattern p)
  {
    return visitUnary (p);
  }

  public T visitOptional (final OptionalPattern p)
  {
    return visitUnary (p);
  }

  public T visitInterleave (final InterleavePattern p)
  {
    return visitComposite (p);
  }

  public T visitGroup (final GroupPattern p)
  {
    return visitComposite (p);
  }

  public T visitChoice (final ChoicePattern p)
  {
    return visitComposite (p);
  }

  public T visitGrammar (final GrammarPattern p)
  {
    return visitPattern (p);
  }

  public T visitExternalRef (final ExternalRefPattern p)
  {
    return visitPattern (p);
  }

  public T visitRef (final RefPattern p)
  {
    return visitPattern (p);
  }

  public T visitParentRef (final ParentRefPattern p)
  {
    return visitPattern (p);
  }

  public T visitValue (final ValuePattern p)
  {
    return visitPattern (p);
  }

  public T visitData (final DataPattern p)
  {
    return visitPattern (p);
  }

  public T visitMixed (final MixedPattern p)
  {
    return visitUnary (p);
  }

  public T visitList (final ListPattern p)
  {
    return visitUnary (p);
  }

  public T visitText (final TextPattern p)
  {
    return visitPattern (p);
  }

  public T visitEmpty (final EmptyPattern p)
  {
    return visitPattern (p);
  }

  public T visitNotAllowed (final NotAllowedPattern p)
  {
    return visitPattern (p);
  }

  public T visitNameClassed (final NameClassedPattern p)
  {
    return visitUnary (p);
  }

  public T visitUnary (final UnaryPattern p)
  {
    return visitPattern (p);
  }

  public T visitComposite (final CompositePattern p)
  {
    return visitPattern (p);
  }

  public abstract T visitPattern (Pattern p);
}
