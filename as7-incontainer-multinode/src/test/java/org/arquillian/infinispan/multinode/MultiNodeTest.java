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

@RunWith(Arquillian.class)
public class MultiNodeTest extends BaseMultiNodeTest
{
   @Deployment(name=DEPLOYMENT1) 
   @TargetsContainer(CONTAINER1)
   public static Archive<?> deployment1() {
       return DeploymentBuilder.basic();
   }
   
   @Deployment(name=DEPLOYMENT2) 
   @TargetsContainer(CONTAINER2)
   public static Archive<?> deployment2() {
       return DeploymentBuilder.basic();
   }
   
   @Test
   @InSequence(INIT+1)
   @OperateOnDeployment(DEPLOYMENT2)
   public void doSomeRealTestingOnServer2() {
       cache.put("k1", "v1");
       Assert.assertEquals("v1", cache.get("k1"));
       System.out.println("CM2 address: " + cache.getCacheManager().getAddress());
   }
   
   @Test
   @InSequence(INIT+2)
   @OperateOnDeployment(DEPLOYMENT1)
   public void doSomeRealTestingOnServer1() {
       Assert.assertEquals("v1", cache.get("k1"));
       System.out.println("CM1 address: " + cache.getCacheManager().getAddress());
   }
   
   @Override
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
}
