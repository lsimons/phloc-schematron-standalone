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
package com.phloc.math.graph.utils;

import java.io.IOException;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.charset.CharsetManager;
import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.xml.EXMLIncorrectCharacterHandling;
import com.phloc.commons.xml.EXMLVersion;
import com.phloc.commons.xml.XMLHelper;
import com.phloc.math.graph.IDirectedGraphNode;
import com.phloc.math.graph.IDirectedGraphRelation;
import com.phloc.math.graph.IGraphNode;
import com.phloc.math.graph.IGraphRelation;
import com.phloc.math.graph.IReadonlyDirectedGraph;
import com.phloc.math.graph.IReadonlyGraph;

/**
 * Utility class to export a graph to something else
 * 
 * @author Philip Helger
 */
@Immutable
public final class GraphVizUtils
{
  private GraphVizUtils ()
  {}

  @Nonnull
  @Nonempty
  public static String getAttribute (@Nonnull @Nonempty final String sName, @Nonnull final String sValue)
  {
    return new StringBuilder (sName).append ("=<")
                                    .append (XMLHelper.getMaskedXMLText (EXMLVersion.XML_10,
                                                                         EXMLIncorrectCharacterHandling.DEFAULT,
                                                                         sValue))
                                    .append ('>')
                                    .toString ();
  }

  /**
   * Get the graph in a simple DOT notation suitable for GraphViz
   * (http://www.graphviz.org). The DOT specs can be found at
   * http://www.graphviz.org/content/dot-language<br>
   * The default file encoding for GraphViz 2.28 is UTF-8!
   * 
   * @param aGraph
   *        The graph to be converted. May not be <code>null</code>.
   * @param sNodeLabelAttr
   *        The name of the attribute to be used for node labels. May be
   *        <code>null</code> to use the node ID as the label.
   * @param sRelationLabelAttr
   *        The name of the attribute to be used for relation labels. May be
   *        <code>null</code> to use no relation label.
   * @return The string representation to be used as input for DOT.
   */
  @Nonnull
  public static String getAsGraphVizDot (@Nonnull final IReadonlyGraph aGraph,
                                         @Nullable final String sNodeLabelAttr,
                                         @Nullable final String sRelationLabelAttr)
  {
    if (aGraph == null)
      throw new NullPointerException ("graph");

    final StringBuilder aSB = new StringBuilder ();
    // It's a directed graph
    aSB.append ("graph ").append (aGraph.getID ()).append ("{\n");
    aSB.append ("node[shape=box];");
    for (final IGraphNode aGraphNode : aGraph.getAllNodes ().values ())
    {
      aSB.append (aGraphNode.getID ());
      if (StringHelper.hasText (sNodeLabelAttr))
      {
        final String sLabel = aGraphNode.getAttributeAsString (sNodeLabelAttr);
        aSB.append ("[").append (getAttribute ("label", sLabel)).append ("]");
      }
      aSB.append (';');
    }
    aSB.append ('\n');
    for (final IGraphRelation aGraphRelation : aGraph.getAllRelations ().values ())
    {
      final Iterator <IGraphNode> it = aGraphRelation.getAllConnectedNodes ().iterator ();
      aSB.append (it.next ().getID ()).append ("--").append (it.next ().getID ());
      if (StringHelper.hasText (sRelationLabelAttr))
      {
        final String sLabel = aGraphRelation.getAttributeAsString (sRelationLabelAttr);
        aSB.append ("[").append (getAttribute ("label", sLabel)).append ("]");
      }
      aSB.append (";\n");
    }
    aSB.append ("overlap=false;\n");
    aSB.append ('}');
    return aSB.toString ();
  }

  /**
   * Get the graph in a simple DOT notation suitable for GraphViz
   * (http://www.graphviz.org). The DOT specs can be found at
   * http://www.graphviz.org/content/dot-language<br>
   * The default file encoding for GraphViz 2.28 is UTF-8!
   * 
   * @param aGraph
   *        The graph to be converted. May not be <code>null</code>.
   * @param sNodeLabelAttr
   *        The name of the attribute to be used for node labels. May be
   *        <code>null</code> to use the node ID as the label.
   * @param sRelationLabelAttr
   *        The name of the attribute to be used for relation labels. May be
   *        <code>null</code> to use no relation label.
   * @return The string representation to be used as input for DOT.
   */
  @Nonnull
  public static String getAsGraphVizDot (@Nonnull final IReadonlyDirectedGraph aGraph,
                                         @Nullable final String sNodeLabelAttr,
                                         @Nullable final String sRelationLabelAttr)
  {
    if (aGraph == null)
      throw new NullPointerException ("graph");

    final StringBuilder aSB = new StringBuilder ();
    // It's a directed graph
    aSB.append ("digraph ").append (aGraph.getID ()).append ("{\n");
    aSB.append ("node[shape=box];");
    for (final IDirectedGraphNode aGraphNode : aGraph.getAllNodes ().values ())
    {
      aSB.append (aGraphNode.getID ());
      if (StringHelper.hasText (sNodeLabelAttr))
      {
        final String sLabel = aGraphNode.getAttributeAsString (sNodeLabelAttr);
        aSB.append ("[label=<")
           .append (XMLHelper.getMaskedXMLText (EXMLVersion.XML_10, EXMLIncorrectCharacterHandling.DEFAULT, sLabel))
           .append (">]");
      }
      aSB.append (';');
    }
    aSB.append ('\n');
    for (final IDirectedGraphRelation aGraphRelation : aGraph.getAllRelations ().values ())
    {
      aSB.append (aGraphRelation.getFromID ()).append ("->").append (aGraphRelation.getToID ());
      if (StringHelper.hasText (sRelationLabelAttr))
      {
        final String sLabel = aGraphRelation.getAttributeAsString (sRelationLabelAttr);
        aSB.append ("[label=<")
           .append (XMLHelper.getMaskedXMLText (EXMLVersion.XML_10, EXMLIncorrectCharacterHandling.DEFAULT, sLabel))
           .append (">]");
      }
      aSB.append (";\n");
    }
    aSB.append ("overlap=false;\n");
    aSB.append ('}');
    return aSB.toString ();
  }

  /**
   * Invoked the external process "neato" from the GraphViz package. Attention:
   * this spans a sub-process!
   * 
   * @param sFileType
   *        The file type to be generated. E.g. "png" - see neato help for
   *        details. May neither be <code>null</code> nor empty.
   * @param sDOT
   *        The DOT file to be converted to an image. May neither be
   *        <code>null</code> nor empty.
   * @return The byte buffer that keeps the converted image. Never
   *         <code>null</code>.
   * @throws IOException
   *         In case some IO error occurs
   * @throws InterruptedException
   *         If the sub-process did not terminate correctly!
   */
  @Nonnull
  public static NonBlockingByteArrayOutputStream getGraphAsImageWithGraphVizNeato (@Nonnull @Nonempty final String sFileType,
                                                                                   @Nonnull final String sDOT) throws IOException,
                                                                                                              InterruptedException
  {
    if (StringHelper.hasNoText (sFileType))
      throw new IllegalArgumentException ("Empty file type!");
    if (StringHelper.hasNoText (sDOT))
      throw new IllegalArgumentException ("Empty DOT!");

    final ProcessBuilder aPB = new ProcessBuilder ("neato", "-T" + sFileType).redirectErrorStream (false);
    final Process p = aPB.start ();
    // Set neato stdin
    p.getOutputStream ().write (CharsetManager.getAsBytes (sDOT, CCharset.CHARSET_UTF_8_OBJ));
    p.getOutputStream ().close ();
    // Read neato stdout
    final NonBlockingByteArrayOutputStream aBAOS = new NonBlockingByteArrayOutputStream ();
    StreamUtils.copyInputStreamToOutputStream (p.getInputStream (), aBAOS);
    p.waitFor ();
    return aBAOS;
  }
}
