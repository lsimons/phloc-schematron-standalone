package com.thaiopensource.relaxng.pattern;

import org.xml.sax.Locator;

import com.thaiopensource.xml.util.Name;

class RestrictionViolationException extends Exception
{
  private final String messageId;
  private Locator loc;
  private Name name;
  private String namespaceUri;

  RestrictionViolationException (final String messageId)
  {
    this.messageId = messageId;
  }

  RestrictionViolationException (final String messageId, final Name name)
  {
    this.messageId = messageId;
    this.name = name;
  }

  RestrictionViolationException (final String messageId, final String namespaceUri)
  {
    this.messageId = messageId;
    this.namespaceUri = namespaceUri;
  }

  String getMessageId ()
  {
    return messageId;
  }

  Locator getLocator ()
  {
    return loc;
  }

  void maybeSetLocator (final Locator loc)
  {
    if (this.loc == null)
      this.loc = loc;
  }

  Name getName ()
  {
    return name;
  }

  String getNamespaceUri ()
  {
    return namespaceUri;
  }
}
