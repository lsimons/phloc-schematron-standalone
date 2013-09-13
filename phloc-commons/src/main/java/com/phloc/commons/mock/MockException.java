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
package com.phloc.commons.mock;

/**
 * Non-runtime exception for test cases.
 * 
 * @author Philip Helger
 */
public class MockException extends Exception implements IMockException
{
  public MockException ()
  {}

  public MockException (final String s)
  {
    super (s);
  }

  public MockException (final Throwable t)
  {
    super (t);
  }

  public MockException (final String s, final Throwable t)
  {
    super (s, t);
  }
}
