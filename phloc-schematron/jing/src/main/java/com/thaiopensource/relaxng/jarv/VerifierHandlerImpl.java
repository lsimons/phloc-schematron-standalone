package com.thaiopensource.relaxng.jarv;

import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.relaxng.sax.PatternValidator;
import com.thaiopensource.xml.sax.CountingErrorHandler;

class VerifierHandlerImpl extends PatternValidator implements VerifierHandler
{
  private boolean complete = false;
  private final CountingErrorHandler ceh;

  VerifierHandlerImpl (final Pattern pattern, final ValidatorPatternBuilder builder, final CountingErrorHandler ceh)
  {
    super (pattern, builder, ceh);
    this.ceh = ceh;
  }

  @Override
  public void endDocument () throws SAXException
  {
    super.endDocument ();
    complete = true;
  }

  public boolean isValid () throws IllegalStateException
  {
    if (!complete)
      throw new IllegalStateException ();
    return !ceh.getHadErrorOrFatalError ();
  }

  void setErrorHandler (final ErrorHandler eh)
  {
    ceh.setErrorHandler (eh);
  }

  @Override
  public void reset ()
  {
    super.reset ();
    if (ceh != null)
      ceh.reset ();
  }
}
