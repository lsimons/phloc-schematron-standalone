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
package com.phloc.commons.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.phloc.commons.charset.CCharset;

/**
 * Test class for class {@link URLUtils}.
 * 
 * @author Philip Helger
 */
public final class URLUtilsTest
{
  @Test
  public void testGetCleanURL ()
  {
    assertEquals ("aeoeue", URLUtils.getCleanURLPartWithoutUmlauts ("äöü"));
    assertEquals ("AeoeUe", URLUtils.getCleanURLPartWithoutUmlauts ("ÄöÜ"));
    assertEquals ("Ae-Uesz", URLUtils.getCleanURLPartWithoutUmlauts ("Ä Üß"));
    assertEquals ("Weisze-Waeste", URLUtils.getCleanURLPartWithoutUmlauts ("Weiße Wäste"));
    assertEquals ("hallo", URLUtils.getCleanURLPartWithoutUmlauts ("hállò"));
    assertEquals ("ffi", URLUtils.getCleanURLPartWithoutUmlauts ("\uFB03"));
    assertEquals ("ffl", URLUtils.getCleanURLPartWithoutUmlauts ("\uFB04"));
    assertEquals ("hallo;jsessionid=1234", URLUtils.getCleanURLPartWithoutUmlauts ("hállò;jsessionid=1234"));
  }

  @Test
  public void testUrlEncodeDecode ()
  {
    String sDec = "hallo welt";
    String sEnc = URLUtils.urlEncode (sDec);
    assertEquals ("hallo+welt", sEnc);
    assertEquals (sDec, URLUtils.urlDecode (sEnc));

    // default: UTF-8
    sDec = "äöü";
    sEnc = URLUtils.urlEncode (sDec);
    assertEquals ("%C3%A4%C3%B6%C3%BC", sEnc);
    assertEquals (sDec, URLUtils.urlDecode (sEnc));

    sDec = "äöü";
    sEnc = URLUtils.urlEncode (sDec, CCharset.CHARSET_ISO_8859_1);
    assertEquals ("%E4%F6%FC", sEnc);
    assertEquals (sDec, URLUtils.urlDecode (sEnc, CCharset.CHARSET_ISO_8859_1));
  }

  @Test
  public void testGetURLData ()
  {
    IURLData aData = URLUtils.getAsURLData ("http://www.phloc.com/folder?x=y&a=b#c");
    assertNotNull (aData);
    assertEquals (EURLProtocol.HTTP, aData.getProtocol ());
    assertEquals ("http://www.phloc.com/folder", aData.getPath ());
    assertEquals (2, aData.getParamCount ());
    assertEquals ("y", aData.getAllParams ().get ("x"));
    assertEquals ("b", aData.getAllParams ().get ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = URLUtils.getAsURLData ("?x=y&a=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (2, aData.getParamCount ());
    assertEquals ("y", aData.getAllParams ().get ("x"));
    assertEquals ("b", aData.getAllParams ().get ("a"));
    assertEquals ("c", aData.getAnchor ());
  }
}
