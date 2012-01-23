/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.arquillian.infinispan.multinode;

import org.infinispan.Cache;
import org.infinispan.test.TestingUtil;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;

/**
 * Base class for multi-node functional tests (2 nodes). Users should extend this class and implement
 * {@link #configureCache()} method that configures and starts a cache. This method is called 
 * on 2 servers and the cluster formation is verified. Three test methods are needed for this
 * initialization so first real test method should be annotated @InSequence(4) (== @InSequence(INIT+1))
 * 
 * @author Martin Gencur
 * 
 */
public abstract class BaseMultiNodeTest {
    
    public static final int INIT = 3; //number of initialization methods that must run before real test methods
    public static final String DEPLOYMENT1 = "dep1";
    public static final String DEPLOYMENT2 = "dep2";
    public static final String CONTAINER1 = "container1";
    public static final String CONTAINER2 = "container2";
    
    public final int NUM_NODES_DEFAULT = 2;
    
    protected static Cache cache;
    
    protected abstract Cache configureCache();
    
    @Test 
    @InSequence(1) 
    @OperateOnDeployment(DEPLOYMENT1)
    public void initServer1() {
        cache = configureCache();
    }
    
    @Test 
    @InSequence(2) 
    @OperateOnDeployment(DEPLOYMENT2)
    public void initServer2AndVerifyClusterFormed(){
        cache = configureCache();
        waitForClusterToForm(cache, NUM_NODES_DEFAULT);
    }
    
    @Test 
    @InSequence(3) 
    @OperateOnDeployment(DEPLOYMENT1)
    public void verifyClusterFormedOnServer1() {
        waitForClusterToForm(cache, NUM_NODES_DEFAULT);
    }
    
    protected void waitForClusterToForm(Cache cache, int numNodes) {
        TestingUtil.blockUntilViewReceived(cache, numNodes, 10000);
// use this when https://github.com/infinispan/infinispan/pull/878 is pushed upstream
//        if (cache.getCacheConfiguration().clustering().cacheMode().isDistributed()) {
//            TestingUtil.waitForRehashToComplete(cache, numNodes);
//        }
    }
}
