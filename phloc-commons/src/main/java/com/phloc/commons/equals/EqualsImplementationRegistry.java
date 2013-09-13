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
package com.phloc.commons.equals;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UseDirectEqualsAndHashCode;
import com.phloc.commons.lang.ClassHelper;
import com.phloc.commons.lang.ClassHierarchyCache;
import com.phloc.commons.lang.ServiceLoaderUtils;
import com.phloc.commons.state.EChange;

/**
 * The default implementation of {@link IEqualsImplementationRegistry}.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public final class EqualsImplementationRegistry implements IEqualsImplementationRegistry
{
  private static final class ArrayEqualsImplementation implements IEqualsImplementation
  {
    public ArrayEqualsImplementation ()
    {}

    public boolean areEqual (final Object aObj1, final Object aObj2)
    {
      final Object [] aArray1 = (Object []) aObj1;
      final Object [] aArray2 = (Object []) aObj2;
      // Size check
      final int nLength = aArray1.length;
      if (nLength != aArray2.length)
        return false;
      // Content check
      for (int i = 0; i < nLength; i++)
        if (!EqualsImplementationRegistry.areEqual (aArray1[i], aArray2[i]))
          return false;
      return true;
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (EqualsImplementationRegistry.class);
  private static final EqualsImplementationRegistry s_aInstance = new EqualsImplementationRegistry ();

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();

  // Use a weak hash map, because the key is a class
  private final Map <Class <?>, IEqualsImplementation> m_aMap = new WeakHashMap <Class <?>, IEqualsImplementation> ();

  // Cache for classes where direct implementation should be used
  private final Map <String, Boolean> m_aDirectEquals = new HashMap <String, Boolean> ();

  // Cache for classes that implement equals directly
  private final Map <String, Boolean> m_aImplementsEquals = new HashMap <String, Boolean> ();

  private EqualsImplementationRegistry ()
  {
    // Register all implementations via SPI
    for (final IEqualsImplementationRegistrarSPI aRegistrar : ServiceLoaderUtils.getAllSPIImplementations (IEqualsImplementationRegistrarSPI.class))
      aRegistrar.registerEqualsImplementations (this);
  }

  @Nonnull
  public static EqualsImplementationRegistry getInstance ()
  {
    return s_aInstance;
  }

  public void registerEqualsImplementation (@Nonnull final Class <?> aClass, @Nonnull final IEqualsImplementation aImpl)
  {
    if (aClass == null)
      throw new NullPointerException ("class");
    if (aImpl == null)
      throw new NullPointerException ("implementation");

    if (aClass.equals (Object.class))
      throw new IllegalArgumentException ("You cannot provide an equals implementation for Object.class!");

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aMap.containsKey (aClass))
        s_aLogger.warn ("Another equals implementation for class " + aClass + " is already implemented!");
      else
        m_aMap.put (aClass, aImpl);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public EChange unregisterEqualsImplementation (@Nonnull final Class <?> aClass)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (m_aMap.remove (aClass) != null);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  private boolean _isUseDirectEquals (@Nonnull final Class <?> aClass)
  {
    final String sClassName = aClass.getName ();

    Boolean aUseDirectEquals;
    m_aRWLock.readLock ().lock ();
    try
    {
      aUseDirectEquals = m_aDirectEquals.get (sClassName);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    if (aUseDirectEquals == null)
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aUseDirectEquals = m_aDirectEquals.get (sClassName);
        if (aUseDirectEquals == null)
        {
          // Determine
          final boolean bHasAnnotation = aClass.getAnnotation (UseDirectEqualsAndHashCode.class) != null;
          aUseDirectEquals = Boolean.valueOf (bHasAnnotation);
          m_aDirectEquals.put (sClassName, aUseDirectEquals);
        }
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }

    return aUseDirectEquals.booleanValue ();
  }

  private boolean _implementsEqualsItself (@Nonnull final Class <?> aClass)
  {
    final String sClassName = aClass.getName ();

    Boolean aImplementsEqualsItself;
    m_aRWLock.readLock ().lock ();
    try
    {
      aImplementsEqualsItself = m_aImplementsEquals.get (sClassName);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    if (aImplementsEqualsItself == null)
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Try again in write lock
        aImplementsEqualsItself = m_aImplementsEquals.get (sClassName);
        if (aImplementsEqualsItself == null)
        {
          // Determine
          boolean bRet = false;
          try
          {
            final Method aMethod = aClass.getDeclaredMethod ("equals", Object.class);
            if (aMethod != null && aMethod.getReturnType ().equals (boolean.class))
              bRet = true;
          }
          catch (final NoSuchMethodException ex)
          {
            // ignore
          }
          aImplementsEqualsItself = Boolean.valueOf (bRet);
          m_aImplementsEquals.put (sClassName, aImplementsEqualsItself);
        }
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }

    return aImplementsEqualsItself.booleanValue ();
  }

  @Nullable
  public IEqualsImplementation getBestMatchingEqualsImplementation (@Nullable final Class <?> aClass)
  {
    if (aClass != null)
    {
      IEqualsImplementation aMatchingImplementation = null;
      Class <?> aMatchingClass = null;

      // No check required?
      if (_isUseDirectEquals (aClass))
        return null;

      m_aRWLock.readLock ().lock ();
      try
      {
        // Check for an exact match first
        aMatchingImplementation = m_aMap.get (aClass);
        if (aMatchingImplementation != null)
          aMatchingClass = aClass;
        else
        {
          // Scan hierarchy in most efficient way
          for (final WeakReference <Class <?>> aCurWRClass : ClassHierarchyCache.getClassHierarchyIterator (aClass))
          {
            final Class <?> aCurClass = aCurWRClass.get ();
            if (aCurClass != null)
            {
              final IEqualsImplementation aImpl = m_aMap.get (aCurClass);
              if (aImpl != null)
              {
                aMatchingImplementation = aImpl;
                aMatchingClass = aCurClass;
                if (s_aLogger.isDebugEnabled ())
                  s_aLogger.debug ("Found hierarchical match with class " +
                                   aMatchingClass +
                                   " when searching for " +
                                   aClass);
                break;
              }
            }
          }
        }
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }

      // Do this outside of the lock for performance reasons
      if (aMatchingImplementation != null)
      {
        // If the matching implementation is for an interface and the
        // implementation class implements equals, use the one from the class
        // Example: a converter for "Map" is registered, but "LRUCache" comes
        // with its own "equals" implementation
        if (ClassHelper.isInterface (aMatchingClass) && _implementsEqualsItself (aClass))
        {
          // Remember to use direct implementation
          m_aRWLock.writeLock ().lock ();
          try
          {
            m_aDirectEquals.put (aClass.getName (), Boolean.TRUE);
          }
          finally
          {
            m_aRWLock.writeLock ().unlock ();
          }
          return null;
        }

        if (!aMatchingClass.equals (aClass))
        {
          // We found a match by walking the hierarchy -> put that match in the
          // direct hit list for further speed up
          registerEqualsImplementation (aClass, aMatchingImplementation);
        }

        return aMatchingImplementation;
      }

      // Handle arrays specially, because we cannot register a converter for
      // every potential array class (but we allow for special implementations)
      if (ClassHelper.isArrayClass (aClass))
        return new ArrayEqualsImplementation ();

      // Remember to use direct implementation
      m_aRWLock.writeLock ().lock ();
      try
      {
        m_aDirectEquals.put (aClass.getName (), Boolean.TRUE);
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }

    // No special handler found
    if (s_aLogger.isDebugEnabled ())
      s_aLogger.debug ("Found no equals implementation for " + aClass);

    // Definitely no special implementation
    return null;
  }

  public static <T> boolean areEqual (@Nullable final T aObj1, @Nullable final T aObj2)
  {
    // Same object - check first
    if (aObj1 == aObj2)
      return true;

    // Is only one value null?
    if (aObj1 == null || aObj2 == null)
      return false;

    // Check whether the implementation classes are identical
    final Class <?> aClass1 = aObj1.getClass ();
    final Class <?> aClass2 = aObj2.getClass ();
    if (!aClass1.equals (aClass2))
    {
      // Not the same class -> not equal!
      return false;
    }

    // Same class
    final IEqualsImplementation aImpl = s_aInstance.getBestMatchingEqualsImplementation (aClass1);

    // Start the main equals check
    boolean bAreEqual;
    if (aImpl == null)
    {
      // No special implementation found
      bAreEqual = aObj1.equals (aObj2);
    }
    else
    {
      // A special implementation was found!
      bAreEqual = aImpl.areEqual (aObj1, aObj2);
    }
    return bAreEqual;
  }
}
