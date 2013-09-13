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
package com.phloc.commons.microdom.convert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.typeconvert.TypeConverterException;
import com.phloc.commons.typeconvert.TypeConverterException.EReason;

/**
 * A utility class for converting objects from and to {@link IMicroElement}.<br>
 * The functionality is a special case of the
 * {@link com.phloc.commons.typeconvert.TypeConverter} as we need a parameter
 * for conversion in this case.<br>
 * All converters are registered in the {@link MicroTypeConverterRegistry}.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public final class MicroTypeConverter
{
  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final MicroTypeConverter s_aInstance = new MicroTypeConverter ();

  private MicroTypeConverter ()
  {}

  @Nullable
  public static IMicroElement convertToMicroElement (@Nullable final Object aObject,
                                                     @Nonnull @Nonempty final String sTagName)
  {
    // Use a null namespace
    return convertToMicroElement (aObject, null, sTagName);
  }

  @Nullable
  public static IMicroElement convertToMicroElement (@Nullable final Object aObject,
                                                     @Nullable final String sNamespaceURI,
                                                     @Nonnull @Nonempty final String sTagName) throws TypeConverterException
  {
    if (StringHelper.hasNoText (sTagName))
      throw new IllegalArgumentException ("tagName is empty");

    if (aObject == null)
      return null;

    // Lookup converter
    final Class <?> aSrcClass = aObject.getClass ();
    final IMicroTypeConverter aConverter = MicroTypeConverterRegistry.getConverterToMicroElement (aSrcClass);
    if (aConverter == null)
      throw new TypeConverterException (aSrcClass, IMicroElement.class, EReason.NO_CONVERTER_FOUND);

    // Perform conversion
    final IMicroElement ret = aConverter.convertToMicroElement (aObject, sNamespaceURI, sTagName);
    if (ret == null)
      throw new TypeConverterException (aSrcClass, IMicroElement.class, EReason.CONVERSION_FAILED);
    return ret;
  }

  @Nullable
  public static <DSTTYPE> DSTTYPE convertToNative (@Nullable final IMicroElement aElement,
                                                   @Nonnull final Class <DSTTYPE> aDstClass) throws TypeConverterException
  {
    if (aDstClass == null)
      throw new NullPointerException ("destinationClass");

    if (aElement == null)
      return null;

    // Lookup converter
    final IMicroTypeConverter aConverter = MicroTypeConverterRegistry.getConverterToNative (aDstClass);
    if (aConverter == null)
      throw new TypeConverterException (IMicroElement.class, aDstClass, EReason.NO_CONVERTER_FOUND);

    // Perform conversion
    final DSTTYPE ret = aDstClass.cast (aConverter.convertToNative (aElement));
    if (ret == null)
      throw new TypeConverterException (IMicroElement.class, aDstClass, EReason.CONVERSION_FAILED);
    return ret;
  }
}
