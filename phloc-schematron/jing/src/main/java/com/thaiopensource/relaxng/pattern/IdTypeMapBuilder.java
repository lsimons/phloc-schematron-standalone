package com.thaiopensource.relaxng.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.relaxng.datatype.Datatype;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.thaiopensource.util.VoidValue;
import com.thaiopensource.xml.util.Name;

public class IdTypeMapBuilder
{
  private boolean hadError;
  private final ErrorHandler eh;
  private final PatternFunction <Integer> idTypeFunction = new IdTypeFunction ();
  private final IdTypeMapImpl idTypeMap = new IdTypeMapImpl ();
  private final Set <ElementPattern> elementProcessed = new HashSet <ElementPattern> ();
  private final Stack <ElementPattern> elementsToProcess = new Stack <ElementPattern> ();
  private final List <PossibleConflict> possibleConflicts = new ArrayList <PossibleConflict> ();

  private void notePossibleConflict (final NameClass elementNameClass,
                                     final NameClass attributeNameClass,
                                     final Locator loc)
  {
    possibleConflicts.add (new PossibleConflict (elementNameClass, attributeNameClass, loc));
  }

  private static class WrappedSAXException extends RuntimeException
  {
    private final SAXException cause;

    WrappedSAXException (final SAXException cause)
    {
      this.cause = cause;
    }
  }

  private static class PossibleConflict
  {
    private final NameClass elementNameClass;
    private final NameClass attributeNameClass;
    private final Locator locator;

    private PossibleConflict (final NameClass elementNameClass,
                              final NameClass attributeNameClass,
                              final Locator locator)
    {
      this.elementNameClass = elementNameClass;
      this.attributeNameClass = attributeNameClass;
      this.locator = locator;
    }
  }

  private static class ScopedName
  {
    private final Name elementName;
    private final Name attributeName;

    private ScopedName (final Name elementName, final Name attributeName)
    {
      this.elementName = elementName;
      this.attributeName = attributeName;
    }

    @Override
    public int hashCode ()
    {
      return elementName.hashCode () ^ attributeName.hashCode ();
    }

    @Override
    public boolean equals (final Object obj)
    {
      if (!(obj instanceof ScopedName))
        return false;
      final ScopedName other = (ScopedName) obj;
      return elementName.equals (other.elementName) && attributeName.equals (other.attributeName);
    }
  }

  private static class IdTypeMapImpl implements IdTypeMap
  {
    private final Map <ScopedName, Integer> table = new HashMap <ScopedName, Integer> ();

    public int getIdType (final Name elementName, final Name attributeName)
    {
      final Integer n = table.get (new ScopedName (elementName, attributeName));
      if (n == null)
        return Datatype.ID_TYPE_NULL;
      return n.intValue ();
    }

    private void add (final Name elementName, final Name attributeName, final int idType)
    {
      table.put (new ScopedName (elementName, attributeName), Integer.valueOf (idType));
    }
  }

  private class IdTypeFunction extends AbstractPatternFunction <Integer>
  {
    @Override
    public Integer caseOther (final Pattern p)
    {
      return Integer.valueOf (Datatype.ID_TYPE_NULL);
    }

    @Override
    public Integer caseData (final DataPattern p)
    {
      return Integer.valueOf (p.getDatatype ().getIdType ());
    }

    @Override
    public Integer caseDataExcept (final DataExceptPattern p)
    {
      return Integer.valueOf (p.getDatatype ().getIdType ());
    }

    @Override
    public Integer caseValue (final ValuePattern p)
    {
      return Integer.valueOf (p.getDatatype ().getIdType ());
    }
  }

  private class BuildFunction extends AbstractPatternFunction <VoidValue>
  {
    private final NameClass elementNameClass;
    private final Locator locator;
    private final boolean attributeIsParent;

    BuildFunction (final NameClass elementNameClass, final Locator locator)
    {
      this.elementNameClass = elementNameClass;
      this.locator = locator;
      this.attributeIsParent = false;
    }

    BuildFunction (final NameClass elementNameClass, final Locator locator, final boolean attributeIsParent)
    {
      this.elementNameClass = elementNameClass;
      this.locator = locator;
      this.attributeIsParent = attributeIsParent;
    }

    private BuildFunction down ()
    {
      if (!attributeIsParent)
        return this;
      return new BuildFunction (elementNameClass, locator, false);
    }

    @Override
    public VoidValue caseChoice (final ChoicePattern p)
    {
      final BuildFunction f = down ();
      p.getOperand1 ().apply (f);
      p.getOperand2 ().apply (f);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseInterleave (final InterleavePattern p)
    {
      final BuildFunction f = down ();
      p.getOperand1 ().apply (f);
      p.getOperand2 ().apply (f);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseGroup (final GroupPattern p)
    {
      final BuildFunction f = down ();
      p.getOperand1 ().apply (f);
      p.getOperand2 ().apply (f);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseOneOrMore (final OneOrMorePattern p)
    {
      p.getOperand ().apply (down ());
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseElement (final ElementPattern p)
    {
      if (elementProcessed.contains (p))
        return VoidValue.VOID;
      elementProcessed.add (p);
      elementsToProcess.push (p);
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseAttribute (final AttributePattern p)
    {
      final int idType = p.getContent ().apply (idTypeFunction).intValue ();
      if (idType != Datatype.ID_TYPE_NULL)
      {
        final NameClass attributeNameClass = p.getNameClass ();
        if (!(attributeNameClass instanceof SimpleNameClass))
        {
          error ("id_attribute_name_class", p.getLocator ());
          return VoidValue.VOID;
        }
        elementNameClass.accept (new ElementNameClassVisitor (((SimpleNameClass) attributeNameClass).getName (),
                                                              locator,
                                                              idType));
      }
      else
        notePossibleConflict (elementNameClass, p.getNameClass (), locator);
      p.getContent ().apply (new BuildFunction (null, p.getLocator (), true));
      return VoidValue.VOID;
    }

    private void datatype (final Datatype dt)
    {
      if (dt.getIdType () != Datatype.ID_TYPE_NULL && !attributeIsParent)
        error ("id_parent", locator);
    }

    @Override
    public VoidValue caseData (final DataPattern p)
    {
      datatype (p.getDatatype ());
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseDataExcept (final DataExceptPattern p)
    {
      datatype (p.getDatatype ());
      p.getExcept ().apply (down ());
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseValue (final ValuePattern p)
    {
      datatype (p.getDatatype ());
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseList (final ListPattern p)
    {
      p.getOperand ().apply (down ());
      return VoidValue.VOID;
    }

    @Override
    public VoidValue caseOther (final Pattern p)
    {
      return VoidValue.VOID;
    }
  }

  private class ElementNameClassVisitor implements NameClassVisitor
  {
    private final Name attributeName;
    private final Locator locator;
    private final int idType;

    ElementNameClassVisitor (final Name attributeName, final Locator locator, final int idType)
    {
      this.attributeName = attributeName;
      this.locator = locator;
      this.idType = idType;
    }

    public void visitChoice (final NameClass nc1, final NameClass nc2)
    {
      nc1.accept (this);
      nc2.accept (this);
    }

    public void visitName (final Name elementName)
    {
      final int tem = idTypeMap.getIdType (elementName, attributeName);
      if (tem != Datatype.ID_TYPE_NULL && tem != idType)
        error ("id_type_conflict", elementName, attributeName, locator);
      idTypeMap.add (elementName, attributeName, idType);
    }

    public void visitNsName (final String ns)
    {
      visitOther ();
    }

    public void visitNsNameExcept (final String ns, final NameClass nc)
    {
      visitOther ();
    }

    public void visitAnyName ()
    {
      visitOther ();
    }

    public void visitAnyNameExcept (final NameClass nc)
    {
      visitOther ();
    }

    public void visitNull ()
    {}

    public void visitError ()
    {}

    private void visitOther ()
    {
      error ("id_element_name_class", locator);
    }
  }

  private void error (final String key, final Locator locator)
  {
    hadError = true;
    if (eh != null)
      try
      {
        eh.error (new SAXParseException (SchemaBuilderImpl.localizer.message (key), locator));
      }
      catch (final SAXException e)
      {
        throw new WrappedSAXException (e);
      }
  }

  private void error (final String key, final Name arg1, final Name arg2, final Locator locator)
  {
    hadError = true;
    if (eh != null)
      try
      {
        eh.error (new SAXParseException (SchemaBuilderImpl.localizer.message (key,
                                                                              NameFormatter.format (arg1),
                                                                              NameFormatter.format (arg2)), locator));
      }
      catch (final SAXException e)
      {
        throw new WrappedSAXException (e);
      }
  }

  public IdTypeMapBuilder (final ErrorHandler eh, final Pattern pattern) throws SAXException
  {
    this.eh = eh;
    try
    {
      pattern.apply (new BuildFunction (null, null));
      while (elementsToProcess.size () > 0)
      {
        final ElementPattern p = elementsToProcess.pop ();
        p.getContent ().apply (new BuildFunction (p.getNameClass (), p.getLocator ()));
      }
      for (final PossibleConflict pc : possibleConflicts)
      {
        if (pc.elementNameClass instanceof SimpleNameClass && pc.attributeNameClass instanceof SimpleNameClass)
        {
          final Name elementName = ((SimpleNameClass) pc.elementNameClass).getName ();
          final Name attributeName = ((SimpleNameClass) pc.attributeNameClass).getName ();
          final int idType = idTypeMap.getIdType (elementName, attributeName);
          if (idType != Datatype.ID_TYPE_NULL)
            error ("id_type_conflict", elementName, attributeName, pc.locator);
        }
        else
        {
          for (final ScopedName sn : idTypeMap.table.keySet ())
          {
            if (pc.elementNameClass.contains (sn.elementName) && pc.attributeNameClass.contains (sn.attributeName))
            {
              error ("id_type_conflict", sn.elementName, sn.attributeName, pc.locator);
              break;
            }
          }
        }
      }
    }
    catch (final WrappedSAXException e)
    {
      throw e.cause;
    }
  }

  public IdTypeMap getIdTypeMap ()
  {
    if (hadError)
      return null;
    return idTypeMap;
  }
}
