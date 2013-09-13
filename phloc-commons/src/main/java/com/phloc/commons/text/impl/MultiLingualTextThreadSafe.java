/**
 * Copyright (C) 2006-2013 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.commons.text.impl;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.callback.IChangeNotify;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.locale.LocaleCache;
import com.phloc.commons.state.EChange;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.text.IMultiLingualText;
import com.phloc.commons.text.IReadonlyMultiLingualText;
import com.phloc.commons.text.ISimpleMultiLingualText;

/**
 * This class represents a thread safe multilingual text. It wraps an existing
 * MultiLingualText and adds a read write lock around it.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public final class MultiLingualTextThreadSafe implements IMultiLingualText
{
  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final MultiLingualText m_aMLT;

  public MultiLingualTextThreadSafe ()
  {
    m_aMLT = new MultiLingualText ();
  }

  /**
   * Constructor especially for the static TextProvider.createXXX methods
   * 
   * @param aSimpleMLT
   *        The simple multi lingual text to use.
   */
  public MultiLingualTextThreadSafe (@Nonnull final ISimpleMultiLingualText aSimpleMLT)
  {
    if (aSimpleMLT == null)
      throw new NullPointerException ("MLT");

    // Create a copy of the multilingual text!
    m_aMLT = new MultiLingualText (aSimpleMLT);
  }

  public MultiLingualTextThreadSafe (@Nonnull final IReadonlyMultiLingualText aMLT)
  {
    if (aMLT == null)
      throw new NullPointerException ("mlt");

    // Create a copy of the multilingual text!
    m_aMLT = new MultiLingualText (aMLT);
  }

  @Nullable
  public String getText (final Locale aContentLocale)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.getText (aContentLocale);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public String getTextWithLocaleFallback (final Locale aContentLocale)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.getTextWithLocaleFallback (aContentLocale);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public String getTextWithArgs (final Locale aContentLocale, @Nullable final Object... aArgs)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.getTextWithArgs (aContentLocale, aArgs);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public String getTextWithLocaleFallbackAndArgs (final Locale aContentLocale, @Nullable final Object... aArgs)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.getTextWithLocaleFallbackAndArgs (aContentLocale, aArgs);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange addText (final Locale aContentLocale, @Nullable final String sText)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aMLT.addText (aContentLocale, sText);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange setText (final Locale aContentLocale, @Nullable final String sText)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aMLT.setText (aContentLocale, sText);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public boolean containsLocale (final Locale aContentLocale)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.containsLocale (aContentLocale);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean containsLocaleWithFallback (final Locale aContentLocale)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.containsLocaleWithFallback (aContentLocale);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Map <Locale, String> getMap ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newMap (m_aMLT.internalGetMap ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public int size ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.size ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean isEmpty ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMLT.isEmpty ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public Set <Locale> getAllLocales ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newSet (m_aMLT.internalGetAllLocales ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange removeText (@Nonnull final Locale aContentLocale)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aMLT.removeText (aContentLocale);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange clear ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aMLT.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange assignFrom (@Nonnull final IReadonlyMultiLingualText aMLT)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aMLT.assignFrom (aMLT);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  public void addChangeNotifier (@Nonnull final IChangeNotify <IMultiLingualText> aCallback)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aMLT.addChangeNotifier (aCallback);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof MultiLingualTextThreadSafe))
      return false;
    final MultiLingualTextThreadSafe rhs = (MultiLingualTextThreadSafe) o;
    return m_aMLT.equals (rhs.m_aMLT);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aMLT).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("mlt", m_aMLT).toString ();
  }

  @Nonnull
  public static IMultiLingualText createFromMap (@Nonnull final Map <String, String> aMap)
  {
    final IMultiLingualText ret = new MultiLingualTextThreadSafe ();
    for (final Entry <String, String> aEntry : aMap.entrySet ())
    {
      final String sText = aEntry.getValue ();
      if (sText != null)
        ret.setText (LocaleCache.getLocale (aEntry.getKey ()), sText);
    }
    return ret;
  }
}
