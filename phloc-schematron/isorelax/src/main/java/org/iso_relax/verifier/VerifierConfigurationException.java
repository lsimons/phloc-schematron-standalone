package org.iso_relax.verifier;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * VerifierConfigurationException
 * 
 * @since Feb. 23, 2001
 * @version Apr. 17, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class VerifierConfigurationException extends Exception
{
  private Exception cause_ = null;

  public VerifierConfigurationException (final String message)
  {
    super (message);
  }

  public VerifierConfigurationException (final Exception e)
  {
    super (e.getMessage ());
    cause_ = e;
  }

  public VerifierConfigurationException (final String message, final Exception e)
  {
    super (message);
    cause_ = e;
  }

  public Exception getException ()
  {
    if (cause_ != null)
    {
      return (cause_);
    }
    return (this);
  }

  public Exception getCauseException ()
  {
    return (cause_);
  }

  @Override
  public void printStackTrace ()
  {
    printStackTrace (new PrintWriter (System.err, true));
  }

  @Override
  public void printStackTrace (final PrintStream out)
  {
    printStackTrace (new PrintWriter (out));
  }

  @Override
  public void printStackTrace (final PrintWriter pwriter)
  {
    final PrintWriter writer = pwriter != null ? pwriter : new PrintWriter (System.err, true);
    super.printStackTrace (writer);
    if (cause_ != null)
    {
      writer.println ();
      writer.println ("StackTrace of Original Exception:");
      cause_.printStackTrace (writer);
    }
  }
}
