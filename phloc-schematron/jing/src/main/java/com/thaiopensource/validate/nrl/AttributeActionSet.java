package com.thaiopensource.validate.nrl;

import com.thaiopensource.validate.Schema;

class AttributeActionSet
{
  private boolean attach;
  private boolean reject;
  private Schema [] schemas = new Schema [0];

  boolean getAttach ()
  {
    return attach;
  }

  void setAttach (final boolean attach)
  {
    this.attach = attach;
  }

  boolean getReject ()
  {
    return reject;
  }

  void setReject (final boolean reject)
  {
    this.reject = reject;
  }

  Schema [] getSchemas ()
  {
    return schemas;
  }

  void addSchema (final Schema schema)
  {
    final Schema [] s = new Schema [schemas.length + 1];
    System.arraycopy (schemas, 0, s, 0, schemas.length);
    s[schemas.length] = schema;
    schemas = s;
  }
}
