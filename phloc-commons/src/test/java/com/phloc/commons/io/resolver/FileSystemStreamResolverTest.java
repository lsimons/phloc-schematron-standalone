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
package com.phloc.commons.io.resolver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.phloc.commons.io.EAppend;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.mock.PhlocTestUtils;

/**
 * Test class for class {@link FileSystemStreamResolver}.
 * 
 * @author Philip Helger
 */
public final class FileSystemStreamResolverTest
{
  @Test
  public void testAll ()
  {
    final FileSystemStreamResolver aFSSR = new FileSystemStreamResolver (new File ("."));
    final InputStream aIS = aFSSR.getInputStream ("pom.xml");
    assertNotNull (aIS);
    StreamUtils.close (aIS);

    final OutputStream aOS = aFSSR.getOutputStream ("$deleteme.txt", EAppend.DEFAULT);
    assertNotNull (aOS);
    StreamUtils.close (aOS);
    assertTrue (FileOperations.deleteFile (new File ("$deleteme.txt")).isSuccess ());

    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new FileSystemStreamResolver (new File (".")),
                                                                    new FileSystemStreamResolver (new File (".")));
    PhlocTestUtils.testDefaultImplementationWithEqualContentObject (new FileSystemStreamResolver (new File (".")),
                                                                    new FileSystemStreamResolver ("."));
    PhlocTestUtils.testDefaultImplementationWithDifferentContentObject (new FileSystemStreamResolver (new File (".")),
                                                                        new FileSystemStreamResolver (new File ("..")));
  }
}
