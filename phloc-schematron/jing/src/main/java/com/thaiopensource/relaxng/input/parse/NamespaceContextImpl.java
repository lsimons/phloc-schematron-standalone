package com.thaiopensource.relaxng.input.parse;

import java.util.Collections;
import java.util.Set;

import com.thaiopensource.relaxng.edit.NamespaceContext;
import com.thaiopensource.relaxng.parse.Context;

class NamespaceContextImpl implements NamespaceContext
{
  private final Context context;
  private Set <String> cachedPrefixes = null;

  NamespaceContextImpl (final Context context)
  {
    this.context = context.copy ();
  }

  public String getNamespace (final String prefix)
  {
    return context.resolveNamespacePrefix (prefix);
  }

  public Set <String> getPrefixes ()
  {
    if (cachedPrefixes == null)
      cachedPrefixes = Collections.unmodifiableSet (context.prefixes ());
    return cachedPrefixes;
  }
}
