package com.thaiopensource.validate.rng.impl;

import org.xml.sax.ErrorHandler;

import com.thaiopensource.relaxng.pattern.IdTypeMap;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.Validator;

public class IdTypeMapSchema extends AbstractSchema
{
  private final IdTypeMap idTypeMap;

  public IdTypeMapSchema (final IdTypeMap idTypeMap, final PropertyMap properties)
  {
    super (properties);
    this.idTypeMap = idTypeMap;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    final ErrorHandler eh = properties.get (ValidateProperty.ERROR_HANDLER);
    return new IdValidator (idTypeMap, eh);
  }
}
