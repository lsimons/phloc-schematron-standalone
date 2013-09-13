package com.thaiopensource.validate.picl;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.Validator;

class SchemaImpl extends AbstractSchema
{
  private final Constraint constraint;

  SchemaImpl (final PropertyMap properties, final Constraint constraint)
  {
    super (properties);
    this.constraint = constraint;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    return new ValidatorImpl (constraint, properties);
  }

}
