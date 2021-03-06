package com.thaiopensource.xml.dtd.parse;

import java.util.Vector;

import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.om.TopLevel;
import com.thaiopensource.xml.dtd.om.TopLevelVisitor;

class DtdImpl implements Dtd
{
  private final Vector topLevel;
  private final String encoding;
  private final String uri;

  DtdImpl (final Vector topLevel, final String uri, final String encoding)
  {
    this.topLevel = topLevel;
    this.uri = uri;
    this.encoding = encoding;
  }

  public String getUri ()
  {
    return uri;
  }

  public String getEncoding ()
  {
    return encoding;
  }

  public TopLevel [] getAllTopLevel ()
  {
    final TopLevel [] tem = new TopLevel [topLevel.size ()];
    for (int i = 0; i < tem.length; i++)
      tem[i] = (TopLevel) topLevel.elementAt (i);
    return tem;
  }

  public void accept (final TopLevelVisitor visitor) throws Exception
  {
    final int n = topLevel.size ();
    for (int i = 0; i < n; i++)
      ((TopLevel) topLevel.elementAt (i)).accept (visitor);
  }
}
