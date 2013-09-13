package com.thaiopensource.relaxng.pattern;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.thaiopensource.relaxng.match.Matcher;
import com.thaiopensource.relaxng.sax.Context;
import com.thaiopensource.xml.util.Name;

/**
 * Test PatternMatcher.
 */
public class PatternMatcherTest extends SchemaPatternBuilder
{
  final SchemaPatternBuilder spb = new SchemaPatternBuilder ();
  static private final Name root = new Name ("", "root");
  static private final Map <String, HashSet <String>> EMPTY_MAP = Collections.emptyMap ();

  @DataProvider (name = "startTagPairs")
  Object [][] startTagPairs ()
  {
    final Name foo = new Name ("", "foo");
    final Name bar = new Name ("", "bar");
    final Set <Name> nameSet = new HashSet <Name> ();
    nameSet.add (foo);
    nameSet.add (bar);
    final NormalizedNameClass foobarNNC = new NormalizedNsNameClass (nameSet, EMPTY_MAP);
    final Locator loc = new LocatorImpl ();
    return new Object [] [] { { rootMatcher (makeChoice (makeElement (new SimpleNameClass (foo), makeEmpty (), loc),
                                                         makeElement (new SimpleNameClass (bar), makeEmpty (), loc))),
                               foobarNNC }

    };
  }

  @Test (dataProvider = "startTagPairs")
  public void testPossibleStartTagNames (final Matcher matcher, final NormalizedNameClass nnc)
  {
    Assert.assertEquals (matcher.possibleStartTagNames (), nnc);
  }

  private Matcher rootMatcher (final Pattern start)
  {
    final Matcher matcher = new PatternMatcher (start, new ValidatorPatternBuilder (this));
    Assert.assertTrue (matcher.matchStartDocument ());
    return matcher;
  }

  @DataProvider (name = "attributePairs")
  Object [][] attributePairs ()
  {
    final Name foo = new Name ("", "foo");
    final Name bar = new Name ("", "bar");
    final Set <Name> nameSet = new HashSet <Name> ();
    nameSet.add (foo);
    nameSet.add (bar);
    final NormalizedNameClass foobarNNC = new NormalizedNsNameClass (nameSet, EMPTY_MAP);
    final Locator loc = new LocatorImpl ();
    return new Object [] [] { { rootAttributeMatcher (makeElement (new SimpleNameClass (root),
                                                                   makeGroup (makeAttribute (new SimpleNameClass (foo),
                                                                                             makeText (),
                                                                                             loc),
                                                                              makeAttribute (new SimpleNameClass (bar),
                                                                                             makeText (),
                                                                                             loc)),
                                                                   loc)),
                               foobarNNC } };
  }

  private Matcher rootAttributeMatcher (final Pattern start)
  {
    final Matcher matcher = rootMatcher (start);
    Assert.assertTrue (matcher.matchStartTagOpen (root, "", new Context ()));
    return matcher;
  }

  @Test (dataProvider = "attributePairs")
  public void testPossibleAttributeNames (final Matcher matcher, final NormalizedNameClass nnc)
  {
    Assert.assertEquals (matcher.possibleAttributeNames (), nnc);
  }

  @DataProvider (name = "missingNamespacedAttribute")
  Object [][] missingNamespacedAttribute ()
  {
    final Name foo = new Name ("http://example.com/", "foo");
    final Locator loc = new LocatorImpl ();
    return new Object [] [] { { rootMissingNamespacedAttributeMatcher (makeElement (new SimpleNameClass (root),
                                                                                    makeAttribute (new SimpleNameClass (foo),
                                                                                                   makeText (),
                                                                                                   loc),
                                                                                    loc)) } };
  }

  private Matcher rootMissingNamespacedAttributeMatcher (final Pattern start)
  {
    final Matcher matcher = rootMatcher (start);
    // Declare the attribute namespace "http://example.com/" as default
    // namespace.
    final Context context = new Context ();
    try
    {
      context.startPrefixMapping ("", "http://example.com/");
    }
    catch (final SAXException e)
    {
      Assert.fail (e.getMessage (), e);
    }
    // Start the root element
    Assert.assertTrue (matcher.matchStartTagOpen (root, "", context));
    // Close the root element, we should get the required attribute missing
    // error.
    Assert.assertFalse (matcher.matchStartTagClose (root, "", context));
    return matcher;
  }

  @Test (dataProvider = "missingNamespacedAttribute")
  public void testErrorMessageAttributeNames (final Matcher matcher)
  {
    // Before fixing issue 105 the error message was
    // element "root" missing required attribute "foo"
    // Now we should get the correct namespace for the missing attribute:
    Assert.assertEquals (matcher.getErrorMessage (),
                         "element \"root\" missing required attribute \"ns:foo\" (with xmlns:ns=\"http://example.com/\")");
  }
}
