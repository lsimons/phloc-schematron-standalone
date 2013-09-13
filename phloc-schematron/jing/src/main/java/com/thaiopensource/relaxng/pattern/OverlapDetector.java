package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class OverlapDetector implements NameClassVisitor
{
  private final NameClass nc1;
  private final NameClass nc2;
  private Name overlapExample = null;

  private static final String IMPOSSIBLE = "\u0000";

  private OverlapDetector (final NameClass nc1, final NameClass nc2)
  {
    this.nc1 = nc1;
    this.nc2 = nc2;
    nc1.accept (this);
    nc2.accept (this);
  }

  private void probe (final Name name)
  {
    if (nc1.contains (name) && nc2.contains (name))
      overlapExample = name;
  }

  public void visitChoice (final NameClass nc1, final NameClass nc2)
  {
    nc1.accept (this);
    nc2.accept (this);
  }

  public void visitNsName (final String ns)
  {
    probe (new Name (ns, IMPOSSIBLE));
  }

  public void visitNsNameExcept (final String ns, final NameClass ex)
  {
    probe (new Name (ns, IMPOSSIBLE));
    ex.accept (this);
  }

  public void visitAnyName ()
  {
    probe (new Name (IMPOSSIBLE, IMPOSSIBLE));
  }

  public void visitAnyNameExcept (final NameClass ex)
  {
    probe (new Name (IMPOSSIBLE, IMPOSSIBLE));
    ex.accept (this);
  }

  public void visitName (final Name name)
  {
    probe (name);
  }

  public void visitNull ()
  {}

  public void visitError ()
  {}

  static void checkOverlap (final NameClass nc1,
                            final NameClass nc2,
                            final String messageForName,
                            final String messageForNs,
                            final String messageForOther) throws RestrictionViolationException
  {
    if (nc2 instanceof SimpleNameClass)
    {
      final SimpleNameClass snc = (SimpleNameClass) nc2;
      if (nc1.contains (snc.getName ()))
        throw new RestrictionViolationException (messageForName, snc.getName ());
    }
    else
      if (nc1 instanceof SimpleNameClass)
      {
        final SimpleNameClass snc = (SimpleNameClass) nc1;
        if (nc2.contains (snc.getName ()))
          throw new RestrictionViolationException (messageForName, snc.getName ());
      }
      else
      {
        final Name name = new OverlapDetector (nc1, nc2).overlapExample;
        if (name != null)
        {
          final String localName = name.getLocalName ();
          if (localName == IMPOSSIBLE)
          {
            final String ns = name.getNamespaceUri ();
            if (ns == IMPOSSIBLE)
              throw new RestrictionViolationException (messageForOther);
            else
              throw new RestrictionViolationException (messageForNs, ns);
          }
          else
            throw new RestrictionViolationException (messageForName, name);
        }
      }
  }
}
