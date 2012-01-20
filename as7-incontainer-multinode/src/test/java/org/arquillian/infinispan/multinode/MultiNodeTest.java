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
package org.arquillian.infinispan.multinode;

import junit.framework.Assert;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

@RunWith(Arquillian.class)
public class MultiNodeTest
{
   static Cache cache; //variable to store a cache to (different on both servers since both servers
                       //have different .class file loaded in JVM)
    
   /* Deploy first deployment to first server - same as the second deployment*/ 
   @Deployment(name="dep1", order=1) 
   @TargetsContainer("container1")
   public static Archive<?> deployment1() {
       WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
       war.addAsManifestResource(new StringAsset("Dependencies: org.infinispan, org.jgroups\n"), "MANIFEST.MF");
       return war;
   }
   
   /* Deploy second deployment to second server*/
   @Deployment(name="dep2", order=2) 
   @TargetsContainer("container2")
   public static Archive<?> deployment2() {
       return deployment1();
   }
   
   /* Start cache manager and its cache in first container - no real testing here */
   @Test 
   @InSequence(1) 
   @OperateOnDeployment("dep1")
   public void initServer1() {
       cache = configureCache();
   }
   
   /*
    * Start the second cache manager in the second container and start its default cache, 
    * wait for the cluster to form and do some real testing
    */
   @Test 
   @InSequence(2) 
   @OperateOnDeployment("dep2")
   public void initAndTestServer2(){
       cache = configureCache();
       waitForClusterToForm(cache, 2);
       
       // do some real testing here
       cache.put("k1", "v1");
       Assert.assertEquals("v1", cache.get("k1"));
       System.out.println("CM2 address: " + cache.getCacheManager().getAddress());
   }
   
   /*Do some real testing on server1 - typically verify results of the operation performed on server2*/
   @Test 
   @InSequence(3) 
   @OperateOnDeployment("dep1")
   public void testServer1() {
       Assert.assertEquals("v1", cache.get("k1"));
       System.out.println("CM1 address: " + cache.getCacheManager().getAddress());
   }
   
   protected Cache configureCache() {
       GlobalConfiguration glob = new GlobalConfigurationBuilder()
           .clusteredDefault()
           .build();
       Configuration loc = new ConfigurationBuilder()
           .clustering()
           .cacheMode(CacheMode.REPL_SYNC)
           .build(); 
       DefaultCacheManager dcm = new DefaultCacheManager(glob, loc, true);
       Cache cache = dcm.getCache();
       return cache;
   }
   
//   protected Cache configureCacheAlternative() { //needs infinispan-core with classifier "tests" 
//       ConfigurationBuilder loc = new ConfigurationBuilder();
//       loc.clustering().cacheMode(CacheMode.REPL_SYNC); 
//       DefaultCacheManager dcm1 = (DefaultCacheManager) TestCacheManagerFactory.createClusteredCacheManager(loc, null);
//       Cache cache = dcm1.getCache();
//       return cache;
//   }
   
   protected void waitForClusterToForm(Cache cache, int numNodes) {
       if (cache.getCacheManager() instanceof DefaultCacheManager) {
           DefaultCacheManager dcm = (DefaultCacheManager) cache.getCacheManager();
           while (dcm.getClusterSize() < numNodes) {
               try {
                   Thread.sleep(500);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
           Assert.assertEquals(numNodes, dcm.getClusterSize());
       } else {
           return;
       }
   }
   
/*
 * When using a servlet which internally instantiates a cache manager and a cache
 *
   @Test
   @RunAsClient
   @OperateOnDeployment("dep1")
   public void testSerialized(@ArquillianResource(SimpleServlet.class) URL baseUrl) throws ClientProtocolException, IOException {
       DefaultHttpClient client = new DefaultHttpClient();

       // returns the URL of the deployment (http://127.0.0.1:8180/test)
       String url = baseUrl.toString();
       System.out.println("URL = " + url);

       try {
           HttpResponse response = client.execute(new HttpGet(url + "simple?param=Martin"));
           Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
           response.getEntity().getContent().close();
       } finally {
           client.getConnectionManager().shutdown();
       }
   }
*/
   
}
