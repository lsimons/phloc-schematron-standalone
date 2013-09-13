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
package com.phloc.commons.xml;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.phloc.commons.CGlobal;
import com.phloc.commons.annotations.DevelopersNote;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.PresentForCodeCoverage;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.collections.iterate.IIterableIterator;
import com.phloc.commons.string.StringHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class contains multiple XML utility methods.
 *
 * @author Philip Helger
 */
@Immutable
public final class XMLHelper
{
  // Order is important!
  // Note: for performance reasons they are all char arrays!
  private static final char [] MASK_PATTERNS_XML10 = new char [] { 0, '&', '<', '>', '"', '\'' };

  // Control characters (except 9 - \t and 10 - \n and 13 - \r)
  private static final char [] MASK_PATTERNS_CONTROL = new char [] { 1, 2, 3, 4, 5, 6, 7, 8,
                                                                    // 9 and 10
                                                                    // are OK
                                                                    11,
                                                                    12,
                                                                    // 13 is OK
                                                                    14,
                                                                    15,
                                                                    16,
                                                                    17,
                                                                    18,
                                                                    19,
                                                                    20,
                                                                    21,
                                                                    22,
                                                                    23,
                                                                    24,
                                                                    25,
                                                                    26,
                                                                    27,
                                                                    28,
                                                                    29,
                                                                    30,
                                                                    31,
                                                                    '\u2028' };
  private static final char [] MASK_PATTERNS_ALL = ArrayHelper.getConcatenated (MASK_PATTERNS_XML10,
                                                                                MASK_PATTERNS_CONTROL);

  /**
   * IE8 emits an error when using &apos; - that's why the work around with
   * &#39; is used!<br>
   * Note: &#0; cannot be read so it is emitted as ""<br>
   */
  private static final char [][] MASK_REPLACE_XML10 = new char [] [] { "".toCharArray (),
                                                                      "&amp;".toCharArray (),
                                                                      "&lt;".toCharArray (),
                                                                      "&gt;".toCharArray (),
                                                                      "&quot;".toCharArray (),
                                                                      "&#39;".toCharArray () };

  /**
   * Control character replacements for removal<br>
   * All other numeric mappings &#1; - &#31; can only be read by XML 1.1
   */
  private static final char [][] MASK_REPLACE_CONTROL_EMPTY = new char [] [] { "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "".toCharArray (),
                                                                              "\n".toCharArray () };

  private static final char [][] MASK_REPLACE_ALL_EMPTY = ArrayHelper.getConcatenated (MASK_REPLACE_XML10,
                                                                                       MASK_REPLACE_CONTROL_EMPTY);

  /**
   * Control character replacements<br>
   * All other numeric mappings &#1; - &#31; can only be read by XML 1.1
   */
  private static final char [][] MASK_REPLACE_CONTROL_XML11 = new char [] [] { "&#1;".toCharArray (),
                                                                              "&#2;".toCharArray (),
                                                                              "&#3;".toCharArray (),
                                                                              "&#4;".toCharArray (),
                                                                              "&#5;".toCharArray (),
                                                                              "&#6;".toCharArray (),
                                                                              "&#7;".toCharArray (),
                                                                              "&#8;".toCharArray (),
                                                                              "&#11;".toCharArray (),
                                                                              "&#12;".toCharArray (),
                                                                              "&#14;".toCharArray (),
                                                                              "&#15;".toCharArray (),
                                                                              "&#16;".toCharArray (),
                                                                              "&#17;".toCharArray (),
                                                                              "&#18;".toCharArray (),
                                                                              "&#19;".toCharArray (),
                                                                              "&#20;".toCharArray (),
                                                                              "&#21;".toCharArray (),
                                                                              "&#22;".toCharArray (),
                                                                              "&#23;".toCharArray (),
                                                                              "&#24;".toCharArray (),
                                                                              "&#25;".toCharArray (),
                                                                              "&#26;".toCharArray (),
                                                                              "&#27;".toCharArray (),
                                                                              "&#28;".toCharArray (),
                                                                              "&#29;".toCharArray (),
                                                                              "&#30;".toCharArray (),
                                                                              "&#31;".toCharArray (),
                                                                              "&#2028;".toCharArray () };

  private static final char [][] MASK_REPLACE_ALL_XML11 = ArrayHelper.getConcatenated (MASK_REPLACE_XML10,
                                                                                       MASK_REPLACE_CONTROL_XML11);

  /**
   * Contains a boolean mask for all characters from 0x00-0xff which are invalid
   * (marked as true) and which are valid (marked as false)
   */
  private static final boolean [] ILLEGAL_XML_CHARS = new boolean [] { true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      false,
                                                                      false,
                                                                      true,
                                                                      true,
                                                                      false,
                                                                      true,
                                                                      true,
                                                                      // 16
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      // 32
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 48
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 64
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 80
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 96
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 112
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      true,
                                                                      // 128
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      // 144
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      true,
                                                                      // 160
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 176
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 192
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 208
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 224
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      // 240
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false,
                                                                      false };

  static
  {
    /**
     * Unicode code points in the following ranges are valid in XML 1.0
     * documents:<br>
     * U+0009, U+000A, U+000D: these are the only C0 controls accepted in XML
     * 1.0<br>
     * U+0020–U+D7FF, U+E000–U+FFFD: this excludes some (not all) non-characters
     * in the BMP (all surrogates, U+FFFE and U+FFFF are forbidden)<br>
     * U+10000–U+10FFFF: this includes all code points in supplementary planes,
     * including non-characters.<br>
     * <br>
     * XML 1.1 extends the set of allowed characters to include all the above,
     * plus the remaining characters in the range U+0001–U+001F. At the same
     * time, however, it restricts the use of C0 and C1 control characters other
     * than U+0009, U+000A, U+000D, and U+0085 by requiring them to be written
     * in escaped form (for example U+0001 must be written as &#x01; or its
     * equivalent). In the case of C1 characters, this restriction is a
     * backwards incompatibility; it was introduced to allow common encoding
     * errors to be detected.<br>
     * <br>
     * The code point U+0000 is the only character that is not permitted in any
     * XML 1.0 or 1.1 document.
     */

    // Check integrity
    if (MASK_PATTERNS_XML10.length != MASK_REPLACE_XML10.length)
      throw new IllegalStateException ("Regular arrays have different length!");
    if (MASK_PATTERNS_CONTROL.length != MASK_REPLACE_CONTROL_EMPTY.length)
      throw new IllegalStateException ("Empty arrays have different length!");
    if (MASK_PATTERNS_CONTROL.length != MASK_REPLACE_CONTROL_XML11.length)
      throw new IllegalStateException ("Control arrays have different length!");
    if (MASK_PATTERNS_ALL.length != MASK_REPLACE_ALL_EMPTY.length)
      throw new IllegalStateException ("Empty arrays have different length!");
    if (MASK_PATTERNS_ALL.length != MASK_REPLACE_ALL_XML11.length)
      throw new IllegalStateException ("Overall arrays have different length!");
  }

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final XMLHelper s_aInstance = new XMLHelper ();

  private XMLHelper ()
  {}

  /**
   * Get the first direct child element of the passed element.
   *
   * @param aStartNode
   *        The element to start searching.
   * @return <code>null</code> if the passed element does not have any direct
   *         child element.
   */
  @Nullable
  public static Element getFirstChildElement (@Nonnull final Node aStartNode)
  {
    final NodeList aNodeList = aStartNode.getChildNodes ();
    final int nLen = aNodeList.getLength ();
    for (int i = 0; i < nLen; ++i)
    {
      final Node aNode = aNodeList.item (i);
      if (aNode.getNodeType () == Node.ELEMENT_NODE)
        return (Element) aNode;
    }
    return null;
  }

  /**
   * Check if the passed node has at least one direct child element or not.
   *
   * @param aStartNode
   *        The parent element to be searched. May not be <code>null</code>.
   * @return <code>true</code> if the passed node has at least one child
   *         element, <code>false</code> otherwise.
   */
  public static boolean hasChildElementNodes (@Nonnull final Node aStartNode)
  {
    return getFirstChildElement (aStartNode) != null;
  }

  /**
   * Search all child nodes of the given for the first element that has the
   * specified tag name.
   *
   * @param aStartNode
   *        The parent element to be searched. May not be <code>null</code>.
   * @param sName
   *        The tag name to search.
   * @return <code>null</code> if the parent element has no such child element.
   */
  @Nullable
  public static Element getFirstChildElementOfName (@Nonnull final Node aStartNode, @Nullable final String sName)
  {
    final NodeList aNodeList = aStartNode.getChildNodes ();
    final int nLen = aNodeList.getLength ();
    for (int i = 0; i < nLen; ++i)
    {
      final Node aNode = aNodeList.item (i);
      if (aNode.getNodeType () == Node.ELEMENT_NODE)
      {
        final Element aElement = (Element) aNode;
        if (aElement.getTagName ().equals (sName))
          return aElement;
      }
    }
    return null;
  }

  /**
   * Get the owner document of the passed node. If the node itself is a
   * document, only a cast is performed.
   *
   * @param aNode
   *        The node to get the document from. May be <code>null</code>.
   * @return <code>null</code> if the passed node was <code>null</code>.
   */
  @Nullable
  public static Document getOwnerDocument (@Nullable final Node aNode)
  {
    return aNode == null ? null : aNode instanceof Document ? (Document) aNode : aNode.getOwnerDocument ();
  }

  @Nonnull
  public static Node append (@Nonnull final Node aParentNode, @Nullable final Object aChild)
  {
    if (aParentNode == null)
      throw new NullPointerException ("parentNode");

    if (aChild != null)
      if (aChild instanceof Document)
      {
        // Special handling for Document comes first, as this is a special case
        // of "Node"

        // Cannot add complete documents!
        append (aParentNode, ((Document) aChild).getDocumentElement ());
      }
      else
        if (aChild instanceof Node)
        {
          // directly append Node
          final Node aChildNode = (Node) aChild;
          final Document aParentDoc = getOwnerDocument (aParentNode);
          if (getOwnerDocument (aChildNode).equals (aParentDoc))
          {
            // Nodes have the same parent
            aParentNode.appendChild (aChildNode);
          }
          else
          {
            // Node to be added belongs to a different document
            aParentNode.appendChild (aParentDoc.adoptNode (aChildNode.cloneNode (true)));
          }
        }
        else
          if (aChild instanceof String)
          {
            // append a string node
            aParentNode.appendChild (getOwnerDocument (aParentNode).createTextNode ((String) aChild));
          }
          else
            if (aChild instanceof Iterable <?>)
            {
              // it's a nested collection -> recursion
              for (final Object aSubChild : (Iterable <?>) aChild)
                append (aParentNode, aSubChild);
            }
            else
              if (ArrayHelper.isArray (aChild))
              {
                // it's a nested collection -> recursion
                for (final Object aSubChild : (Object []) aChild)
                  append (aParentNode, aSubChild);
              }
              else
              {
                // unsupported type
                throw new IllegalArgumentException ("Passed object cannot be appended to a DOMNode (type=" +
                                                    aChild.getClass ().getName () +
                                                    ".");
              }
    return aParentNode;
  }

  public static void append (@Nonnull final Node aSrcNode, @Nonnull final Collection <?> aNodesToAppend)
  {
    for (final Object aNode : aNodesToAppend)
      append (aSrcNode, aNode);
  }

  @Nonnegative
  public static int getDirectChildElementCountNoNS (@Nullable final Element aParent)
  {
    return aParent == null ? 0 : ContainerHelper.getSize (getChildElementIteratorNoNS (aParent));
  }

  @Nonnegative
  public static int getDirectChildElementCountNoNS (@Nullable final Element aParent,
                                                    @Nonnull @Nonempty final String sTagName)
  {
    return aParent == null ? 0 : ContainerHelper.getSize (getChildElementIteratorNoNS (aParent, sTagName));
  }

  @Nonnegative
  public static int getDirectChildElementCountNS (@Nullable final Element aParent, @Nullable final String sNamespaceURI)
  {
    return aParent == null ? 0 : ContainerHelper.getSize (getChildElementIteratorNS (aParent, sNamespaceURI));
  }

  @Nonnegative
  public static int getDirectChildElementCountNS (@Nullable final Element aParent,
                                                  @Nullable final String sNamespaceURI,
                                                  @Nonnull @Nonempty final String sLocalName)
  {
    return aParent == null ? 0
                          : ContainerHelper.getSize (getChildElementIteratorNS (aParent, sNamespaceURI, sLocalName));
  }

  /**
   * Get an iterator over all child elements that have no namespace.
   *
   * @param aStartNode
   *        the parent element
   * @return a non-null Iterator
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNoNS (@Nonnull final Node aStartNode)
  {
    return new ChildElementIterator (aStartNode, FilterElementWithoutNamespace.getInstance ());
  }

  /**
   * Get an iterator over all child elements that have no namespace and the
   * desired tag name.
   *
   * @param aStartNode
   *        the parent element
   * @param sTagName
   *        the name of the tag that is desired
   * @return a non-null Iterator
   * @throws IllegalArgumentException
   *         if the passed tag name is null or empty
   */
  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNoNS (@Nonnull final Node aStartNode,
                                                                         @Nonnull @Nonempty final String sTagName)
  {
    if (StringHelper.hasNoText (sTagName))
      throw new IllegalArgumentException ("Passed tag name is illegal");

    return new ChildElementIterator (aStartNode, new FilterElementWithTagName (sTagName));
  }

  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNS (@Nonnull final Node aStartNode,
                                                                       @Nullable final String sNamespaceURI)
  {
    return new ChildElementIterator (aStartNode, new FilterElementWithNamespace (sNamespaceURI));
  }

  @Nonnull
  public static IIterableIterator <Element> getChildElementIteratorNS (@Nonnull final Node aStartNode,
                                                                       @Nullable final String sNamespaceURI,
                                                                       @Nonnull @Nonempty final String sLocalName)
  {
    if (StringHelper.hasNoText (sLocalName))
      throw new IllegalArgumentException ("Passed local name is illegal");

    return new ChildElementIterator (aStartNode, new FilterElementWithNamespaceAndLocalName (sNamespaceURI, sLocalName));
  }

  public static boolean hasNamespaceURI (@Nullable final Node aNode, @Nullable final String sNamespaceURI)
  {
    final String sNSURI = aNode == null ? null : aNode.getNamespaceURI ();
    return sNSURI != null && sNSURI.equals (sNamespaceURI);
  }

  /**
   * Shortcut for {@link #getPathToNode(Node, String)} using "/" as the
   * separator.
   *
   * @param aNode
   *        The node to check.
   * @return A non-<code>null</code> path.
   */
  @Nonnull
  public static String getPathToNode (@Nonnull final Node aNode)
  {
    return getPathToNode (aNode, "/");
  }

  /**
   * Get the path from root node to the passed node.
   *
   * @param aNode
   *        The node to start. May not be <code>null</code>.
   * @param sSep
   *        The separator string to use. May not be <code>null</code>.
   * @return The path to the node.
   */
  @Nonnull
  @SuppressFBWarnings ("IL_INFINITE_LOOP")
  public static String getPathToNode (@Nonnull final Node aNode, @Nonnull final String sSep)
  {
    if (aNode == null)
      throw new NullPointerException ("node");
    if (sSep == null)
      throw new NullPointerException ("separator");

    final StringBuilder aRet = new StringBuilder ();
    Node aCurNode = aNode;
    while (aCurNode != null)
    {
      final StringBuilder aName = new StringBuilder (aCurNode.getNodeName ());
      if (aCurNode.getNodeType () == Node.ELEMENT_NODE && aCurNode.getParentNode () != null)
      {
        // get index of my current element
        final Element aCurElement = (Element) aCurNode;
        int nIndex = 0;
        for (final Element x : getChildElementIteratorNoNS (aCurNode.getParentNode ()))
        {
          if (x == aCurNode)// NOPMD
            break;
          if (x.getTagName ().equals (aCurElement.getTagName ()))
            ++nIndex;
        }
        aName.append ('[').append (nIndex).append (']');
      }

      aRet.insert (0, sSep).insert (0, aName);

      // goto parent
      aCurNode = aCurNode.getParentNode ();
    }
    return aRet.toString ();
  }

  /**
   * Remove all child nodes of the given node.
   *
   * @param aElement
   *        The element whose children are to be removed.
   */
  public static void removeAllChildElements (@Nonnull final Element aElement)
  {
    while (aElement.getChildNodes ().getLength () > 0)
      aElement.removeChild (aElement.getChildNodes ().item (0));
  }

  @Nonnull
  public static char [] getMaskedXMLText (@Nonnull final EXMLVersion eXMLVersion,
                                          @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                          @Nullable final String s)
  {
    if (eIncorrectCharHandling.isTestRequired () && s != null)
    {
      final char [] aChars = s.toCharArray ();
      if (containsInvalidXMLCharacter (aChars))
      {
        eIncorrectCharHandling.notifyOnInvalidXMLCharacter (s, getAllInvalidXMLCharacters (aChars));
        if (eIncorrectCharHandling.isReplaceWithNothing ())
          return StringHelper.replaceMultiple (s, MASK_PATTERNS_ALL, MASK_REPLACE_ALL_EMPTY);
      }
    }

    if (eXMLVersion.equals (EXMLVersion.XML_10))
    {
      return StringHelper.replaceMultiple (s, MASK_PATTERNS_XML10, MASK_REPLACE_XML10);
    }
    return StringHelper.replaceMultiple (s, MASK_PATTERNS_ALL, MASK_REPLACE_ALL_XML11);
  }

  @Nonnegative
  public static int getMaskedXMLTextLength (@Nonnull final EXMLVersion eXMLVersion,
                                            @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                            @Nullable final String s)
  {
    if (StringHelper.hasNoText (s))
      return 0;

    final char [] aChars = s.toCharArray ();
    if (eIncorrectCharHandling.isTestRequired () && containsInvalidXMLCharacter (aChars))
    {
      final Set <Character> aInvalidCharacters = getAllInvalidXMLCharacters (aChars);
      eIncorrectCharHandling.notifyOnInvalidXMLCharacter (s, aInvalidCharacters);
      if (eIncorrectCharHandling.isReplaceWithNothing ())
      {
        final int nResLen = StringHelper.getReplaceMultipleResultLength (aChars,
                                                                         MASK_PATTERNS_ALL,
                                                                         MASK_REPLACE_ALL_EMPTY);
        return nResLen == CGlobal.ILLEGAL_UINT ? s.length () : nResLen;
      }
    }

    int nResLen;
    if (eXMLVersion.equals (EXMLVersion.XML_10))
      nResLen = StringHelper.getReplaceMultipleResultLength (aChars, MASK_PATTERNS_XML10, MASK_REPLACE_XML10);
    else
      nResLen = StringHelper.getReplaceMultipleResultLength (aChars, MASK_PATTERNS_ALL, MASK_REPLACE_ALL_XML11);
    return nResLen == CGlobal.ILLEGAL_UINT ? s.length () : nResLen;
  }

  public static void maskXMLTextTo (@Nonnull final EXMLVersion eXMLVersion,
                                    @Nonnull final EXMLIncorrectCharacterHandling eIncorrectCharHandling,
                                    @Nullable final String sText,
                                    @Nonnull final Writer aWriter) throws IOException
  {
    if (StringHelper.hasNoText (sText))
      return;

    if (eIncorrectCharHandling.isTestRequired ())
    {
      final char [] aChars = sText.toCharArray ();
      if (containsInvalidXMLCharacter (aChars))
      {
        final Set <Character> aInvalidCharacters = getAllInvalidXMLCharacters (aChars);
        eIncorrectCharHandling.notifyOnInvalidXMLCharacter (sText, aInvalidCharacters);
        if (eIncorrectCharHandling.isReplaceWithNothing ())
        {
          StringHelper.replaceMultipleTo (sText, MASK_PATTERNS_ALL, MASK_REPLACE_ALL_EMPTY, aWriter);
          return;
        }
      }
    }

    if (eXMLVersion.equals (EXMLVersion.XML_10))
      StringHelper.replaceMultipleTo (sText, MASK_PATTERNS_XML10, MASK_REPLACE_XML10, aWriter);
    else
      StringHelper.replaceMultipleTo (sText, MASK_PATTERNS_ALL, MASK_REPLACE_ALL_XML11, aWriter);
  }

  /**
   * Check if the passed node is a text node. This includes all nodes derived
   * from {@link CharacterData} which are not {@link Comment} nodes as well as
   * {@link EntityReference} nodes.
   *
   * @param aNode
   *        The node to be checked.
   * @return <code>true</code> if the passed node is a text node,
   *         <code>false</code> otherwise.
   */
  public static boolean isTextNode (@Nullable final Node aNode)
  {
    return (aNode instanceof CharacterData && !(aNode instanceof Comment)) || aNode instanceof EntityReference;
  }

  /**
   * Get the content of the first Text child element of the passed element.
   *
   * @param aStartNode
   *        the element to scan for a TextNode child
   * @return <code>null</code> if the element contains no text node as child
   */
  @Nullable
  public static String getFirstChildText (@Nullable final Node aStartNode)
  {
    if (aStartNode != null)
    {
      final NodeList aNodeList = aStartNode.getChildNodes ();
      final int nLen = aNodeList.getLength ();
      for (int i = 0; i < nLen; ++i)
      {
        final Node aNode = aNodeList.item (i);
        if (aNode instanceof Text)
        {
          final Text aText = (Text) aNode;

          // ignore whitespace-only content
          if (!aText.isElementContentWhitespace ())
            return aText.getData ();
        }
      }
    }
    return null;
  }

  /**
   * The latest version of XercesJ 2.9 returns an empty string for non existing
   * attributes. To differentiate between empty attributes and non-existing
   * attributes, this method returns null for non existing attributes.
   *
   * @param aElement
   *        the source element to get the attribute from
   * @param sAttrName
   *        the name of the attribute to query
   * @return <code>null</code> if the attribute does not exists, the string
   *         value otherwise
   */
  @Nullable
  public static String getAttributeValue (@Nonnull final Element aElement, @Nonnull final String sAttrName)
  {
    return getAttributeValue (aElement, sAttrName, null);
  }

  /**
   * The latest version of XercesJ 2.9 returns an empty string for non existing
   * attributes. To differentiate between empty attributes and non-existing
   * attributes, this method returns a default value for non existing
   * attributes.
   *
   * @param aElement
   *        the source element to get the attribute from. May not be
   *        <code>null</code>.
   * @param sAttrName
   *        the name of the attribute to query. May not be <code>null</code>.
   * @param sDefault
   *        the value to be returned if the attribute is not present.
   * @return the default value if the attribute does not exists, the string
   *         value otherwise
   */
  @Nullable
  public static String getAttributeValue (@Nonnull final Element aElement,
                                          @Nonnull final String sAttrName,
                                          @Nullable final String sDefault)
  {
    final Attr aAttr = aElement.getAttributeNode (sAttrName);
    return aAttr == null ? sDefault : aAttr.getValue ();
  }

  @Nullable
  public static Map <String, String> getAllAttributesAsMap (@Nullable final Element aSrcNode)
  {
    if (aSrcNode != null)
    {
      final NamedNodeMap aNNM = aSrcNode.getAttributes ();
      if (aNNM != null)
      {
        final Map <String, String> aMap = new LinkedHashMap <String, String> (aNNM.getLength ());
        final int nMax = aNNM.getLength ();
        for (int i = 0; i < nMax; ++i)
        {
          final Attr aAttr = (Attr) aNNM.item (i);
          aMap.put (aAttr.getName (), aAttr.getValue ());
        }
        return aMap;
      }
    }
    return null;
  }

  /**
   * Get the full qualified attribute name to use for the given namespace
   * prefix. The result will e.g. be <code>xmlns</code> or
   * <code>xmlns:foo</code>.
   *
   * @param sNSPrefix
   *        The namespace prefix to build the attribute name from. May be
   *        <code>null</code> or empty.
   * @return If the namespace prefix is empty (if it equals
   *         {@link XMLConstants#DEFAULT_NS_PREFIX} or <code>null</code>) than
   *         "xmlns" is returned, else "xmlns:<i>prefix</i>" is returned.
   */
  @Nonnull
  public static String getXMLNSAttrName (@Nullable final String sNSPrefix)
  {
    if (sNSPrefix != null && sNSPrefix.contains (CXML.XML_PREFIX_NAMESPACE_SEP_STR))
      throw new IllegalArgumentException ("prefix is invalid: " + sNSPrefix);
    if (sNSPrefix == null || sNSPrefix.equals (XMLConstants.DEFAULT_NS_PREFIX))
      return CXML.XML_ATTR_XMLNS;
    return CXML.XML_ATTR_XMLNS_WITH_SEP + sNSPrefix;
  }

  /**
   * Get the full qualified attribute name to use for the given prefix.
   *
   * @param sNSPrefix
   *        The namespace prefix to build the attribute name from. May neither
   *        be <code>null</code> nor empty.
   * @return "xmlns:<i>prefix</i>"
   */
  @Nonnull
  @Deprecated
  @DevelopersNote ("Use getXMLNSAttrName instead!")
  public static String getXMLNSPrefix (@Nonnull final String sNSPrefix)
  {
    if (StringHelper.hasNoText (sNSPrefix) || sNSPrefix.contains (CXML.XML_PREFIX_NAMESPACE_SEP_STR))
      throw new IllegalArgumentException ("prefix is invalid: " + sNSPrefix);
    return getXMLNSAttrName (sNSPrefix);
  }

  @Nullable
  public static String getNamespaceURI (@Nullable final Node aNode)
  {
    if (aNode instanceof Document)
      return getNamespaceURI (((Document) aNode).getDocumentElement ());
    if (aNode != null)
      return aNode.getNamespaceURI ();
    return null;
  }

  /**
   * Check if the passed character is valid for XML content. Works for XML 1.0
   * and XML 1.1.<br>
   * Note: makes no difference between the runtime JAXP solution and the
   * explicit Xerces version
   *
   * @param c
   *        The character to be checked.
   * @return <code>true</code> if the character is valid in XML,
   *         <code>false</code> otherwise.
   */
  public static boolean isInvalidXMLCharacter (final char c)
  {
    // Based on: http://www.w3.org/TR/2006/REC-xml11-20060816/#charsets

    // Speed up by separating the most common use cases first
    if (c < 256)
    {
      // Character <= 0x00ff - use precomposed table
      return ILLEGAL_XML_CHARS[c];
    }

    // Character >= 0x0100
    // For completeness, the Unicode line separator character, #x2028, is
    // also supported.
    // Surrogate blocks (no Java IDs found)
    // High surrogate: 0xd800-0xdbff
    // Low surrogate: 0xdc00-0xdfff
    return c == '\u2028' ||
           (c >= '\ufdd0' && c <= '\ufddf') ||
           c == '\ufffe' ||
           c == '\uffff' ||
           Character.isHighSurrogate (c) ||
           Character.isLowSurrogate (c);
  }

  public static boolean containsInvalidXMLCharacter (@Nullable final String s)
  {
    return s != null && containsInvalidXMLCharacter (s.toCharArray ());
  }

  public static boolean containsInvalidXMLCharacter (@Nullable final char [] aChars)
  {
    if (aChars != null)
      for (final char c : aChars)
        if (isInvalidXMLCharacter (c))
          return true;
    return false;
  }

  @Nullable
  public static Set <Character> getAllInvalidXMLCharacters (@Nullable final String s)
  {
    return s == null ? null : getAllInvalidXMLCharacters (s.toCharArray ());
  }

  @Nullable
  public static Set <Character> getAllInvalidXMLCharacters (@Nullable final char [] aChars)
  {
    if (ArrayHelper.isEmpty (aChars))
      return null;

    final Set <Character> aRes = new HashSet <Character> ();
    for (final char c : aChars)
      if (isInvalidXMLCharacter (c))
        aRes.add (Character.valueOf (c));
    return aRes;
  }

  @Nonnegative
  public static int getLength (@Nullable final NodeList aNL)
  {
    return aNL == null ? 0 : aNL.getLength ();
  }

  public static boolean isEmpty (@Nullable final NodeList aNL)
  {
    return getLength (aNL) == 0;
  }
}
