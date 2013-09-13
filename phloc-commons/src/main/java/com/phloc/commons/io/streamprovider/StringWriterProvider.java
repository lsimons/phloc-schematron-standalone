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
package com.phloc.commons.io.streamprovider;

import java.io.Writer;

import javax.annotation.Nonnull;

import com.phloc.commons.io.IWriterProvider;
import com.phloc.commons.io.streams.NonBlockingStringWriter;
import com.phloc.commons.string.ToStringGenerator;

/**
 * An {@link Writer} provider based on a {@link String}.
 * 
 * @author Philip Helger
 */
public class StringWriterProvider implements IWriterProvider
{
  public StringWriterProvider ()
  {}

  @Nonnull
  public final NonBlockingStringWriter getWriter ()
  {
    return new NonBlockingStringWriter ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).toString ();
  }
}
