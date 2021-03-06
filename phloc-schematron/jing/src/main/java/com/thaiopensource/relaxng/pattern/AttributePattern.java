package com.thaiopensource.relaxng.pattern;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class AttributePattern extends Pattern
{
  private final NameClass nameClass;
  private final Pattern p;
  private final Locator loc;

  AttributePattern (final NameClass nameClass, final Pattern value, final Locator loc)
  {
    super (false, EMPTY_CONTENT_TYPE, combineHashCode (ATTRIBUTE_HASH_CODE, nameClass.hashCode (), value.hashCode ()));
    this.nameClass = nameClass;
    this.p = value;
    this.loc = loc;
  }

  @Override
  Pattern expand (final SchemaPatternBuilder b)
  {
    final Pattern ep = p.expand (b);
    if (ep != p)
      return b.makeAttribute (nameClass, ep, loc);
    else
      return this;
  }

  @Override
  void checkRestrictions (final int context, final DuplicateAttributeDetector dad, final Alphabet alpha) throws RestrictionViolationException
  {
    switch (context)
    {
      case START_CONTEXT:
        throw new RestrictionViolationException ("start_contains_attribute");
      case ELEMENT_CONTEXT:
        if (nameClass.isOpen ())
          throw new RestrictionViolationException ("open_name_class_not_repeated");
        break;
      case ELEMENT_REPEAT_GROUP_CONTEXT:
        throw new RestrictionViolationException ("one_or_more_contains_group_contains_attribute");
      case ELEMENT_REPEAT_INTERLEAVE_CONTEXT:
        throw new RestrictionViolationException ("one_or_more_contains_interleave_contains_attribute");
      case LIST_CONTEXT:
        throw new RestrictionViolationException ("list_contains_attribute");
      case ATTRIBUTE_CONTEXT:
        throw new RestrictionViolationException ("attribute_contains_attribute");
      case DATA_EXCEPT_CONTEXT:
        throw new RestrictionViolationException ("data_except_contains_attribute");
    }
    dad.addAttribute (nameClass);
    try
    {
      p.checkRestrictions (ATTRIBUTE_CONTEXT, null, null);
    }
    catch (final RestrictionViolationException e)
    {
      e.maybeSetLocator (loc);
      throw e;
    }
  }

  @Override
  boolean samePattern (final Pattern other)
  {
    if (!(other instanceof AttributePattern))
      return false;
    final AttributePattern ap = (AttributePattern) other;
    return nameClass.equals (ap.nameClass) && p == ap.p;
  }

  @Override
  void checkRecursion (final int depth) throws SAXException
  {
    p.checkRecursion (depth);
  }

  @Override
  <T> T apply (final PatternFunction <T> f)
  {
    return f.caseAttribute (this);
  }

  Pattern getContent ()
  {
    return p;
  }

  NameClass getNameClass ()
  {
    return nameClass;
  }

  Locator getLocator ()
  {
    return loc;
  }
}
