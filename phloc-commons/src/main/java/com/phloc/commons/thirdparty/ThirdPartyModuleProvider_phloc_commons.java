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
package com.phloc.commons.thirdparty;

import javax.annotation.Nullable;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party
 * modules.
 * 
 * @author Philip Helger
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_phloc_commons implements IThirdPartyModuleProviderSPI
{
  private static final IThirdPartyModule FINDBUGS = new ThirdPartyModule ("FindBugs JSR305 and annotations",
                                                                          "Bill Pugh and David Hovemeyer",
                                                                          ELicense.LGPL30,
                                                                          new Version (2, 0, 1),
                                                                          "http://findbugs.sourceforge.net/");
  private static final IThirdPartyModule SLF4J = new ThirdPartyModule ("SLF4J API",
                                                                       "QOS.ch",
                                                                       ELicense.MIT,
                                                                       new Version (1, 7, 5),
                                                                       "http://www.slf4j.org/");
  private static final IThirdPartyModule BASE64 = new ThirdPartyModule ("Base64",
                                                                        "Robert Harder",
                                                                        ELicense.PUBLIC_DOMAIN,
                                                                        new Version (2, 3, 7),
                                                                        "http://iharder.net/base64");

  @Nullable
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { FINDBUGS, SLF4J, BASE64 };
  }
}
