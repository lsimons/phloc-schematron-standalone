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
package com.phloc.commons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.priviledged.AccessControllerHelper;
import com.phloc.commons.priviledged.PrivilegedActionSystemClearProperty;
import com.phloc.commons.priviledged.PrivilegedActionSystemGetProperties;
import com.phloc.commons.priviledged.PrivilegedActionSystemGetProperty;
import com.phloc.commons.priviledged.PrivilegedActionSystemSetProperty;

/**
 * This class wraps all the Java system properties like version number etc.
 * 
 * @author Philip Helger
 */
@Immutable
public final class SystemProperties
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SystemProperties.class);
  private static final Set <String> s_aWarnedPropertyNames = new HashSet <String> ();

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final SystemProperties s_aInstance = new SystemProperties ();

  private SystemProperties ()
  {}

  @Nullable
  public static String getPropertyValueOrNull (@Nullable final String sKey)
  {
    return sKey == null ? null : AccessControllerHelper.call (new PrivilegedActionSystemGetProperty (sKey));
  }

  @Nullable
  public static String getPropertyValue (@Nullable final String sKey)
  {
    String ret = null;
    if (sKey != null)
    {
      ret = getPropertyValueOrNull (sKey);
      if (ret == null && s_aWarnedPropertyNames.add (sKey))
      {
        // don't show anything for phloc ;-)
        if (!sKey.contains ("phloc"))
        {
          // Warn about each property once
          s_aLogger.warn ("System property '" + sKey + "' is not set!");
        }
      }
    }
    return ret;
  }

  /**
   * Set a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   * 
   * @param sKey
   *        The key of the system property. May not be <code>null</code>.
   * @param sValue
   *        The value of the system property. If the value is <code>null</code>
   *        the property is removed.
   */
  public static void setPropertyValue (@Nonnull final String sKey, @Nullable final String sValue)
  {
    if (sValue == null)
      removePropertyValue (sKey);
    else
      AccessControllerHelper.run (new PrivilegedActionSystemSetProperty (sKey, sValue));
  }

  /**
   * Remove a system property value under consideration of an eventually present
   * {@link SecurityManager}.
   * 
   * @param sKey
   *        The key of the system property to be removed. May not be
   *        <code>null</code>.
   * @return the previous string value of the system property, or
   *         <code>null</code> if there was no property with that key.
   */
  @Nullable
  public static String removePropertyValue (@Nonnull final String sKey)
  {
    return AccessControllerHelper.call (new PrivilegedActionSystemClearProperty (sKey));
  }

  @Nullable
  public static String getJavaVersion ()
  {
    return getPropertyValue ("java.version");
  }

  @Nullable
  public static String getJavaVendor ()
  {
    return getPropertyValue ("java.vendor");
  }

  @Nullable
  public static String getJavaVendorURL ()
  {
    return getPropertyValue ("java.vendor.url");
  }

  @Nullable
  public static String getJavaHome ()
  {
    return getPropertyValue ("java.home");
  }

  @Nullable
  public static String getJavaClassVersion ()
  {
    return getPropertyValue ("java.class.version");
  }

  @Nullable
  public static String getJavaClassPath ()
  {
    return getPropertyValue ("java.class.path");
  }

  @Nullable
  public static String getJavaLibraryPath ()
  {
    return getPropertyValue ("java.library.path");
  }

  @Nullable
  public static String getOsName ()
  {
    return getPropertyValue ("os.name");
  }

  @Nullable
  public static String getOsArch ()
  {
    return getPropertyValue ("os.arch");
  }

  @Nullable
  public static String getOsVersion ()
  {
    return getPropertyValue ("os.version");
  }

  @Nullable
  public static String getFileSeparator ()
  {
    return getPropertyValue ("file.separator");
  }

  @Nullable
  public static String getPathSeparator ()
  {
    return getPropertyValue ("path.separator");
  }

  @Nullable
  public static String getLineSeparator ()
  {
    return getPropertyValue ("line.separator");
  }

  @Nullable
  public static String getUserName ()
  {
    return getPropertyValue ("user.name");
  }

  @Nullable
  public static String getUserHome ()
  {
    return getPropertyValue ("user.home");
  }

  @Nullable
  public static String getUserDir ()
  {
    return getPropertyValue ("user.dir");
  }

  @Nullable
  public static String getJavaVmSpecificationVersion ()
  {
    return getPropertyValue ("java.vm.specification.version");
  }

  @Nullable
  public static String getJavaVmSpecificationVendor ()
  {
    return getPropertyValue ("java.vm.specification.vendor");
  }

  @Nullable
  public static String getJavaVmSpecificationUrl ()
  {
    return getPropertyValue ("java.vm.specification.url");
  }

  @Nullable
  public static String getJavaVmVersion ()
  {
    return getPropertyValue ("java.vm.version");
  }

  @Nullable
  public static String getJavaVmVendor ()
  {
    return getPropertyValue ("java.vm.vendor");
  }

  @Nullable
  public static String getJavaVmUrl ()
  {
    return getPropertyValue ("java.vm.url");
  }

  @Nullable
  public static String getJavaSpecificationVersion ()
  {
    return getPropertyValue ("java.specification.version");
  }

  @Nullable
  public static String getJavaSpecificationVendor ()
  {
    return getPropertyValue ("java.specification.vendor");
  }

  @Nullable
  public static String getJavaSpecificationUrl ()
  {
    return getPropertyValue ("java.specification.url");
  }

  @DevelopersNote ("This property is not part of the language but part of the Sun SDK")
  @Nullable
  public static String getTmpDir ()
  {
    return getPropertyValue ("java.io.tmpdir");
  }

  /**
   * @return A set with all defined property names. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <String> getAllPropertyNames ()
  {
    return new HashSet <String> (getAllProperties ().keySet ());
  }

  /**
   * @return A map with all system properties where the key is the system
   *         property name and the value is the system property value.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Map <String, String> getAllProperties ()
  {
    final Map <String, String> ret = new HashMap <String, String> ();
    final Properties aProperties = AccessControllerHelper.call (new PrivilegedActionSystemGetProperties ());
    if (aProperties != null)
      for (final Map.Entry <Object, Object> aEntry : aProperties.entrySet ())
      {
        final String sKey = (String) aEntry.getKey ();
        ret.put (sKey, (String) aEntry.getValue ());
      }
    return ret;
  }

  /**
   * Check if a system property with the given name exists.
   * 
   * @param sPropertyName
   *        The name of the property.
   * @return <code>true</code> if such a system property is present,
   *         <code>false</code> otherwise
   */
  public static boolean containsPropertyName (final String sPropertyName)
  {
    return getAllProperties ().containsKey (sPropertyName);
  }
}
