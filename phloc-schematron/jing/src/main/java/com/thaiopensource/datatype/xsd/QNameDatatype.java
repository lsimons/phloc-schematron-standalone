package com.thaiopensource.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import com.thaiopensource.xml.util.Name;
import com.thaiopensource.xml.util.Naming;

class QNameDatatype extends DatatypeBase
{
  @Override
  public boolean lexicallyAllows (final String str)
  {
    return Naming.isQname (str);
  }

  @Override
  String getLexicalSpaceKey ()
  {
    return "qname";
  }

  @Override
  Object getValue (final String str, final ValidationContext vc) throws DatatypeException
  {
    final int i = str.indexOf (':');
    if (i < 0)
    {
      String ns = vc.resolveNamespacePrefix ("");
      if (ns == null)
        ns = "";
      return new Name (ns, str);
    }
    else
    {
      final String prefix = str.substring (0, i);
      final String ns = vc.resolveNamespacePrefix (prefix);
      if (ns == null)
        throw new DatatypeException (localizer ().message ("undeclared_prefix", prefix));
      return new Name (ns, str.substring (i + 1));
    }
  }

  @Override
  boolean allowsValue (final String str, final ValidationContext vc)
  {
    final int i = str.indexOf (':');
    return i < 0 || vc.resolveNamespacePrefix (str.substring (0, i)) != null;
  }

  @Override
  public boolean isContextDependent ()
  {
    return true;
  }
}
