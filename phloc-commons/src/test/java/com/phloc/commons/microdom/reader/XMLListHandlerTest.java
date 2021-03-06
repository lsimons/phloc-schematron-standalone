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
package com.phloc.commons.microdom.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.IInputStreamProvider;
import com.phloc.commons.io.IOutputStreamProvider;
import com.phloc.commons.io.IReadableResource;
import com.phloc.commons.io.resource.ClassPathResource;
import com.phloc.commons.io.streamprovider.ByteArrayOutputStreamProvider;
import com.phloc.commons.io.streamprovider.StringInputStreamProvider;
import com.phloc.commons.io.streams.NonBlockingByteArrayOutputStream;

/**
 * Test class for {@link XMLListHandler}.
 * 
 * @author Philip Helger
 */
public final class XMLListHandlerTest
{
  @Test
  public void testRead ()
  {
    List <String> aList = new ArrayList <String> ();
    final IReadableResource aRes = new ClassPathResource ("xml/list.xml");
    XMLListHandler.readList (aRes, aList);
    assertEquals (3, aList.size ());
    assertTrue (aList.contains ("item1"));

    assertNotNull (XMLListHandler.readList (aRes));
    assertNull (XMLListHandler.readList (new ClassPathResource ("test1.txt")));
    assertEquals (3, XMLListHandler.readList (aRes).size ());
    assertTrue (XMLListHandler.readList (aRes).contains ("item1"));

    assertTrue (XMLListHandler.writeList (aList, new NonBlockingByteArrayOutputStream ()).isSuccess ());

    final String sXML = "<root><item/><item value='a' /><item value='a' /></root>";
    aList = XMLListHandler.readList (new StringInputStreamProvider (sXML, CCharset.CHARSET_ISO_8859_1_OBJ));
    assertNotNull (aList);
    assertEquals (2, aList.size ());
    assertEquals ("a", aList.get (0));
    assertEquals ("a", aList.get (1));

    final Set <String> aSet = new HashSet <String> ();
    XMLListHandler.readList (new StringInputStreamProvider (sXML, CCharset.CHARSET_ISO_8859_1_OBJ), aSet);
    assertEquals (1, aSet.size ());
    assertTrue (aSet.contains ("a"));

    assertTrue (XMLListHandler.writeList (aList, new NonBlockingByteArrayOutputStream ()).isSuccess ());
    assertTrue (XMLListHandler.writeList (aList, new ByteArrayOutputStreamProvider ()).isSuccess ());
  }

  @Test
  public void testReadInvalid ()
  {
    final List <String> aList = new ArrayList <String> ();
    final IReadableResource aRes = new ClassPathResource ("xml/list.xml");
    try
    {
      XMLListHandler.readList ((IInputStreamProvider) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.readList ((IInputStreamProvider) null, aList);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.readList (aRes, null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.readList ((InputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.readList ((InputStream) null, aList);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.readList (aRes.getInputStream (), null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }

  @Test
  public void testWriteInvalid ()
  {
    final List <String> aList = new ArrayList <String> ();
    try
    {
      XMLListHandler.writeList (aList, (IOutputStreamProvider) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.writeList (aList, (OutputStream) null);
      fail ();
    }
    catch (final NullPointerException ex)
    {}
    try
    {
      XMLListHandler.writeList (null, new NonBlockingByteArrayOutputStream ());
      fail ();
    }
    catch (final NullPointerException ex)
    {}
  }
}
