/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.renderer.ios_xe_provider.impl.config;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.controller.config.yang.config.groupbasedpolicy.GroupbasedpolicyInstance;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.groupbasedpolicy.renderer.ios_xe_provider.impl.IosXeRendererProviderImpl;
import org.opendaylight.groupbasedpolicy.sxp.ep.provider.spi.SxpEpProviderProvider;
import org.opendaylight.mdsal.singleton.common.api.ClusterSingletonService;
import org.opendaylight.mdsal.singleton.common.api.ClusterSingletonServiceProvider;
import org.opendaylight.mdsal.singleton.common.api.ClusterSingletonServiceRegistration;
import org.opendaylight.mdsal.singleton.common.api.ServiceGroupIdentifier;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.config.ip.sgt.distribution.rev160715.IpSgtDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IosXeProviderInstance implements ClusterSingletonService, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(IosXeProviderInstance.class);

    private static final ServiceGroupIdentifier IDENTIFIER =
            ServiceGroupIdentifier.create(GroupbasedpolicyInstance.GBP_SERVICE_GROUP_IDENTIFIER);
    private final DataBroker dataBroker;
    private final BindingAwareBroker bindingAwareBroker;
    private final ClusterSingletonServiceProvider clusterSingletonService;
    private final SxpEpProviderProvider sxpEpProvider;
    private final IpSgtDistributionService ipSgtDistributor;
    private ClusterSingletonServiceRegistration singletonServiceRegistration;
    private IosXeRendererProviderImpl renderer;

    public IosXeProviderInstance(final DataBroker dataBroker,
                                 final BindingAwareBroker broker,
                                 final ClusterSingletonServiceProvider clusterSingletonService,
                                 final SxpEpProviderProvider sxpEpProvider,
                                 final RpcProviderRegistry rpcProviderRegistry) {
        this.dataBroker = Preconditions.checkNotNull(dataBroker);
        this.bindingAwareBroker = Preconditions.checkNotNull(broker);
        this.clusterSingletonService = Preconditions.checkNotNull(clusterSingletonService);
        this.sxpEpProvider = Preconditions.checkNotNull(sxpEpProvider);
        this.ipSgtDistributor = Preconditions.checkNotNull(rpcProviderRegistry.getRpcService(IpSgtDistributionService.class));
    }

    public void initialize() {
        LOG.info("Clustering session initiated for {}", this.getClass().getSimpleName());
        singletonServiceRegistration = clusterSingletonService.registerClusterSingletonService(this);
    }

    @Override
    public void instantiateServiceInstance() {
        LOG.info("Instantiating {}", this.getClass().getSimpleName());
        renderer = new IosXeRendererProviderImpl(dataBroker, bindingAwareBroker, sxpEpProvider, ipSgtDistributor);
    }

    @Override
    public ListenableFuture<Void> closeServiceInstance() {
        LOG.info("Instance {} closed", this.getClass().getSimpleName());
        renderer.close();
        return Futures.immediateFuture(null);
    }

    @Override
    public void close() throws Exception {
        LOG.info("Clustering provider closed for {}", this.getClass().getSimpleName());
        if (singletonServiceRegistration != null) {
            try {
                singletonServiceRegistration.close();
            } catch (Exception e) {
                LOG.warn("{} closed unexpectedly. Cause: {}", e.getMessage());
            }
            singletonServiceRegistration = null;
        }
    }

    @Override
    public ServiceGroupIdentifier getIdentifier() {
        return IDENTIFIER;
    }
}
