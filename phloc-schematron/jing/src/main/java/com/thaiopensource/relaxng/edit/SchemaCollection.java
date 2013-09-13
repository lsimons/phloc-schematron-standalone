package com.thaiopensource.relaxng.edit;

import java.util.HashMap;
import java.util.Map;

public class SchemaCollection
{
  private final Map <String, SchemaDocument> schemaDocumentMap = new HashMap <String, SchemaDocument> ();
  private String mainUri;

  public SchemaCollection ()
  {}

  public String getMainUri ()
  {
    return mainUri;
  }

  public void setMainUri (final String mainUri)
  {
    this.mainUri = mainUri;
  }

  public Map <String, SchemaDocument> getSchemaDocumentMap ()
  {
    return schemaDocumentMap;
  }
}
