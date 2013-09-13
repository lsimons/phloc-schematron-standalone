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
package com.phloc.commons.error;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.phloc.commons.ICloneable;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.state.EChange;
import com.phloc.commons.state.IClearable;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Contains a list of resource errors and some sanity access methods.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class ResourceErrorGroup implements IResourceErrorGroup, ICloneable <ResourceErrorGroup>, IClearable
{
  private final List <IResourceError> m_aErrors = new ArrayList <IResourceError> ();

  public ResourceErrorGroup ()
  {}

  public ResourceErrorGroup (@Nonnull final IResourceError... aResourceErrors)
  {
    if (aResourceErrors == null)
      throw new NullPointerException ("resourceErrors");
    for (final IResourceError aResourceError : aResourceErrors)
    {
      if (aResourceError == null)
        throw new NullPointerException ("resourceError");
      m_aErrors.add (aResourceError);
    }
  }

  public ResourceErrorGroup (@Nonnull final Iterable <? extends IResourceError> aResourceErrors)
  {
    if (aResourceErrors == null)
      throw new NullPointerException ("resourceErrors");
    for (final IResourceError aResourceError : aResourceErrors)
    {
      if (aResourceError == null)
        throw new NullPointerException ("resourceError");
      m_aErrors.add (aResourceError);
    }
  }

  /**
   * Add a new resource error item.
   * 
   * @param aResourceError
   *        The resource error to be added. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ResourceErrorGroup addResourceError (@Nonnull final IResourceError aResourceError)
  {
    if (aResourceError == null)
      throw new NullPointerException ("resourceError");
    m_aErrors.add (aResourceError);
    return this;
  }

  /**
   * Add a all resource errors of the other group
   * 
   * @param aResourceErrorGroup
   *        The resource error group to be added. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ResourceErrorGroup addResourceErrorGroup (@Nonnull final IResourceErrorGroup aResourceErrorGroup)
  {
    if (aResourceErrorGroup == null)
      throw new NullPointerException ("resourceErrorGroup");
    m_aErrors.addAll (aResourceErrorGroup.getAllResourceErrors ());
    return this;
  }

  public boolean containsOnlySuccess ()
  {
    if (m_aErrors.isEmpty ())
      return false;
    for (final IResourceError aError : m_aErrors)
      if (aError.isFailure ())
        return false;
    return true;
  }

  public boolean containsAtLeastOneSuccess ()
  {
    for (final IResourceError aError : m_aErrors)
      if (aError.isSuccess ())
        return true;
    return false;
  }

  public boolean containsNoSuccess ()
  {
    for (final IResourceError aError : m_aErrors)
      if (aError.isSuccess ())
        return false;
    return true;
  }

  @Nonnegative
  public int getSuccessCount ()
  {
    int ret = 0;
    for (final IResourceError aError : m_aErrors)
      if (aError.isSuccess ())
        ret++;
    return ret;
  }

  public boolean containsOnlyFailure ()
  {
    if (m_aErrors.isEmpty ())
      return false;
    for (final IResourceError aError : m_aErrors)
      if (aError.isSuccess ())
        return false;
    return true;
  }

  public boolean containsAtLeastOneFailure ()
  {
    for (final IResourceError aError : m_aErrors)
      if (aError.isFailure ())
        return true;
    return false;
  }

  public boolean containsNoFailure ()
  {
    for (final IResourceError aError : m_aErrors)
      if (aError.isFailure ())
        return false;
    return true;
  }

  @Nonnegative
  public int getFailureCount ()
  {
    int ret = 0;
    for (final IResourceError aError : m_aErrors)
      if (aError.isFailure ())
        ret++;
    return ret;
  }

  public boolean containsOnlyError ()
  {
    if (m_aErrors.isEmpty ())
      return false;
    for (final IResourceError aError : m_aErrors)
      if (aError.isNoError ())
        return false;
    return true;
  }

  public boolean containsAtLeastOneError ()
  {
    for (final IResourceError aError : m_aErrors)
      if (aError.isError ())
        return true;
    return false;
  }

  public boolean containsNoError ()
  {
    for (final IResourceError aError : m_aErrors)
      if (aError.isError ())
        return false;
    return true;
  }

  @Nonnegative
  public int getErrorCount ()
  {
    int ret = 0;
    for (final IResourceError aError : m_aErrors)
      if (aError.isError ())
        ret++;
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ResourceErrorGroup getAllFailures ()
  {
    final ResourceErrorGroup ret = new ResourceErrorGroup ();
    for (final IResourceError aError : m_aErrors)
      if (aError.isFailure ())
        ret.addResourceError (aError);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ResourceErrorGroup getAllErrors ()
  {
    final ResourceErrorGroup ret = new ResourceErrorGroup ();
    for (final IResourceError aError : m_aErrors)
      if (aError.isError ())
        ret.addResourceError (aError);
    return ret;
  }

  @Nonnull
  public EErrorLevel getMostSevereErrorLevel ()
  {
    EErrorLevel eRet = EErrorLevel.SUCCESS;
    for (final IResourceError aError : m_aErrors)
      if (eRet == null || aError.getErrorLevel ().isMoreSevereThan (eRet))
        eRet = aError.getErrorLevel ();
    return eRet;
  }

  @Nonnegative
  public int size ()
  {
    return m_aErrors.size ();
  }

  public boolean isEmpty ()
  {
    return m_aErrors.isEmpty ();
  }

  @Nonnull
  public ResourceErrorGroup getClone ()
  {
    return new ResourceErrorGroup (m_aErrors);
  }

  @Nonnull
  public Iterator <IResourceError> iterator ()
  {
    return m_aErrors.iterator ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IResourceError> getAllResourceErrors ()
  {
    return ContainerHelper.newList (m_aErrors);
  }

  @Nonnull
  public EChange clear ()
  {
    if (m_aErrors.isEmpty ())
      return EChange.UNCHANGED;
    m_aErrors.clear ();
    return EChange.CHANGED;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ResourceErrorGroup rhs = (ResourceErrorGroup) o;
    return m_aErrors.equals (rhs.m_aErrors);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aErrors).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("errors", m_aErrors).toString ();
  }
}
