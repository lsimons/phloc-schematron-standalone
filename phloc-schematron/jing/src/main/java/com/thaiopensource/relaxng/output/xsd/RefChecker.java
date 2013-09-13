package com.thaiopensource.relaxng.output.xsd;

import java.util.HashMap;
import java.util.Map;

import com.thaiopensource.relaxng.edit.AbstractVisitor;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.ExternalRefPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.ParentRefPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.UnaryPattern;
import com.thaiopensource.relaxng.output.common.ErrorReporter;
import com.thaiopensource.util.VoidValue;

class RefChecker extends AbstractVisitor
{
  private final SchemaInfo schema;
  private final ErrorReporter er;
  private final Map <String, Ref> refMap = new HashMap <String, Ref> ();
  private int currentDepth = 0;

  static private class Ref
  {
    int checkRecursionDepth;

    Ref (final int checkRecursionDepth)
    {
      this.checkRecursionDepth = checkRecursionDepth;
    }
  }

  private RefChecker (final SchemaInfo schema, final ErrorReporter er)
  {
    this.schema = schema;
    this.er = er;
  }

  static void check (final SchemaInfo schema, final ErrorReporter er)
  {
    schema.getGrammar ().componentsAccept (new RefChecker (schema, er));
  }

  @Override
  public VoidValue visitDiv (final DivComponent c)
  {
    c.componentsAccept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitDefine (final DefineComponent c)
  {
    final String name = c.getName ();
    if (name == DefineComponent.START || refMap.get (name) == null)
      c.getBody ().accept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitInclude (final IncludeComponent c)
  {
    schema.getSchema (c.getUri ()).componentsAccept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitElement (final ElementPattern p)
  {
    currentDepth++;
    p.getChild ().accept (this);
    currentDepth--;
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitUnary (final UnaryPattern p)
  {
    return p.getChild ().accept (this);
  }

  @Override
  public VoidValue visitComposite (final CompositePattern p)
  {
    p.childrenAccept (this);
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitRef (final RefPattern p)
  {
    Ref ref = refMap.get (p.getName ());
    if (ref == null)
    {
      ref = new Ref (currentDepth);
      refMap.put (p.getName (), ref);
      final Pattern body = schema.getBody (p);
      if (body == null)
        er.error ("undefined_reference", p.getName (), p.getSourceLocation ());
      else
        schema.getBody (p).accept (this);
      ref.checkRecursionDepth = -1;
    }
    else
      if (currentDepth == ref.checkRecursionDepth)
        er.error ("recursive_reference", p.getName (), p.getSourceLocation ());
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitExternalRef (final ExternalRefPattern p)
  {
    er.error ("external_ref_not_supported", p.getSourceLocation ());
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitGrammar (final GrammarPattern p)
  {
    er.error ("nested_grammar_not_supported", p.getSourceLocation ());
    return VoidValue.VOID;
  }

  @Override
  public VoidValue visitParentRef (final ParentRefPattern p)
  {
    er.error ("parent_ref_no_grammar", p.getSourceLocation ());
    return VoidValue.VOID;
  }
}
