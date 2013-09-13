package com.thaiopensource.xml.dtd.parse;

import java.io.IOException;

import com.thaiopensource.util.Localizer;

public class ParseException extends IOException
{
  private final Localizer localizer;
  private final String location;
  private final int lineNumber;
  private final int columnNumber;

  public ParseException (final Localizer localizer,
                         final String message,
                         final String location,
                         final int lineNumber,
                         final int columnNumber)
  {
    super (message);
    this.localizer = localizer;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.location = location;
  }

  public int getLineNumber ()
  {
    return lineNumber;
  }

  public int getColumnNumber ()
  {
    return columnNumber;
  }

  public String getLocation ()
  {
    return location;
  }

  @Override
  public String getMessage ()
  {
    return localizer.message ("MESSAGE", new Object [] { super.getMessage (),
                                                        location,
                                                        new Integer (lineNumber),
                                                        new Integer (columnNumber) });
  }

  public String getMessageBody ()
  {
    return super.getMessage ();
  }
}
