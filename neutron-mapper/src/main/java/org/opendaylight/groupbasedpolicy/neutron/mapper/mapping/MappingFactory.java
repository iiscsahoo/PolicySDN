/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.neutron.mapper.mapping;

import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.common.rev140421.UniqueId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.endpoint.rev140421.endpoints.EndpointKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.endpoint.rev140421.endpoints.EndpointL3Key;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.gbp.by.neutron.mappings.endpoints.by.ports.EndpointByPort;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.gbp.by.neutron.mappings.endpoints.by.ports.EndpointByPortBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.neutron.by.gbp.mappings.external.gateways.as.l3.endpoints.ExternalGatewayAsL3Endpoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.neutron.by.gbp.mappings.external.gateways.as.l3.endpoints.ExternalGatewayAsL3EndpointBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.neutron.by.gbp.mappings.external.gateways.as.l3.endpoints.ExternalGatewayAsL3EndpointKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.neutron.by.gbp.mappings.ports.by.endpoints.PortByEndpoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.neutron.gbp.mapper.rev150513.mappings.neutron.by.gbp.mappings.ports.by.endpoints.PortByEndpointBuilder;

public class MappingFactory {

    private MappingFactory() {
        throw new UnsupportedOperationException("cannot create an instance");
    }

    public static EndpointByPort createEndpointByPort(EndpointKey epKey, UniqueId portId) {
        return new EndpointByPortBuilder().setPortId(portId)
            .setL2Context(epKey.getL2Context())
            .setMacAddress(epKey.getMacAddress())
            .build();
    }

    public static PortByEndpoint createPortByEndpoint(UniqueId portId, EndpointKey epKey) {
        return new PortByEndpointBuilder().setPortId(portId)
            .setL2Context(epKey.getL2Context())
            .setMacAddress(epKey.getMacAddress())
            .build();
    }

    public static ExternalGatewayAsL3Endpoint createExternalGatewayByL3Endpoint(EndpointL3Key epL3Key) {
        return new ExternalGatewayAsL3EndpointBuilder().setKey(
                new ExternalGatewayAsL3EndpointKey(epL3Key.getIpAddress(), epL3Key.getL3Context()))
            .setIpAddress(epL3Key.getIpAddress())
            .setL3Context(epL3Key.getL3Context())
            .build();
    }

}
