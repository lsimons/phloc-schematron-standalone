package com.thaiopensource.relaxng.jarv;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.pattern.Pattern;
import com.thaiopensource.relaxng.pattern.ValidatorPatternBuilder;
import com.thaiopensource.xml.sax.CountingErrorHandler;

class VerifierImpl extends org.iso_relax.verifier.impl.VerifierImpl
{
  private final VerifierHandlerImpl vhi;
  private boolean needReset = false;

  VerifierImpl (final Pattern start, final ValidatorPatternBuilder builder) throws VerifierConfigurationException
  {
    vhi = new VerifierHandlerImpl (start, builder, new CountingErrorHandler (errorHandler));
    reader.setDTDHandler (vhi);
  }

  @Override
  public VerifierHandler getVerifierHandler () throws SAXException
  {
    if (needReset)
      vhi.reset ();
    else
      needReset = true;
    return vhi;
  }

  @Override
  public void setErrorHandler (final ErrorHandler handler)
  {
    vhi.setErrorHandler (handler);
    super.setErrorHandler (handler);
  }

}
