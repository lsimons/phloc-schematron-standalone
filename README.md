this is a version of phloc-schematron 2.6.3-SNAPSHOT
...that does not depend on or use the phloc maven repository.
...which is useful for paranoid people that don't want to download
   random binary code of the internet
...or in case the phloc repository goes down and you need to
   recompile or patch the source code.

to get phloc-schematron for yourself, you need
  http://code.google.com/p/phloc-parent-pom/
  http://code.google.com/p/phloc-commons/
  http://code.google.com/p/phloc-math/
  http://code.google.com/p/phloc-schematron/

here's (roughly) how this version was made:

```bash
rm -r ~/.m2/repository/com/phloc
svn checkout http://phloc-parent-pom.googlecode.com/svn/trunk/ phloc-parent-pom
cd phloc-parent-pom
svn up -r 151

patch -p0 <<END
Index: pom.xml
===================================================================
--- pom.xml	(revision 151)
+++ pom.xml	(working copy)
@@ -201,19 +201,6 @@
     </dependency>
   </dependencies>
   
-  <repositories>
-    <repository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>true</enabled>
-      </snapshots>
-    </repository>
-  </repositories>
-
 
-  <pluginRepositories>
-    <pluginRepository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>true</enabled>
-      </snapshots>
-    </pluginRepository>
-  </pluginRepositories>  
-
   <!-- Build plugins -->
   <build>
     <pluginManagement>
@@ -460,7 +434,8 @@
           <groupId>org.codehaus.mojo</groupId>
           <artifactId>taglist-maven-plugin</artifactId>
           <version>2.4</version>
-        </plugin>  
+        </plugin>
+        <!--
         <plugin>
           <groupId>com.phloc.maven</groupId>
           <artifactId>buildinfo-maven-plugin</artifactId>
@@ -487,6 +462,7 @@
           <artifactId>dirindex-maven-plugin</artifactId>
           <version>1.0.0</version>
         </plugin>
+        -->
         <!--This plugin's configuration is used to store Eclipse m2e settings only. It has no influence on the Maven build itself.-->
         <plugin>
           <groupId>org.eclipse.m2e</groupId>
@@ -495,6 +471,7 @@
           <configuration>
             <lifecycleMappingMetadata>
               <pluginExecutions>
+                <!--
                 <pluginExecution>
                   <pluginExecutionFilter>
                     <groupId>com.phloc.maven</groupId>
@@ -547,6 +524,7 @@
                     <ignore />
                   </action>
                 </pluginExecution>
+                -->
                 <pluginExecution>
                   <pluginExecutionFilter>
                     <groupId>org.codehaus.mojo</groupId>
@@ -627,6 +605,7 @@
                     <ignore />
                   </action>
                 </pluginExecution>
+                <!--
                 <pluginExecution>
                   <pluginExecutionFilter>
                     <groupId>com.phloc.maven</groupId>
@@ -641,6 +620,7 @@
                     <ignore />
                   </action>
                 </pluginExecution>
+                -->
                 <pluginExecution>
                   <pluginExecutionFilter>
                     <groupId>org.apache.felix</groupId>
@@ -799,6 +779,7 @@
         </configuration>
       </plugin>
       <!-- Create build information for all artefacts -->
+      <!--
       <plugin>
         <groupId>com.phloc.maven</groupId>
         <artifactId>buildinfo-maven-plugin</artifactId>
@@ -823,6 +804,7 @@
           </selectedEnvVars>
         </configuration>
       </plugin>
+      -->
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-site-plugin</artifactId>
END

mvn install

cd ../
svn checkout http://phloc-commons.googlecode.com/svn/tags/phloc-commons-4.0.10 phloc-commons
cd phloc-commons

patch -p0 <<END
Index: pom.xml
===================================================================
--- pom.xml	(revision 1674)
+++ pom.xml	(working copy)
@@ -50,32 +50,6 @@
     </developer>
   </developers>
   
-  <repositories>
-    <repository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>true</enabled>
-      </snapshots>
-    </repository>
-  </repositories>
-
-  <pluginRepositories>
-    <pluginRepository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>false</enabled>
-      </snapshots>
-    </pluginRepository>
-  </pluginRepositories>  
-
   <distributionManagement>
     <repository>
       <id>repo.phloc.public</id>
@@ -141,6 +115,7 @@
       <optional>true</optional>
     </dependency>
     
+    <!--
     <dependency>
       <groupId>com.phloc</groupId>
       <artifactId>phloc-jdk5</artifactId>
@@ -147,6 +122,7 @@
       <version>1.0.2</version>
       <scope>test</scope>
     </dependency>
+    -->
     <dependency>
       <groupId>dom4j</groupId>
       <artifactId>dom4j</artifactId>
END

mvn install -Dmaven.test.skip=true


cd ..
svn checkout http://phloc-math.googlecode.com/svn/tags/phloc-math-1.0.3 phloc-math
patch -p0 <<END
Index: pom.xml
===================================================================
--- pom.xml	(revision 97)
+++ pom.xml	(working copy)
@@ -50,32 +50,6 @@
     </developer>
   </developers>
   
-  <repositories>
-    <repository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>true</enabled>
-      </snapshots>
-    </repository>
-  </repositories>
-
-  <pluginRepositories>
-    <pluginRepository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>false</enabled>
-      </snapshots>
-    </pluginRepository>
-  </pluginRepositories>  
-
   <distributionManagement>
     <repository>
       <id>repo.phloc.public</id>
@@ -87,7 +61,7 @@
     <dependency>
       <groupId>com.phloc</groupId>
       <artifactId>phloc-commons</artifactId>
-      <version>4.0.7</version>
+      <version>4.0.10</version>
     </dependency>
     <dependency>
       <groupId>net.sf.trove4j</groupId>
END



svn co http://phloc-commons.googlecode.com/svn/tags/phloc-jaxb22-plugin-2.2.7.4 phloc-jaxb22-plugin
cd phloc-jaxb22-plugin
patch -p0 <<END
Index: pom.xml
===================================================================
--- pom.xml	(revision 1674)
+++ pom.xml	(working copy)
@@ -41,32 +41,6 @@
     </developer>
   </developers>
 
-  <repositories>
-    <repository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>true</enabled>
-      </snapshots>
-    </repository>
-  </repositories>
-
-  <pluginRepositories>
-    <pluginRepository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>false</enabled>
-      </snapshots>
-    </pluginRepository>
-  </pluginRepositories>  
-  
   <distributionManagement>
     <repository>
       <id>repo.phloc.public</id>
@@ -82,12 +56,12 @@
     <dependency>
       <groupId>com.phloc</groupId>
       <artifactId>phloc-commons</artifactId>
-      <version>4.0.7</version>
+      <version>4.0.10</version>
     </dependency>
     <dependency>
       <groupId>com.phloc</groupId>
       <artifactId>phloc-math</artifactId>
-      <version>1.0.2</version>
+      <version>1.0.3</version>
     </dependency>
     <dependency>
       <groupId>com.sun.xml.bind</groupId>
END
mvn install


cd ..
svn co http://phloc-schematron.googlecode.com/svn/trunk/ phloc-schematron

cd phloc-schematron/phloc-schematron
patch -p0 <<END
Index: pom.xml
===================================================================
--- pom.xml	(revision 404)
+++ pom.xml	(working copy)
@@ -42,32 +42,6 @@
     </developer>
   </developers>
 
-  <repositories>
-    <repository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>true</enabled>
-      </snapshots>
-    </repository>
-  </repositories>
-
-  <pluginRepositories>
-    <pluginRepository>
-      <id>phloc.com</id>
-      <url>http://repo.phloc.com/maven2</url>
-      <releases>
-        <enabled>true</enabled>
-      </releases>
-      <snapshots>
-        <enabled>false</enabled>
-      </snapshots>
-    </pluginRepository>
-  </pluginRepositories>
-
   <distributionManagement>
     <repository>
       <id>repo.phloc.public</id>
@@ -88,6 +62,7 @@
       <version>9.5.1-2</version>
     </dependency>
     
+    <!--
     <dependency>
       <groupId>com.phloc</groupId>
       <artifactId>phloc-schematron-testfiles</artifactId>
@@ -94,6 +69,7 @@
       <version>1.0.1</version>
       <scope>test</scope>
     </dependency>
+    -->
     <dependency>
       <groupId>org.slf4j</groupId>
       <artifactId>slf4j-simple</artifactId>
END

mvn install -Dmaven.test.skip=true
```