package com.thaiopensource.xml.infer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thaiopensource.xml.util.Name;

class ElementDeclInferrer
{
  private final DatatypeRepertoire datatypes;
  private ContentModelInferrer contentModelInferrer;
  private final Map <Name, DatatypeInferrer> attributeTypeMap = new HashMap <Name, DatatypeInferrer> ();
  private DatatypeInferrer valueInferrer;
  private final Set <Name> requiredAttributeNames = new HashSet <Name> ();
  private Set <Name> mixedContentNames = null;

  ElementDeclInferrer (final DatatypeRepertoire datatypes, final List <Name> attributeNames)
  {
    this.datatypes = datatypes;
    requiredAttributeNames.addAll (attributeNames);
  }

  ElementDecl infer ()
  {
    final ElementDecl decl = new ElementDecl ();
    for (final Map.Entry <Name, DatatypeInferrer> entry : attributeTypeMap.entrySet ())
    {
      decl.getAttributeDecls ().put (entry.getKey (),
                                     new AttributeDecl ((entry.getValue ()).getTypeName (),
                                                        !requiredAttributeNames.contains (entry.getKey ())));
    }
    if (contentModelInferrer != null)
      decl.setContentModel (contentModelInferrer.inferContentModel ());
    else
      if (mixedContentNames != null)
        decl.setContentModel (makeMixedContentModel ());
      else
      {
        if (valueInferrer.isAllWhiteSpace ())
          decl.setContentModel (new EmptyParticle ());
        else
        {
          final Name typeName = valueInferrer.getTypeName ();
          if (typeName == null)
            decl.setContentModel (new TextParticle ());
          else
            decl.setDatatype (typeName);
        }
      }
    return decl;
  }

  private Particle makeMixedContentModel ()
  {
    Particle p = new TextParticle ();
    for (final Name name : mixedContentNames)
      p = new ChoiceParticle (p, new ElementParticle (name));
    return new OneOrMoreParticle (p);
  }

  boolean wantValue ()
  {
    return contentModelInferrer == null && mixedContentNames == null;
  }

  void addElement (final Name elementName)
  {
    if (valueInferrer != null)
    {
      if (valueInferrer.isAllWhiteSpace ())
      {
        if (contentModelInferrer == null)
          contentModelInferrer = ContentModelInferrer.createContentModelInferrer ();
        // Previously had all elements contained only white space.
        // Equivalent to an empty content model.
        contentModelInferrer.endSequence ();
        valueInferrer = null;
      }
      else
        useMixedContent ();
    }
    if (mixedContentNames != null)
      mixedContentNames.add (elementName);
    else
    {
      if (contentModelInferrer == null)
        contentModelInferrer = ContentModelInferrer.createContentModelInferrer ();
      contentModelInferrer.addElement (elementName);
    }
  }

  void endSequence ()
  {
    if (contentModelInferrer != null)
      contentModelInferrer.endSequence ();
  }

  void addValue (final String value)
  {
    if (valueInferrer == null)
      valueInferrer = new DatatypeInferrer (datatypes, value);
    else
      valueInferrer.addValue (value);
  }

  void addText ()
  {
    useMixedContent ();
  }

  private void useMixedContent ()
  {
    if (mixedContentNames == null)
    {
      mixedContentNames = new HashSet <Name> ();
      if (contentModelInferrer != null)
      {
        mixedContentNames.addAll (contentModelInferrer.getElementNames ());
        contentModelInferrer = null;
      }
      valueInferrer = null;
    }
  }

  void addAttributeNames (final List <Name> attributeNames)
  {
    requiredAttributeNames.retainAll (attributeNames);
  }

  void addAttributeValue (final Name name, final String value)
  {
    DatatypeInferrer dt = attributeTypeMap.get (name);
    if (dt == null)
    {
      dt = new DatatypeInferrer (datatypes, value);
      attributeTypeMap.put (name, dt);
    }
    else
      dt.addValue (value);
  }

}
