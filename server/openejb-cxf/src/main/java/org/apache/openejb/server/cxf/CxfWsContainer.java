/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.openejb.server.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.openejb.assembler.classic.util.ServiceConfiguration;
import org.apache.openejb.core.webservices.PortData;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.server.cxf.transport.util.CxfUtil;
import org.apache.openejb.server.httpd.HttpListener;
import org.apache.openejb.server.httpd.HttpRequest;
import org.apache.openejb.server.httpd.HttpResponse;

import javax.management.ObjectName;

public abstract class CxfWsContainer implements HttpListener {
    protected final Bus bus;
    protected final PortData port;
    protected AbstractHTTPDestination destination;
    protected CxfEndpoint endpoint;
    protected HTTPTransportFactory httpTransportFactory;
    protected final ServiceConfiguration serviceConfiguration;
    private ObjectName jmxName;

    public CxfWsContainer(final Bus bus, final HTTPTransportFactory httpTransportFactory, final PortData port, final ServiceConfiguration config) {
        this.bus = bus;
        this.port = port;
        this.serviceConfiguration = config;
        this.httpTransportFactory = httpTransportFactory;
    }

    public void start() {
        endpoint = createEndpoint();
        endpoint.publish("http://" + getFakeUrl() + ":80"); // needs to be unique and with a port
        destination = (AbstractHTTPDestination) endpoint.getServer().getDestination();

        // register an MBean for this endpoint
        this.jmxName = registerMBean();
    }

    public CxfEndpoint getEndpoint() {
        return endpoint;
    }

    protected String getFakeUrl() {
        return "" + endpoint.hashCode();
    }

    protected abstract CxfEndpoint createEndpoint();

    protected abstract ObjectName registerMBean();

    protected abstract void setWsldUrl(String wsdl);

    public void destroy() {
        unregisterMBean();

        if (endpoint != null) {
            endpoint.stop();
        }
    }

    private void unregisterMBean() {
        LocalMBeanServer.unregisterSilently(jmxName);
    }

    @Override
    public void onMessage(final HttpRequest request, final HttpResponse response) throws Exception {
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(CxfUtil.initBusLoader());
        try {
            destination.invoke(null, request.getServletContext(), request, response);
        } finally {
            if (oldLoader != null) {
                CxfUtil.clearBusLoader(oldLoader);
            }
        }
    }

}
