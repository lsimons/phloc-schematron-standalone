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
package com.phloc.commons.microdom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;

import com.phloc.commons.microdom.EMicroEvent;
import com.phloc.commons.microdom.IMicroEvent;
import com.phloc.commons.microdom.IMicroEventTarget;

final class MockMicroEventListener implements IMicroEventTarget
{
  private final EMicroEvent m_eType;
  private int m_nIC = 0;

  public MockMicroEventListener (@Nonnull final EMicroEvent eType)
  {
    m_eType = eType;
  }

  public void handleEvent (final IMicroEvent aEvent)
  {
    assertNotNull (aEvent);
    assertEquals (m_eType, aEvent.getEventType ());
    m_nIC++;
  }

  public int getInvocationCount ()
  {
    return m_nIC;
  }
}
