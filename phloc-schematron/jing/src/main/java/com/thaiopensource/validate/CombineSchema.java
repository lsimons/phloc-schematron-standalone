package com.thaiopensource.validate;

import com.thaiopensource.util.PropertyMap;

public class CombineSchema extends AbstractSchema
{
  private final Schema schema1;
  private final Schema schema2;

  public CombineSchema (final Schema schema1, final Schema schema2, final PropertyMap properties)
  {
    super (properties);
    this.schema1 = schema1;
    this.schema2 = schema2;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    return new CombineValidator (schema1.createValidator (properties), schema2.createValidator (properties));
  }
}
