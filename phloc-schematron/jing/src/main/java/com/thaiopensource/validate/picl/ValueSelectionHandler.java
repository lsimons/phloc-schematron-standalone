package com.thaiopensource.validate.picl;

import org.xml.sax.Locator;

class ValueSelectionHandler implements SelectionHandler
{
  private final SelectedValueHandler handler;

  ValueSelectionHandler (final SelectedValueHandler handler)
  {
    this.handler = handler;
  }

  public void selectAttribute (final ErrorContext ec, final Path path, final String value)
  {
    handler.select (ec, null, value, value);
  }

  static class ValueHandlerImpl implements ValueHandler
  {
    private final StringBuffer buf = new StringBuffer ();
    private final Locator locator;
    private final SelectedValueHandler handler;

    ValueHandlerImpl (final SelectedValueHandler handler, final Locator locator)
    {
      this.handler = handler;
      this.locator = locator;
    }

    public void characters (final ErrorContext ec, final char [] chars, final int start, final int len)
    {
      buf.append (chars, start, len);
    }

    public void tag (final ErrorContext ec)
    {}

    public void valueComplete (final ErrorContext ec)
    {
      final String value = buf.toString ();
      handler.select (ec, locator, value, value);
    }
  }

  public void selectElement (final ErrorContext ec, final Path path, final PatternManager pm)
  {
    pm.registerValueHandler (new ValueHandlerImpl (handler, ec.saveLocator ()));
  }

  public void selectComplete (final ErrorContext ec)
  {
    handler.selectComplete (ec);
  }

}
