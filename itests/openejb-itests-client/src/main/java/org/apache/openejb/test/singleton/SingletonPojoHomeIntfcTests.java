/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openejb.test.singleton;


/**
 * [2] Should be run as the second test suite of the BasicSingletonTestClients
 * 
 * 
 * @version $Rev: 525022 $ $Date: 2007-04-02 21:19:21 -0700 (Mon, 02 Apr 2007) $
 */
public class SingletonPojoHomeIntfcTests extends BasicSingletonTestClient {

    public SingletonPojoHomeIntfcTests(){
        super("HomeIntfc.");
    }
    
    protected void setUp() throws Exception{
        super.setUp();
        final Object obj = initialContext.lookup("client/tests/singleton/BasicSingletonPojoHome");
        ejbHome = (BasicSingletonHome) javax.rmi.PortableRemoteObject.narrow( obj, BasicSingletonHome.class);
    }
    
    //===============================
    // Test home interface methods
    //
    public void test01_create(){
        try{
            ejbObject = ejbHome.createObject();
            assertNotNull( "The EJBObject is null", ejbObject );
        } catch (final Exception e){
            fail("Received Exception " + e.getClass() + " : " + e.getMessage());
        }
    }
    //
    // Test home interface methods
    //===============================

}


