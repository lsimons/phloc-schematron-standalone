package com.thaiopensource.validate.xerces;

import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;

import com.thaiopensource.util.PropertyId;
import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.validate.AbstractSchema;
import com.thaiopensource.validate.Validator;

class SchemaImpl extends AbstractSchema
{
  private final SymbolTable symbolTable;
  private final XMLGrammarPool grammarPool;

  SchemaImpl (final SymbolTable symbolTable,
              final XMLGrammarPool grammarPool,
              final PropertyMap properties,
              final PropertyId <?> [] supportedPropertyIds)
  {
    super (properties, supportedPropertyIds);
    this.symbolTable = symbolTable;
    this.grammarPool = grammarPool;
  }

  public Validator createValidator (final PropertyMap properties)
  {
    return new ValidatorImpl (symbolTable, grammarPool, properties);
  }
}
