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
package com.phloc.commons.microdom.serialize;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.streamprovider.StringInputStreamProvider;
import com.phloc.commons.microdom.IMicroNode;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

/**
 * A special input stream provider that takes an existing {@link IMicroNode} and
 * converts it to a byte array.
 * 
 * @author Philip Helger
 */
public class MicroDOMInputStreamProvider extends StringInputStreamProvider
{
  /**
   * Constructor for MicroNodes using the default charset.
   * 
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @see XMLWriterSettings#DEFAULT_XML_CHARSET
   */
  public MicroDOMInputStreamProvider (@Nonnull final IMicroNode aNode)
  {
    this (aNode, XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
  }

  /**
   * Constructor for MicroNodes.
   * 
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param sCharset
   *        The charset to use. May not be <code>null</code>.
   */
  @Deprecated
  public MicroDOMInputStreamProvider (@Nonnull final IMicroNode aNode, @Nonnull @Nonempty final String sCharset)
  {
    super (MicroWriter.getNodeAsString (aNode, new XMLWriterSettings ().setCharset (sCharset)), sCharset);
  }

  /**
   * Constructor for MicroNodes.
   * 
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public MicroDOMInputStreamProvider (@Nonnull final IMicroNode aNode, @Nonnull final Charset aCharset)
  {
    super (MicroWriter.getNodeAsString (aNode, new XMLWriterSettings ().setCharset (aCharset)), aCharset);
  }
}
