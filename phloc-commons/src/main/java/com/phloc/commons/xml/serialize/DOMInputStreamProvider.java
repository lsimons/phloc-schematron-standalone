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
package com.phloc.commons.xml.serialize;

import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import org.w3c.dom.Node;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.streamprovider.StringInputStreamProvider;

/**
 * A special input stream provider that takes an existing {@link Node} and
 * converts it to a byte array.
 * 
 * @author Philip Helger
 */
public class DOMInputStreamProvider extends StringInputStreamProvider
{
  /**
   * Constructor for W3C nodes using the default XML charset.
   * 
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   */
  public DOMInputStreamProvider (@Nonnull final Node aNode)
  {
    this (aNode, XMLWriterSettings.DEFAULT_XML_CHARSET_OBJ);
  }

  /**
   * Constructor for W3C nodes.
   * 
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param sCharset
   *        The charset to use. May not be <code>null</code>.
   */
  @Deprecated
  public DOMInputStreamProvider (@Nonnull final Node aNode, @Nonnull @Nonempty final String sCharset)
  {
    super (XMLWriter.getNodeAsString (aNode, new XMLWriterSettings ().setCharset (sCharset)), sCharset);
  }

  /**
   * Constructor for W3C nodes.
   * 
   * @param aNode
   *        The node to be streamed. May not be <code>null</code>.
   * @param aCharset
   *        The charset to use. May not be <code>null</code>.
   */
  public DOMInputStreamProvider (@Nonnull final Node aNode, @Nonnull final Charset aCharset)
  {
    super (XMLWriter.getNodeAsString (aNode, new XMLWriterSettings ().setCharset (aCharset)), aCharset);
  }
}
