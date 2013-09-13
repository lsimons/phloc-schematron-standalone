package com.thaiopensource.relaxng.pattern;

import com.thaiopensource.xml.util.Name;

class NameFormatter
{
  static String format (final Name name)
  {
    final String localName = name.getLocalName ();
    final String namespaceUri = name.getNamespaceUri ();
    if (namespaceUri.equals (""))
      return SchemaBuilderImpl.localizer.message ("name_absent_namespace", localName);
    else
      return SchemaBuilderImpl.localizer.message ("name_with_namespace", namespaceUri, localName);
  }
}
