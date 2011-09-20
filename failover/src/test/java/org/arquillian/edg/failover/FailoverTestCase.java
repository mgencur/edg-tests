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
package org.arquillian.edg.failover;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.container.test.api.Config;
import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FailoverTest which tests manual starting/stopping/killing of JBoss AS
 * instances.
 * 
 * Server instances can be set up to be controlled manually in the arquillian.xml
 * file using managed="false" attribute on <container> tag. The default value is
 * managed="true" which means that we cannot control those instances and they are
 * started and stopped automatically before/after test.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin gencur</a>
 * @version $Revision: $
 */
@RunWith(Arquillian.class)
public class FailoverTestCase {
   
    private static final String CONTAINER1 = "container1"; 
    private static final String CONTAINER2 = "container2"; 
    private static final String DEPLOYMENT1 = "dep.container1"; 
   
    @ArquillianResource
    private ContainerController controller;
    
    @ArquillianResource
    private Deployer deployer;
   
    /*
     * @Deployment needs to be marked as managed="false" so that
     * we can control deploying manually via Deployer's deploy/undeploy
     * methods
     */
    @Deployment(name = DEPLOYMENT1, managed=true)
    @TargetsContainer(CONTAINER1)
    public static WebArchive createTestDeployment() {
        return Deployments.createActiveClient();
    }
    
    /*
     * We don't have a container available at this point so we have to run in Client mode.
     * All the subsequent @Test methods will be already InContainer.
     * Set @Deployment's managed flag to "false" when intending to try @RunAsClient
     */
//    @Test
//    @RunAsClient
//    public void startAndDeploy() throws Exception {
//       controller.start(CONTAINER1);
//       System.out.println("===Container1 started===");
//       deployer.deploy(DEPLOYMENT1);
//       System.out.println("===Deployment deployed===");
//    }
    
    @Test
    public void stopTest() throws Exception {
       System.out.println("=== Before Container 2 Started ===");
       controller.start(CONTAINER2); //, new Config().add("managementPort", "29999").map()
       System.out.println("===Container2 started===");
       controller.stop(CONTAINER2);
       System.out.println("===Container2 stopped===");
    }
    
    @Test
    public void killTest() throws Exception {
       controller.start(CONTAINER2, new Config().add("managementPort", "19999").map());
       System.out.println("===Container2 started again===");
       controller.kill(CONTAINER2);
       //this is now implemented the same way as stop -> softly
       System.out.println("===Container2 killed===");
    }
}
