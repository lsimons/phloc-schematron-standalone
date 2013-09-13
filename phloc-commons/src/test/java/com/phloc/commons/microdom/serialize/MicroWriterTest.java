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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.phloc.commons.CGlobal;
import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.IMicroNode;
import com.phloc.commons.microdom.impl.MicroCDATA;
import com.phloc.commons.microdom.impl.MicroComment;
import com.phloc.commons.microdom.impl.MicroContainer;
import com.phloc.commons.microdom.impl.MicroDocument;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.microdom.impl.MicroEntityReference;
import com.phloc.commons.xml.EXMLVersion;
import com.phloc.commons.xml.XMLHelper;
import com.phloc.commons.xml.namespace.MapBasedNamespaceContext;
import com.phloc.commons.xml.serialize.EXMLSerializeDocType;
import com.phloc.commons.xml.serialize.EXMLSerializeFormat;
import com.phloc.commons.xml.serialize.EXMLSerializeIndent;
import com.phloc.commons.xml.serialize.XMLWriterSettings;

/**
 * Test class for class {@link MicroWriter}.
 * 
 * @author Philip Helger
 */
public final class MicroWriterTest
{
  private static final String TEST_XML = "<?xml version=\"1.0\"?>"
                                         + "<!DOCTYPE verrryoot>"
                                         + "<verrryoot xmlns=\"sthgelse\">"
                                         + "<!-- arg - a comment -->"
                                         + "<root xmlns=\"myuri\">"
                                         + "<child xmlns=\"www.phloc.com\">"
                                         + "<a:child2 xmlns:a=\"foo\">Value text - no entities!</a:child2>"
                                         + "&lt;entity&gt;<![CDATA[xxx]]></child>"
                                         + "</root>"
                                         + "<?target value?>"
                                         + "</verrryoot>";

  private static void _testGetNodeAsXHTMLString (final IMicroNode aNode)
  {
    // try all permutations
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setFormat (EXMLSerializeFormat.HTML);
    for (int nCharSet = 0; nCharSet < 2; ++nCharSet)
    {
      aSettings.setCharset (nCharSet == 1 ? CCharset.CHARSET_ISO_8859_1_OBJ : CCharset.CHARSET_UTF_8_OBJ);
      for (final EXMLSerializeIndent eIndent : EXMLSerializeIndent.values ())
      {
        aSettings.setIndent (eIndent);
        for (final EXMLSerializeDocType eDocType : EXMLSerializeDocType.values ())
        {
          aSettings.setSerializeDocType (eDocType);
          assertNotNull (MicroWriter.getNodeAsString (aNode, aSettings));
        }
      }
    }
  }

  @Test
  public void testGetXHTMLString ()
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML);
    _testGetNodeAsXHTMLString (aDoc);
    _testGetNodeAsXHTMLString (aDoc.getDocumentElement ());
    _testGetNodeAsXHTMLString (new MicroDocument ());
    _testGetNodeAsXHTMLString (new MicroElement ("xyz"));
    _testGetNodeAsXHTMLString (new MicroContainer ());
    _testGetNodeAsXHTMLString (new MicroComment ("useless"));
    _testGetNodeAsXHTMLString (new MicroEntityReference ("xyz"));
    try
    {
      _testGetNodeAsXHTMLString (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  private static void _testGetNodeAsXMLString (final IMicroNode aNode)
  {
    // try all permutations
    final XMLWriterSettings aSettings = new XMLWriterSettings ();
    for (int nCharSet = 0; nCharSet < 2; ++nCharSet)
    {
      aSettings.setCharset (nCharSet == 1 ? CCharset.CHARSET_ISO_8859_1_OBJ : CCharset.CHARSET_UTF_8_OBJ);
      for (final EXMLSerializeIndent eIndent : EXMLSerializeIndent.values ())
      {
        aSettings.setIndent (eIndent);
        for (final EXMLSerializeDocType eDocType : EXMLSerializeDocType.values ())
        {
          aSettings.setSerializeDocType (eDocType);
          assertNotNull (MicroWriter.getNodeAsString (aNode, aSettings));
        }
      }
    }
  }

  @Test
  public void testGetXMLString ()
  {
    final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML);
    _testGetNodeAsXMLString (aDoc);
    _testGetNodeAsXMLString (aDoc.getDocumentElement ());
    _testGetNodeAsXMLString (new MicroElement ("xyz"));
    _testGetNodeAsXMLString (new MicroContainer ());
    _testGetNodeAsXMLString (new MicroContainer (new MicroComment ("useless"), new MicroElement ("xyz")));
    _testGetNodeAsXMLString (new MicroComment ("useless"));
    _testGetNodeAsXMLString (new MicroEntityReference ("xyz"));
    _testGetNodeAsXMLString (new MicroCDATA ("xyz"));
    try
    {
      _testGetNodeAsXMLString (null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}

    try
    {
      MicroWriter.getNodeAsString (null, XMLWriterSettings.DEFAULT_XML_SETTINGS);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      MicroWriter.getNodeAsString (aDoc, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testSaveToStream ()
  {
    try
    {
      MicroWriter.writeToStream (null, new NonBlockingByteArrayOutputStream (), XMLWriterSettings.DEFAULT_XML_SETTINGS);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      MicroWriter.writeToStream (new MicroDocument (), null, XMLWriterSettings.DEFAULT_XML_SETTINGS);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      MicroWriter.writeToStream (new MicroDocument (), new NonBlockingByteArrayOutputStream (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testXMLVersion ()
  {
    for (final EXMLVersion eVersion : EXMLVersion.values ())
    {
      final IMicroDocument aDoc = MicroReader.readMicroXML (TEST_XML);
      final XMLWriterSettings aSettings = new XMLWriterSettings ();
      aSettings.setXMLVersion (eVersion);
      final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
      assertNotNull (sXML);
      assertTrue (sXML.contains ("version=\"" + eVersion.getVersion () + "\""));
    }
  }

  @Test
  public void testNestedCDATASections ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE);

    // Containing the forbidden CDATA end marker
    IMicroElement e = new MicroElement ("a");
    e.appendCDATA ("a]]>b");
    assertEquals ("<a><![CDATA[a]]>]]&gt;<![CDATA[b]]></a>", MicroWriter.getNodeAsString (e, aSettings));

    // Containing more than one forbidden CDATA end marker
    e = new MicroElement ("a");
    e.appendCDATA ("a]]>b]]>c");
    assertEquals ("<a><![CDATA[a]]>]]&gt;<![CDATA[b]]>]]&gt;<![CDATA[c]]></a>",
                  MicroWriter.getNodeAsString (e, aSettings));

    // Containing a complete CDATA section
    e = new MicroElement ("a");
    e.appendCDATA ("a<![CDATA[x]]>b");
    assertEquals ("<a><![CDATA[a<![CDATA[x]]>]]&gt;<![CDATA[b]]></a>", MicroWriter.getNodeAsString (e, aSettings));
  }

  @Test
  public void testWithNamespaceContext ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("ns1url", "root");
    eRoot.appendElement ("ns2url", "child1");
    eRoot.appendElement ("ns2url", "child2");

    String s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</root>", s);

    final MapBasedNamespaceContext aCtx = new MapBasedNamespaceContext ();
    aCtx.addMapping ("a", "ns1url");
    aSettings.setNamespaceContext (aCtx);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</a:root>", s);

    // Add mapping to namespace context
    aCtx.addMapping ("xy", "ns2url");
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<a:root xmlns:a=\"ns1url\">" +
                  "<xy:child1 xmlns:xy=\"ns2url\" />" +
                  "<xy:child2 xmlns:xy=\"ns2url\" />" +
                  "</a:root>", s);

    // Put namespace context mappings in root
    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<a:root xmlns:a=\"ns1url\" xmlns:xy=\"ns2url\">" +
                  "<xy:child1 />" +
                  "<xy:child2 />" +
                  "</a:root>", s);

    eRoot.appendElement ("ns3url", "zz");
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<a:root xmlns:a=\"ns1url\" xmlns:xy=\"ns2url\">" +
                  "<xy:child1 />" +
                  "<xy:child2 />" +
                  "<ns0:zz xmlns:ns0=\"ns3url\" />" +
                  "</a:root>", s);
  }

  @Test
  public void testWithoutEmitNamespaces ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.NONE)
                                                                .setCharset (CCharset.CHARSET_ISO_8859_1_OBJ);
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("ns1url", "root");
    eRoot.appendElement ("ns2url", "child1");
    eRoot.appendElement ("ns2url", "child2");

    String s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<root xmlns=\"ns1url\">" +
                  "<ns0:child1 xmlns:ns0=\"ns2url\" />" +
                  "<ns0:child2 xmlns:ns0=\"ns2url\" />" +
                  "</root>", s);

    aSettings.setEmitNamespaces (false);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<root>" +
                  "<child1 />" +
                  "<child2 />" +
                  "</root>", s);

    aSettings.setPutNamespaceContextPrefixesInRoot (true);
    s = MicroWriter.getNodeAsString (aDoc, aSettings);
    assertEquals ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                  CGlobal.LINE_SEPARATOR +
                  "<root>" +
                  "<child1 />" +
                  "<child2 />" +
                  "</root>", s);
  }

  @Test
  public void testSpecialCharactersXML10 ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setXMLVersion (EXMLVersion.XML_10);
    for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; ++i)
      if (!XMLHelper.isInvalidXMLCharacter (i))
      {
        final String sText = "abc" + i + "def";
        assertEquals (7, sText.length ());
        final IMicroDocument aDoc = new MicroDocument ();
        aDoc.appendElement ("root").appendText (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + (int) i, aDoc2);
        assertEquals (i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());

        // Wont work for \u0000 because it is mapped to ""
        if (i > 0 && i != 13)
          assertTrue ("Difference in byte 0x" + Integer.toHexString (i), aDoc.isEqualContent (aDoc2));
      }
  }

  @Test
  public void testSpecialCharactersXML11 ()
  {
    final XMLWriterSettings aSettings = new XMLWriterSettings ().setXMLVersion (EXMLVersion.XML_11);
    for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; ++i)
      if (!XMLHelper.isInvalidXMLCharacter (i))
      {
        final String sText = "abc" + i + "def";
        assertEquals (7, sText.length ());
        final IMicroDocument aDoc = new MicroDocument ();
        aDoc.appendElement ("root").appendText (sText);
        final String sXML = MicroWriter.getNodeAsString (aDoc, aSettings);
        final IMicroDocument aDoc2 = MicroReader.readMicroXML (sXML);
        assertNotNull ("Failed to read with byte " + (int) i, aDoc2);
        assertEquals (i == 0 ? 6 : 7, aDoc2.getDocumentElement ().getTextContent ().length ());

        // Wont work for \u0000 because it is mapped to ""
        if (i > 0 && i != 13)
          assertTrue ("0x" + Integer.toHexString (i), aDoc.isEqualContent (aDoc2));
      }
  }
}
