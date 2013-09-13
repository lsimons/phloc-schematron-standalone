package com.thaiopensource.relaxng.parse.compact;

class EscapeSyntaxException extends RuntimeException
{
  private final String key;
  private final int lineNumber;
  private final int columnNumber;

  EscapeSyntaxException (final String key, final int lineNumber, final int columnNumber)
  {
    this.key = key;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  String getKey ()
  {
    return key;
  }

  int getLineNumber ()
  {
    return lineNumber;
  }

  int getColumnNumber ()
  {
    return columnNumber;
  }
}
