package com.thaiopensource.xml.dtd.om;

import java.io.IOException;

import com.thaiopensource.xml.em.EntityManager;
import com.thaiopensource.xml.em.OpenEntity;

public interface DtdParser
{
  Dtd parse (String systemId, EntityManager em) throws IOException;

  Dtd parse (OpenEntity entity, EntityManager em) throws IOException;
}
