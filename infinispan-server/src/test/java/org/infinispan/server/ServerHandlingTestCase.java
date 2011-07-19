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
package org.infinispan.server;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.infinispan.arquillian.core.Infinispan;
import org.jboss.infinispan.arquillian.core.InfinispanInfo;
import org.jboss.infinispan.arquillian.model.CacheStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ServerHandlingTestCase {
   
    @Infinispan("container1")
    InfinispanInfo info1;
    
    @Infinispan("container2")
    InfinispanInfo info2;
   
    @Test
    public void shouldRetrieveGeneralInformation() throws Exception 
    {
       System.out.println("=== General info ===");
       System.out.println("port: " + info2.getMemcachedEndpoint().getPort());
       System.out.println("hostname: " + info2.getMemcachedEndpoint().getInetAddress());
    }
    
    @Test
    public void shouldRetrieveCacheManagerInformation()
    {
       System.out.println("=== CacheManager info ===");
       System.out.println(info1.getCacheManager("DefaultCacheManager").getCreatedCacheCount());
       System.out.println(info1.getDefaultCacheManager().getCacheManagerStatus());
       System.out.println(info1.getDefaultCacheManager().getCacheName());
       System.out.println(info1.getDefaultCacheManager().getClusterMembers());
       System.out.println(info1.getDefaultCacheManager().getClusterSize());
       System.out.println(info1.getDefaultCacheManager().getCreatedCacheCount());
       System.out.println(info1.getDefaultCacheManager().getDefinedCacheCount());
       System.out.println(info1.getDefaultCacheManager().getDefinedCacheNames());
       System.out.println(info1.getDefaultCacheManager().getNodeAddress());
       System.out.println(info1.getDefaultCacheManager().getPhysicalAddresses());
       System.out.println(info1.getDefaultCacheManager().getRunningCacheCount());
       System.out.println(info1.getDefaultCacheManager().getVersion());
    }
    
    @Test
    public void shouldRetrieveCacheInformation()
    {
       System.out.println("=== Cache info ===");
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getCacheName());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getCacheStatus());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getAverageReadTime());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getAverageWriteTime());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getElapsedTime());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getEvictions());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getHitRatio());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getHits());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getMisses());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getNumberOfEntries());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getReadWriteRatio());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getRemoveHits());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getRemoveMisses());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStores());
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getTimeSinceReset());
       
       //getting statistics via a common method getStatistics
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.AVERAGE_READ_TIME));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.AVERAGE_WRITE_TIME));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.ELAPSED_TIME));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.EVICTIONS));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.HIT_RATIO));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.HITS));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.MISSES));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.NUMBER_OF_ENTRIES));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.READ_WRITE_RATIO));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.REMOVE_HITS));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.REMOVE_MISSES));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.STORES));
       System.out.println(info1.getDefaultCacheManager().getDefaultCache().getStatistics(CacheStatistics.TIME_SINCE_RESET));
    }
    
    @Test
    public void infinispanInfoTest(@Infinispan("container2") InfinispanInfo info3) {
       //also possible to inject into method parameters
    }
    
}
