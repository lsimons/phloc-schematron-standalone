package org.iso_relax.verifier;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.xml.sax.SAXException;

/**
 * VerifierException
 * 
 * @since Feb. 23, 2001
 * @version Mar. 4, 2001
 * @author ASAMI, Tomoharu (asami@zeomtech.com)
 */
public class VerifierException extends SAXException
{

  public VerifierException (final String message)
  {
    super (message);
  }

  public VerifierException (final Exception e)
  {
    super (e);
  }

  public VerifierException (final String message, final Exception e)
  {
    super (message, e);
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

    final Exception cause = super.getException ();
    if (cause != null)
    {
      writer.println ();
      writer.println ("StackTrace of Original Exception:");
      cause.printStackTrace (writer);
    }
  }
}
