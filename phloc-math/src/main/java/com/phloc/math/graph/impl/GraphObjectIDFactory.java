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
package com.phloc.math.graph.impl;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.idfactory.GlobalIDFactory;
import com.phloc.commons.idfactory.IIDFactory;

/**
 * Factory class that handles the generation of graph object IDs. It allows to
 * provide another ID factory. If no custom ID factory is present (which is the
 * default), {@link GlobalIDFactory#getNewStringID()} is used to create Graph
 * object IDs.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public final class GraphObjectIDFactory
{
  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();
  private static IIDFactory <String> s_aIDFactory;

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final GraphObjectIDFactory s_aInstance = new GraphObjectIDFactory ();

  private GraphObjectIDFactory ()
  {}

  /**
   * @return The custom ID factory if defined. May be <code>null</code> if no
   *         custom ID factory is set.
   */
  @Nullable
  public static IIDFactory <String> getIDFactory ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aIDFactory;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Set a custom ID factory.
   * 
   * @param aIDFactory
   *        The new ID factory to use. May be <code>null</code> to indicate that
   *        the default should be used.
   */
  public static void setIDFactory (@Nullable final IIDFactory <String> aIDFactory)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aIDFactory = aIDFactory;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Get a new ID for a graph object. If a custom ID factory is defined, the ID
   * is retrieved from there. Otherwise the ID is retrieved from
   * {@link GlobalIDFactory}.
   * 
   * @return A new graph object ID. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  public static String createNewGraphObjectID ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aIDFactory != null ? s_aIDFactory.getNewID () : GlobalIDFactory.getNewStringID ();
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }
}
