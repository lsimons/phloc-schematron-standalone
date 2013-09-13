package com.thaiopensource.validate.nrl;

import java.util.Enumeration;
import java.util.Hashtable;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

class Mode
{
  static final String ANY_NAMESPACE = "##any";
  static final int ATTRIBUTE_PROCESSING_NONE = 0;
  static final int ATTRIBUTE_PROCESSING_QUALIFIED = 1;
  static final int ATTRIBUTE_PROCESSING_FULL = 2;
  static final Mode CURRENT = new Mode ("#current", null);

  private final String name;
  private Mode baseMode;
  private boolean defined;
  private Locator whereDefined;
  private Locator whereUsed;
  private final Hashtable <String, ActionSet> elementMap = new Hashtable <String, ActionSet> ();
  private final Hashtable <String, AttributeActionSet> attributeMap = new Hashtable <String, AttributeActionSet> ();
  private int attributeProcessing = -1;

  Mode (final String name, final Mode baseMode)
  {
    this.name = name;
    this.baseMode = baseMode;
  }

  String getName ()
  {
    return name;
  }

  Mode getBaseMode ()
  {
    return baseMode;
  }

  void setBaseMode (final Mode baseMode)
  {
    this.baseMode = baseMode;
  }

  ActionSet getElementActions (final String ns)
  {
    ActionSet actions = getElementActionsExplicit (ns);
    if (actions == null)
    {
      actions = getElementActionsExplicit (ANY_NAMESPACE);
      // this is not correct: it breaks a derived mode that use anyNamespace
      // elementMap.put(ns, actions);
    }
    return actions;
  }

  private ActionSet getElementActionsExplicit (final String ns)
  {
    ActionSet actions = elementMap.get (ns);
    if (actions == null && baseMode != null)
    {
      actions = baseMode.getElementActionsExplicit (ns);
      if (actions != null)
      {
        actions = actions.changeCurrentMode (this);
        elementMap.put (ns, actions);
      }
    }
    return actions;
  }

  AttributeActionSet getAttributeActions (final String ns)
  {
    AttributeActionSet actions = getAttributeActionsExplicit (ns);
    if (actions == null)
    {
      actions = getAttributeActionsExplicit (ANY_NAMESPACE);
      // this is not correct: it breaks a derived mode that use anyNamespace
      // attributeMap.put(ns, actions);
    }
    return actions;
  }

  private AttributeActionSet getAttributeActionsExplicit (final String ns)
  {
    AttributeActionSet actions = attributeMap.get (ns);
    if (actions == null && baseMode != null)
    {
      actions = baseMode.getAttributeActionsExplicit (ns);
      if (actions != null)
        attributeMap.put (ns, actions);
    }
    return actions;
  }

  int getAttributeProcessing ()
  {
    if (attributeProcessing == -1)
    {
      if (baseMode != null)
        attributeProcessing = baseMode.getAttributeProcessing ();
      else
        attributeProcessing = ATTRIBUTE_PROCESSING_NONE;
      for (final Enumeration <String> e = attributeMap.keys (); e.hasMoreElements () &&
                                                       attributeProcessing != ATTRIBUTE_PROCESSING_FULL;)
      {
        final String ns = e.nextElement ();
        final AttributeActionSet actions = attributeMap.get (ns);
        if (!actions.getAttach () || actions.getReject () || actions.getSchemas ().length > 0)
          attributeProcessing = ((ns.equals ("") || ns.equals (ANY_NAMESPACE)) ? ATTRIBUTE_PROCESSING_FULL
                                                                              : ATTRIBUTE_PROCESSING_QUALIFIED);
      }
    }
    return attributeProcessing;
  }

  Locator getWhereDefined ()
  {
    return whereDefined;
  }

  boolean isDefined ()
  {
    return defined;
  }

  Locator getWhereUsed ()
  {
    return whereUsed;
  }

  void noteUsed (final Locator locator)
  {
    if (whereUsed == null && locator != null)
      whereUsed = new LocatorImpl (locator);
  }

  void noteDefined (final Locator locator)
  {
    defined = true;
    if (whereDefined == null && locator != null)
      whereDefined = new LocatorImpl (locator);
  }

  boolean bindElement (final String ns, final ActionSet actions)
  {
    if (elementMap.get (ns) != null)
      return false;
    elementMap.put (ns, actions);
    return true;
  }

  boolean bindAttribute (final String ns, final AttributeActionSet actions)
  {
    if (attributeMap.get (ns) != null)
      return false;
    attributeMap.put (ns, actions);
    return true;
  }

}
