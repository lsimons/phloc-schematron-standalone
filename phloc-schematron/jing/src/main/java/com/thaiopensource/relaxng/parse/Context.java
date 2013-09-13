package com.thaiopensource.relaxng.parse;

import java.util.Set;

import org.relaxng.datatype.ValidationContext;

public interface Context extends ValidationContext
{
  Set <String> prefixes ();

  Context copy ();
}
