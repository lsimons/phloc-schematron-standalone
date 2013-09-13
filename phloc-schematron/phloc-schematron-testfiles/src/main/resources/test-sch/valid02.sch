<!--

    Copyright (C) 2013 phloc systems
    http://www.phloc.com
    office[at]phloc[dot]com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<schema  xmlns="http://purl.oclc.org/dsdl/schematron" >
     <pattern>
          <rule context="AAA">
               <assert test="BBB"><name /> element is <dir>missing</dir>.</assert>
               <report test="BBB"><name /> element is <emph>present</emph>.</report>
               <assert test="@name"><name /> misses attribute <span class="any">name</span>.</assert>
               <report test="@name"><name /> contains attribute name with value <value-of select="@name" />.</report>
          </rule>
     </pattern>
     <pattern>
          <rule context="AAA">
               <report test="BBB">BBB element is present.</report>
               <report test="@name"><name /> contains attribute name with value <value-of select="@name" />.</report>
          </rule>
     </pattern>
     <pattern>
          <rule context="AAA">
               <assert test="BBB">BBB element is missing.</assert>
               <assert test="@name"><name /> misses attribute name.</assert>
          </rule>
     </pattern>
</schema>