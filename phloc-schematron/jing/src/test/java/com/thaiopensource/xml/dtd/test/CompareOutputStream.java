package com.thaiopensource.xml.dtd.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CompareOutputStream extends OutputStream
{
  private final InputStream in;
  private long byteIndex = 0;

  public CompareOutputStream (final InputStream in)
  {
    this.in = in;
  }

  @Override
  public void write (final int b) throws IOException
  {
    if (in.read () != (b & 0xFF))
      throw new CompareFailException (byteIndex);
    byteIndex++;
  }

  @Override
  public void close () throws IOException
  {
    if (in.read () != -1)
      throw new CompareFailException (byteIndex);
    in.close ();
  }
}
