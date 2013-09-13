package com.thaiopensource.relaxng.output.dtd;

import java.util.HashMap;
import java.util.Map;

import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameNameClass;

class AttlistMapper
{
  private final Map <ElementPattern, Object> elementToAttlistMap = new HashMap <ElementPattern, Object> ();
  private final Map <String, Object> paramEntityToElementMap = new HashMap <String, Object> ();

  void noteAttribute (final ElementPattern e)
  {
    elementToAttlistMap.put (e, Boolean.FALSE);
  }

  void noteAttributeGroupRef (final ElementPattern e, final String paramEntityName)
  {
    if (e != null)
    {
      if (elementToAttlistMap.get (e) != null)
        elementToAttlistMap.put (e, Boolean.FALSE);
      else
        elementToAttlistMap.put (e, paramEntityName);
    }
    if (e == null || paramEntityToElementMap.get (paramEntityName) != null)
      paramEntityToElementMap.put (paramEntityName, Boolean.FALSE);
    else
      paramEntityToElementMap.put (paramEntityName, e);
  }

  NameNameClass getParamEntityElementName (final String name)
  {
    final Object elem = paramEntityToElementMap.get (name);
    if (elem == null || elem == Boolean.FALSE)
      return null;
    final Object tem = elementToAttlistMap.get (elem);
    if (!name.equals (tem))
      return null;
    final NameClass nc = ((ElementPattern) elem).getNameClass ();
    if (!(nc instanceof NameNameClass))
      return null;
    return (NameNameClass) nc;
  }
}
