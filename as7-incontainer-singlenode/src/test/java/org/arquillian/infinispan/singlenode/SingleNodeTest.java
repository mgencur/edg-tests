/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.infinispan.singlenode;

import junit.framework.Assert;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@RunWith(Arquillian.class)
public class SingleNodeTest
{
   //@InfinispanResource
   //DatagridManager dm;  //DatagridManager class needs to be extended in ispn core tests to match latest configuration changes
   
   Cache<String, String> cache;

   @Deployment 
   public static Archive<?> deployment() {
       WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
       //add dependencies
       war.addAsManifestResource(new StringAsset("Dependencies: org.infinispan\n"), "MANIFEST.MF");
       
       /*alternatively specify dependencies via jboss-deployment-structure.xml file:       
       war.addAsManifestResource("deployment/jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
          or
       war.addAsManifestResource(new StringAsset(
               "<jboss-deployment-structure>" +
               "    <deployment>" +
               "        <dependencies>" + 
               "            <module name=\"org.infinispan\"/>" +
               "        </dependencies>" + 
               "    </deployment>" +
               "</jboss-deployment-structure>"),
               "jboss-deployment-structure.xml");
       */

       return war;
   }
   
   @Before
   public void setUp() throws Exception {
       GlobalConfiguration glob = new GlobalConfigurationBuilder()
           .nonClusteredDefault()
           .build();
       Configuration loc = new ConfigurationBuilder()
           .clustering()
           .cacheMode(CacheMode.LOCAL)  
           .build();
       DefaultCacheManager dc = new DefaultCacheManager(glob, loc, true);
       cache = dc.getCache();
   }
   
   @Test 
   public void testSimple() {
       cache.put("k", "v");
       Assert.assertEquals("v", cache.get("k"));
       System.out.println("Result: " +  cache.get("k"));
   }
}
