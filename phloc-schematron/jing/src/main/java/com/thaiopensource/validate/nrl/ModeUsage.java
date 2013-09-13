package com.thaiopensource.validate.nrl;

import java.util.Enumeration;
import java.util.Vector;

import com.thaiopensource.util.Equal;

class ModeUsage
{
  private final Mode mode;
  private final Mode currentMode;
  private ContextMap modeMap;
  private int attributeProcessing = -1;

  ModeUsage (final Mode mode, final Mode currentMode)
  {
    this (mode, currentMode, null);
  }

  private ModeUsage (final Mode mode, final Mode currentMode, final ContextMap modeMap)
  {
    this.mode = mode;
    this.currentMode = currentMode;
    this.modeMap = modeMap;
  }

  ModeUsage changeCurrentMode (final Mode currentMode)
  {
    return new ModeUsage (mode, currentMode, modeMap);
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (!(obj instanceof ModeUsage))
      return false;
    final ModeUsage other = (ModeUsage) obj;
    return this.mode == other.mode &&
           this.currentMode == other.currentMode &&
           Equal.equal (this.modeMap, other.modeMap);
  }

  @Override
  public int hashCode ()
  {
    int hc = mode.hashCode () ^ currentMode.hashCode ();
    if (modeMap != null)
      hc ^= modeMap.hashCode ();
    return hc;
  }

  private Mode resolve (final Mode mode)
  {
    return mode == Mode.CURRENT ? currentMode : mode;
  }

  int getAttributeProcessing ()
  {
    if (attributeProcessing == -1)
    {
      attributeProcessing = resolve (mode).getAttributeProcessing ();
      if (modeMap != null)
      {
        for (final Enumeration e = modeMap.values (); e.hasMoreElements () &&
                                                      attributeProcessing != Mode.ATTRIBUTE_PROCESSING_FULL;)
          attributeProcessing = Math.max (resolve ((Mode) e.nextElement ()).getAttributeProcessing (),
                                          attributeProcessing);
      }
    }
    return attributeProcessing;
  }

  boolean isContextDependent ()
  {
    return modeMap != null;
  }

  Mode getMode (final Vector context)
  {
    if (modeMap != null)
    {
      final Mode m = (Mode) modeMap.get (context);
      if (m != null)
        return resolve (m);
    }
    return resolve (mode);
  }

  boolean addContext (final boolean isRoot, final Vector names, final Mode mode)
  {
    if (modeMap == null)
      modeMap = new ContextMap ();
    return modeMap.put (isRoot, names, mode);
  }
}
