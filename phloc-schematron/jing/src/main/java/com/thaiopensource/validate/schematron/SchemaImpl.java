package com.thaiopensource.validate.schematron;

import javax.xml.transform.Templates;
import javax.xml.transform.sax.SAXTransformerFactory;

import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.Validator;

class SchemaImpl extends AbstractSchema
{
  private final Templates templates;
  private final Class <? extends SAXTransformerFactory> factoryClass;

  SchemaImpl (final Templates templates,
              final Class <? extends SAXTransformerFactory> factoryClass,
              final PropertyMap properties,
              final PropertyId <?> [] supportedPropertyIds)
  {
    super (properties, supportedPropertyIds);
    this.templates = templates;
    this.factoryClass = factoryClass;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    try
    {
      return new ValidatorImpl (templates, factoryClass.newInstance (), properties);
    }
    catch (final InstantiationException e)
    {
      throw new RuntimeException ("unexpected InstantiationException creating SAXTransformerFactory");
    }
    catch (final IllegalAccessException e)
    {
      throw new RuntimeException ("unexpected IllegalAccessException creating SAXTransformerFactory");
    }
  }
}
