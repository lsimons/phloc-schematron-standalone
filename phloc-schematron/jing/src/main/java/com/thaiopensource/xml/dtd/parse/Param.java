package com.thaiopensource.xml.dtd.parse;

import java.util.Enumeration;
import java.util.Vector;

import com.thaiopensource.xml.dtd.om.Any;
import com.thaiopensource.xml.dtd.om.Attribute;
import com.thaiopensource.xml.dtd.om.AttributeDefault;
import com.thaiopensource.xml.dtd.om.AttributeDefaultRef;
import com.thaiopensource.xml.dtd.om.AttributeGroup;
import com.thaiopensource.xml.dtd.om.AttributeGroupMember;
import com.thaiopensource.xml.dtd.om.AttributeGroupRef;
import com.thaiopensource.xml.dtd.om.CdataDatatype;
import com.thaiopensource.xml.dtd.om.Datatype;
import com.thaiopensource.xml.dtd.om.DatatypeRef;
import com.thaiopensource.xml.dtd.om.DefaultValue;
import com.thaiopensource.xml.dtd.om.EnumDatatype;
import com.thaiopensource.xml.dtd.om.EnumGroup;
import com.thaiopensource.xml.dtd.om.EnumGroupMember;
import com.thaiopensource.xml.dtd.om.EnumGroupRef;
import com.thaiopensource.xml.dtd.om.FixedValue;
import com.thaiopensource.xml.dtd.om.Flag;
import com.thaiopensource.xml.dtd.om.FlagRef;
import com.thaiopensource.xml.dtd.om.Ignore;
import com.thaiopensource.xml.dtd.om.ImpliedValue;
import com.thaiopensource.xml.dtd.om.Include;
import com.thaiopensource.xml.dtd.om.ModelGroup;
import com.thaiopensource.xml.dtd.om.ModelGroupRef;
import com.thaiopensource.xml.dtd.om.Name;
import com.thaiopensource.xml.dtd.om.NameSpec;
import com.thaiopensource.xml.dtd.om.NameSpecRef;
import com.thaiopensource.xml.dtd.om.NotationDatatype;
import com.thaiopensource.xml.dtd.om.RequiredValue;
import com.thaiopensource.xml.dtd.om.Sequence;
import com.thaiopensource.xml.dtd.om.TokenizedDatatype;

class Param
{
  static final int REFERENCE = 0;
  static final int REFERENCE_END = 1;
  static final int LITERAL = 2;
  static final int MODEL_GROUP = 3;
  static final int PERCENT = 4;
  static final int IMPLIED = 5; // #IMPLIED
  static final int REQUIRED = 6; // #REQUIRED
  static final int FIXED = 7; // #REQUIRED
  static final int EMPTY = 8;
  static final int ANY = 9;
  static final int ELEMENT_NAME = 10; // name after <!ELEMENT or <!ATTLIST
  static final int ATTRIBUTE_NAME = 11;
  static final int ATTRIBUTE_TYPE = 12;
  static final int ATTRIBUTE_TYPE_NOTATION = 13;
  static final int DEFAULT_ATTRIBUTE_VALUE = 14;
  static final int ATTRIBUTE_VALUE_GROUP = 15; // a group in an ATTLIST
  // Pseudo-param representing zero or more attributes in an ATTLIST
  static final int EMPTY_ATTRIBUTE_GROUP = 16;
  static final int NOTATION_GROUP = 17;
  static final int IGNORE = 18;
  static final int INCLUDE = 19;

  static final int OTHER = 30;

  Param (final int type)
  {
    this.type = type;
  }

  final int type;
  Entity entity;
  Particle group;
  String value;

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof Param))
      return false;
    final Param other = (Param) obj;
    if (this.type != other.type)
      return false;
    if (this.entity != other.entity)
      return false;
    if (this.value != null && !this.value.equals (other.value))
      return false;
    if (this.group != null && !this.group.equals (other.group))
      return false;
    return true;
  }

  static AttributeGroup paramsToAttributeGroup (final Vector v)
  {
    return paramsToAttributeGroup (new ParamStream (v, true));
  }

  static AttributeGroup paramsToAttributeGroup (final ParamStream ps)
  {
    final Vector ag = new Vector ();
    while (ps.advance ())
    {
      AttributeGroupMember agm = null;
      switch (ps.type)
      {
        case EMPTY_ATTRIBUTE_GROUP:
          break;
        case REFERENCE:
          if (ps.entity.semantic == Entity.SEMANTIC_ATTRIBUTE_GROUP)
          {
            agm = new AttributeGroupRef (ps.entity.name, ps.entity.attributeGroup);
            break;
          }
          // fall through
        case ATTRIBUTE_NAME:
        {
          final NameSpec nameSpec = currentParamToNameSpec (ps);
          final Datatype datatype = paramsToDatatype (ps);
          final AttributeDefault ad = paramsToAttributeDefault (ps);
          agm = new Attribute (nameSpec, datatype, ad);
        }
      }
      if (agm != null)
        ag.addElement (agm);
    }
    final AttributeGroupMember [] members = new AttributeGroupMember [ag.size ()];
    for (int i = 0; i < members.length; i++)
      members[i] = (AttributeGroupMember) ag.elementAt (i);
    return new AttributeGroup (members);
  }

  static Datatype paramsToDatatype (final Vector v)
  {
    return paramsToDatatype (new ParamStream (v, true));
  }

  static Datatype paramsToDatatype (final ParamStream ps)
  {
    ps.advance ();
    switch (ps.type)
    {
      case REFERENCE:
        return new DatatypeRef (ps.entity.name, ps.entity.datatype);
      case ATTRIBUTE_VALUE_GROUP:
        return new EnumDatatype (Particle.particlesToEnumGroup (ps.group.particles));
      case ATTRIBUTE_TYPE_NOTATION:
        ps.advance ();
        return new NotationDatatype (paramToEnumGroup (ps));
      case ATTRIBUTE_TYPE:
        if (ps.value.equals ("CDATA"))
          return new CdataDatatype ();
        else
          return new TokenizedDatatype (ps.value);
    }
    throw new Error ();
  }

  static EnumGroup paramToEnumGroup (final ParamStream ps)
  {
    if (ps.type == REFERENCE)
      return new EnumGroup (new EnumGroupMember [] { new EnumGroupRef (ps.entity.name, ps.entity.enumGroup) });
    else
      return Particle.particlesToEnumGroup (ps.group.particles);
  }

  static AttributeDefault paramsToAttributeDefault (final Vector v)
  {
    return paramsToAttributeDefault (new ParamStream (v, true));
  }

  static AttributeDefault paramsToAttributeDefault (final ParamStream ps)
  {
    ps.advance ();
    switch (ps.type)
    {
      case REFERENCE:
        return new AttributeDefaultRef (ps.entity.name, ps.entity.attributeDefault);
      case REQUIRED:
        return new RequiredValue ();
      case FIXED:
        ps.advance ();
        return new FixedValue (ps.value);
      case DEFAULT_ATTRIBUTE_VALUE:
        return new DefaultValue (ps.value);
      case IMPLIED:
        return new ImpliedValue ();
    }
    throw new Error ();
  }

  static ModelGroup paramsToModelGroup (final Vector v)
  {
    return paramsToModelGroup (new ParamStream (v, true));
  }

  static ModelGroup paramsToModelGroup (final ParamStream ps)
  {
    ps.advance ();
    switch (ps.type)
    {
      case Param.REFERENCE:
        return new ModelGroupRef (ps.entity.name, ps.entity.modelGroup);
      case Param.ANY:
        return new Any ();
      case Param.EMPTY:
        return new Sequence (new ModelGroup [0]);
      case Param.MODEL_GROUP:
        return ps.group.createModelGroup ();
    }
    throw new Error ();
  }

  static Flag paramsToFlag (final Vector v)
  {
    return paramsToFlag (new ParamStream (v, true));
  }

  static Flag paramsToFlag (final ParamStream ps)
  {
    ps.advance ();
    switch (ps.type)
    {
      case Param.REFERENCE:
        return new FlagRef (ps.entity.name, ps.entity.flag);
      case Param.IGNORE:
        return new Ignore ();
      case Param.INCLUDE:
        return new Include ();
    }
    throw new Error ();
  }

  static NameSpec paramsToNameSpec (final Vector v)
  {
    return paramsToNameSpec (new ParamStream (v, true));
  }

  static NameSpec paramsToNameSpec (final ParamStream ps)
  {
    ps.advance ();
    return currentParamToNameSpec (ps);
  }

  static private NameSpec currentParamToNameSpec (final ParamStream ps)
  {
    switch (ps.type)
    {
      case Param.REFERENCE:
        return new NameSpecRef (ps.entity.name, ps.entity.nameSpec);
      case Param.ELEMENT_NAME:
      case Param.ATTRIBUTE_NAME:
        return new Name (ps.value);
    }
    throw new Error ();
  }

  static void examineElementNames (final DtdBuilder db, final Enumeration params)
  {
    while (params.hasMoreElements ())
    {
      final Param param = (Param) params.nextElement ();
      switch (param.type)
      {
        case ELEMENT_NAME:
          db.noteElementName (param.value, null);
          break;
        case MODEL_GROUP:
          Particle.examineElementNames (db, param.group.particles.elements ());
          break;
      }
    }
  }

}
