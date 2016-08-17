/*
 * Copyright (c) 2015 Cisco Systems, Inc.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

/*
* Generated file
*
* Generated from: yang module name: groupbasedpolicy-cfg yang module local name: policy-validator-registry-impl
* Generated by: org.opendaylight.controller.config.yangjmxgenerator.plugin.JMXGenerator
* Generated at: Thu Dec 10 17:52:04 CET 2015
*
* Do not modify this file unless it is present under src/main directory
*/
package org.opendaylight.controller.config.yang.config.groupbasedpolicy;

import org.opendaylight.controller.config.api.DependencyResolver;
import org.osgi.framework.BundleContext;

public class PolicyValidatorRegistryModuleFactory extends org.opendaylight.controller.config.yang.config.groupbasedpolicy.AbstractPolicyValidatorRegistryModuleFactory {

    @Override
    public PolicyValidatorRegistryModule instantiateModule(String instanceName, DependencyResolver dependencyResolver,
            PolicyValidatorRegistryModule oldModule, AutoCloseable oldInstance, BundleContext bundleContext) {
        PolicyValidatorRegistryModule module = super.instantiateModule(instanceName, dependencyResolver, oldModule,
                oldInstance, bundleContext);
        module.setBundleContext(bundleContext);
        return module;
    }

    @Override
    public PolicyValidatorRegistryModule instantiateModule(String instanceName, DependencyResolver dependencyResolver,
            BundleContext bundleContext) {
        PolicyValidatorRegistryModule module = super.instantiateModule(instanceName, dependencyResolver, bundleContext);
        module.setBundleContext(bundleContext);
        return module;
    }
}
