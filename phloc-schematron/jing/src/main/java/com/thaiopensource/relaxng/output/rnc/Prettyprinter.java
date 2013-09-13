package com.thaiopensource.relaxng.output.rnc;

import java.io.IOException;

interface Prettyprinter
{
  public static class WrappedException extends RuntimeException
  {
    private final IOException cause;

    @Override
    public Throwable getCause ()
    {
      return cause;
    }

    public IOException getIOException ()
    {
      return cause;
    }

    public WrappedException (final IOException cause)
    {
      this.cause = cause;
    }
  }

  void hardNewline ();

  void softNewline (String noBreak);

  void text (String str);

  void startNest (String indent);

  void endNest ();

  void startGroup ();

  void endGroup ();

  void close ();
}
