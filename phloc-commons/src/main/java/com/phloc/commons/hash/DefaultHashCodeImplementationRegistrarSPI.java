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
package com.phloc.commons.hash;

import java.io.File;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.io.file.FilenameHelper;

/**
 * This class registers the default hash code implementations. The
 * implementations in here should be aligned with the implementations in the
 * {@link com.phloc.commons.equals.DefaultEqualsImplementationRegistrarSPI}
 * 
 * @author Philip Helger
 */
@IsSPIImplementation
public final class DefaultHashCodeImplementationRegistrarSPI implements IHashCodeImplementationRegistrarSPI
{
  public void registerHashCodeImplementations (@Nonnull final IHashCodeImplementationRegistry aRegistry)
  {
    // StringBuffer does not implement hashCode!
    aRegistry.registerHashCodeImplementation (StringBuffer.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        return aObj.toString ().hashCode ();
      }
    });

    // StringBuilder does not implement hashCode!
    aRegistry.registerHashCodeImplementation (StringBuilder.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        return aObj.toString ().hashCode ();
      }
    });

    // Node does not implement hashCode
    aRegistry.registerHashCodeImplementation (Node.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final Node aRealObj = (Node) aObj;
        HashCodeGenerator aHC = new HashCodeGenerator (aRealObj).append (aRealObj.getNodeType ())
                                                                .append (aRealObj.getNodeName ())
                                                                .append (aRealObj.getLocalName ())
                                                                .append (aRealObj.getNamespaceURI ())
                                                                .append (aRealObj.getPrefix ())
                                                                .append (aRealObj.getNodeValue ());

        // For all children
        final NodeList aNL = aRealObj.getChildNodes ();
        final int nLength = aNL.getLength ();
        aHC = aHC.append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aNL.item (i));
        return aHC.getHashCode ();
      }
    });

    // AtomicBoolean does not implement hashCode!
    aRegistry.registerHashCodeImplementation (AtomicBoolean.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        return HashCodeCalculator.append (0, ((AtomicBoolean) aObj).get ());
      }
    });

    // AtomicInteger does not implement hashCode!
    aRegistry.registerHashCodeImplementation (AtomicInteger.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        return HashCodeCalculator.append (0, ((AtomicInteger) aObj).get ());
      }
    });

    // AtomicLong does not implement hashCode!
    aRegistry.registerHashCodeImplementation (AtomicLong.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        return HashCodeCalculator.append (0, ((AtomicLong) aObj).get ());
      }
    });

    // Special handling for arrays
    // (Object[] is handled internally)
    aRegistry.registerHashCodeImplementation (boolean [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final boolean [] aArray = (boolean []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (byte [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final byte [] aArray = (byte []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (char [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final char [] aArray = (char []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (double [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final double [] aArray = (double []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (float [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final float [] aArray = (float []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (int [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final int [] aArray = (int []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (long [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final long [] aArray = (long []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });
    aRegistry.registerHashCodeImplementation (short [].class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final short [] aArray = (short []) aObj;
        final int nLength = aArray.length;
        HashCodeGenerator aHC = new HashCodeGenerator (aObj.getClass ()).append (nLength);
        for (int i = 0; i < nLength; ++i)
          aHC = aHC.append (aArray[i]);
        return aHC.getHashCode ();
      }
    });

    // Special handling for Map
    aRegistry.registerHashCodeImplementation (Map.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final Map <?, ?> aRealObj = (Map <?, ?>) aObj;
        HashCodeGenerator aHC = new HashCodeGenerator (aRealObj).append (aRealObj.size ());
        for (final Map.Entry <?, ?> aEntry : aRealObj.entrySet ())
          aHC = aHC.append (aEntry.getKey ()).append (aEntry.getValue ());
        return aHC.getHashCode ();
      }
    });

    // Special handling for Collection
    aRegistry.registerHashCodeImplementation (Collection.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final Collection <?> aRealObj = (Collection <?>) aObj;
        HashCodeGenerator aHC = new HashCodeGenerator (aRealObj).append (aRealObj.size ());
        for (final Object aMember : aRealObj)
          aHC = aHC.append (aMember);
        return aHC.getHashCode ();
      }
    });

    // Special handling for Iterator
    aRegistry.registerHashCodeImplementation (Iterator.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final Iterator <?> aRealObj = (Iterator <?>) aObj;
        HashCodeGenerator aHC = new HashCodeGenerator (aRealObj);
        while (aRealObj.hasNext ())
          aHC = aHC.append (aRealObj.next ());
        return aHC.getHashCode ();
      }
    });

    // Special handling for Enumeration
    aRegistry.registerHashCodeImplementation (Enumeration.class, new IHashCodeImplementation ()
    {
      public int getHashCode (final Object aObj)
      {
        final Enumeration <?> aRealObj = (Enumeration <?>) aObj;
        HashCodeGenerator aHC = new HashCodeGenerator (aRealObj);
        while (aRealObj.hasMoreElements ())
          aHC = aHC.append (aRealObj.nextElement ());
        return aHC.getHashCode ();
      }
    });

    // Special handling for File
    aRegistry.registerHashCodeImplementation (File.class, new IHashCodeImplementation ()
    {
      public int getHashCode (@Nonnull final Object aObj)
      {
        final File aFile = (File) aObj;
        return FilenameHelper.getCleanPath (aFile.getAbsoluteFile ()).hashCode ();
      }
    });
  }
}
