package com.thaiopensource.xml.dtd.parse;

import java.util.Enumeration;
import java.util.Vector;

import com.thaiopensource.xml.dtd.om.Choice;
import com.thaiopensource.xml.dtd.om.ElementRef;
import com.thaiopensource.xml.dtd.om.EnumGroup;
import com.thaiopensource.xml.dtd.om.EnumGroupMember;
import com.thaiopensource.xml.dtd.om.EnumGroupRef;
import com.thaiopensource.xml.dtd.om.EnumValue;
import com.thaiopensource.xml.dtd.om.ModelGroup;
import com.thaiopensource.xml.dtd.om.ModelGroupRef;
import com.thaiopensource.xml.dtd.om.Name;
import com.thaiopensource.xml.dtd.om.NameSpecRef;
import com.thaiopensource.xml.dtd.om.OneOrMore;
import com.thaiopensource.xml.dtd.om.Optional;
import com.thaiopensource.xml.dtd.om.Pcdata;
import com.thaiopensource.xml.dtd.om.Sequence;
import com.thaiopensource.xml.dtd.om.ZeroOrMore;

class Particle
{
  static final int REFERENCE = 0; // entity
  static final int GROUP = 1; // particles + occur
  static final int ELEMENT_NAME = 2; // value + occur
  static final int NMTOKEN = 3; // value
  static final int PCDATA = 4;
  static final int REFERENCE_END = 5;
  static final int CONNECT_OR = 6;
  static final int CONNECT_SEQ = 7;

  Particle (final int type)
  {
    this.type = type;
  }

  final int type;
  char occur; // * ? + or 0
  Vector particles;
  Entity entity;
  String value;

  @Override
  public boolean equals (final Object obj)
  {
    if (obj == null || !(obj instanceof Particle))
      return false;
    final Particle other = (Particle) obj;
    if (this.type != other.type)
      return false;
    if (this.occur != other.occur)
      return false;
    if (this.entity != other.entity)
      return false;
    if (this.value != null && !this.value.equals (other.value))
      return false;
    if (this.particles != null)
    {
      final int n = this.particles.size ();
      if (other.particles.size () != n)
        return false;
      for (int i = 0; i < n; i++)
        if (!this.particles.elementAt (i).equals (other.particles.elementAt (i)))
          return false;
    }
    return true;
  }

  ModelGroup createModelGroup ()
  {
    ModelGroup mg;
    switch (type)
    {
      case GROUP:
        mg = particlesToModelGroup (particles);
        break;
      case ELEMENT_NAME:
        mg = new ElementRef (new Name (value));
        break;
      case PCDATA:
        mg = new Pcdata ();
        break;
      default:
        return null;
    }
    switch (occur)
    {
      case '?':
        mg = new Optional (mg);
        break;
      case '+':
        mg = new OneOrMore (mg);
        break;
      case '*':
        mg = new ZeroOrMore (mg);
        break;
    }
    return mg;
  }

  static ModelGroup particlesToModelGroup (final Vector v)
  {
    final Vector <ModelGroup> mgs = new Vector <ModelGroup> ();
    final int len = v.size ();
    boolean isSequence = false;
    for (int i = 0; i < len; i++)
    {
      ModelGroup mg = null;
      final Particle p = (Particle) v.elementAt (i);
      switch (p.type)
      {
        case REFERENCE:
          switch (p.entity.semantic)
          {
            case Entity.SEMANTIC_MODEL_GROUP:
              mg = new ModelGroupRef (p.entity.name, p.entity.modelGroup);
              if (p.entity.parsed.size () == 0 && ((p.entity.groupFlags & Entity.GROUP_CONTAINS_SEQ) != 0))
                isSequence = true;
              i = indexOfReferenceEnd (v, i);
              break;
            case Entity.SEMANTIC_NAME_SPEC:
              mg = new ElementRef (new NameSpecRef (p.entity.name, p.entity.nameSpec));
              i = indexOfReferenceEnd (v, i);
              break;
          }
          break;
        case GROUP:
        case ELEMENT_NAME:
        case PCDATA:
          mg = p.createModelGroup ();
          break;
        case CONNECT_SEQ:
          isSequence = true;
          break;
      }
      if (mg != null)
        mgs.addElement (mg);
    }
    if (mgs.size () == 0)
      return null;
    if (mgs.size () == 1)
      return mgs.elementAt (0);
    final ModelGroup [] tem = new ModelGroup [mgs.size ()];
    for (int i = 0; i < tem.length; i++)
      tem[i] = mgs.elementAt (i);
    if (isSequence)
      return new Sequence (tem);
    else
      return new Choice (tem);
  }

  private static int indexOfReferenceEnd (final Vector v, int i)
  {
    int level = 0;
    for (;;)
    {
      final Particle p = (Particle) v.elementAt (++i);
      if (p.type == REFERENCE)
        level++;
      else
        if (p.type == REFERENCE_END && level-- == 0)
          break;
    }
    return i;
  }

  static EnumGroup particlesToEnumGroup (final Vector v)
  {
    final int len = v.size ();
    final Vector <EnumGroupMember> eg = new Vector <EnumGroupMember> ();
    for (int i = 0; i < len; i++)
    {
      EnumGroupMember egm = null;
      Particle p = (Particle) v.elementAt (i);
      switch (p.type)
      {
        case REFERENCE:
          if (p.entity.semantic == Entity.SEMANTIC_ENUM_GROUP)
          {
            egm = new EnumGroupRef (p.entity.name, p.entity.enumGroup);
            int level = 0;
            for (;;)
            {
              p = (Particle) v.elementAt (++i);
              if (p.type == REFERENCE)
                level++;
              else
                if (p.type == REFERENCE_END && level-- == 0)
                  break;
            }
          }
          break;
        case NMTOKEN:
          egm = new EnumValue (p.value);
          break;
      }
      if (egm != null)
        eg.addElement (egm);
    }
    final EnumGroupMember [] members = new EnumGroupMember [eg.size ()];
    for (int i = 0; i < members.length; i++)
      members[i] = eg.elementAt (i);
    return new EnumGroup (members);
  }

  static void examineElementNames (final DtdBuilder db, final Enumeration particles)
  {
    Entity prevEntity = null;
    while (particles.hasMoreElements ())
    {
      final Particle particle = (Particle) particles.nextElement ();
      Entity curEntity = null;
      switch (particle.type)
      {
        case REFERENCE:
          curEntity = particle.entity;
          break;
        case ELEMENT_NAME:
          db.noteElementName (particle.value, prevEntity);
          break;
        case GROUP:
          examineElementNames (db, particle.particles.elements ());
          break;
      }
      prevEntity = curEntity;
    }
  }

}
