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

import org.opendaylight.controller.config.api.osgi.WaitingServiceTracker;
import org.opendaylight.groupbasedpolicy.api.StatisticsManager;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.statistics.rev151215.statistic.records.StatRecords;
import org.osgi.framework.BundleContext;

public class StatisticsManagerImplModule extends org.opendaylight.controller.config.yang.config.groupbasedpolicy.AbstractStatisticsManagerImplModule {

    private BundleContext bundleContext;

    public StatisticsManagerImplModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public StatisticsManagerImplModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.config.groupbasedpolicy.StatisticsManagerImplModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final WaitingServiceTracker<StatisticsManager> tracker = WaitingServiceTracker.create(
                StatisticsManager.class, bundleContext);
        final StatisticsManager service = tracker.waitForService(WaitingServiceTracker.FIVE_MINUTES);

        final class Instance implements StatisticsManager, AutoCloseable {
            @Override
            public void close() {
                tracker.close();
            }

            @Override
            public boolean writeStat(StatRecords record) {
                return service.writeStat(record);
            }

            @Override
            public StatRecords readStats() {
                return service.readStats();
            }
        }

        return new Instance();
    }

    void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}
