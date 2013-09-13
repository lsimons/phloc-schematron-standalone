package com.thaiopensource.xml.dtd.parse;

import java.io.IOException;

import com.thaiopensource.xml.dtd.om.Dtd;
import com.thaiopensource.xml.dtd.om.DtdParser;
import com.thaiopensource.xml.em.EntityManager;
import com.thaiopensource.xml.em.OpenEntity;

public class DtdParserImpl implements DtdParser
{
  public DtdParserImpl ()
  {}

  public Dtd parse (final String systemId, final EntityManager em) throws IOException
  {
    return parse (em.open (systemId), em);
  }

  public Dtd parse (final OpenEntity entity, final EntityManager em) throws IOException
  {
    final DtdBuilder db = new Parser (entity, em).parse ();
    db.unexpandEntities ();
    db.createDecls ();
    db.analyzeSemantics ();
    return new DtdImpl (db.createTopLevel (), entity.getBaseUri (), entity.getEncoding ());
  }
}
