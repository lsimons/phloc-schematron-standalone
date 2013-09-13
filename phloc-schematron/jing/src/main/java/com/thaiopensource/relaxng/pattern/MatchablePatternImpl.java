package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.relaxng.match.MatchablePattern;
import com.thaiopensource.relaxng.match.Matcher;

public class MatchablePatternImpl implements MatchablePattern
{
  private final SchemaPatternBuilder spb;
  private final Pattern start;

  public MatchablePatternImpl (final SchemaPatternBuilder spb, final Pattern start)
  {
    this.spb = spb;
    this.start = start;
  }

  public Matcher createMatcher ()
  {
    return new PatternMatcher (start, new ValidatorPatternBuilder (spb));
  }

}
