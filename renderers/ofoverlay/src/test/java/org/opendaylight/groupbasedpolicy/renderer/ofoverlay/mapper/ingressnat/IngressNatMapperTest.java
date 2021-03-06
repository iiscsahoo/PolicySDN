/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.renderer.ofoverlay.mapper.ingressnat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.opendaylight.groupbasedpolicy.dto.EgKey;
import org.opendaylight.groupbasedpolicy.dto.EpKey;
import org.opendaylight.groupbasedpolicy.dto.IndexedTenant;
import org.opendaylight.groupbasedpolicy.dto.PolicyInfo;
import org.opendaylight.groupbasedpolicy.renderer.ofoverlay.OfContext;
import org.opendaylight.groupbasedpolicy.renderer.ofoverlay.OfWriter;
import org.opendaylight.groupbasedpolicy.renderer.ofoverlay.PolicyManager;
import org.opendaylight.groupbasedpolicy.renderer.ofoverlay.endpoint.EndpointManager;
import org.opendaylight.groupbasedpolicy.renderer.ofoverlay.flow.OrdinalFactory;
import org.opendaylight.groupbasedpolicy.renderer.ofoverlay.mapper.MapperUtilsTest;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.common.rev140421.EndpointGroupId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.common.rev140421.TenantId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.endpoint.rev140421.endpoints.Endpoint;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.endpoint.rev140421.endpoints.EndpointL3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class IngressNatMapperTest extends MapperUtilsTest {

    @Before
    public void init() {
        ctx = mock(OfContext.class);
        ofWriter = mock(OfWriter.class);
        endpointManager = mock(EndpointManager.class);
        policyManager = mock(PolicyManager.class);
        policyInfo = mock(PolicyInfo.class);
        tableId = 1;
    }

    @Test
    public void testSyncFlows() throws Exception {

        // Endpoints
        Endpoint endpoint = buildEndpoint(IPV4_0, MAC_0, CONNECTOR_0).build();
        List<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(endpoint);

        // L3 endpoints
        EndpointL3 endpointL3 = buildL3Endpoint(IPV4_0, IPV4_1, MAC_0, L2).build();
        List<EndpointL3> endpointsL3 = new ArrayList<>();
        endpointsL3.add(endpointL3);

        // EgKEy set
        Set<EgKey> egKeys = new HashSet<>();
        egKeys.add(new EgKey(TENANT_ID, ENDPOINT_GROUP_1));

        when(ctx.getPolicyManager()).thenReturn(policyManager);
        when(ctx.getTenant(Mockito.any(TenantId.class))).thenReturn(getTestIndexedTenant());
        when(ctx.getEndpointManager()).thenReturn(endpointManager);
        when(endpointManager.getL3EndpointsWithNat()).thenReturn(endpointsL3);
        when(endpointManager.getEndpoint(Mockito.any(EpKey.class))).thenReturn(endpoint);
        when(endpointManager.getEgKeysForEndpoint(Mockito.any(Endpoint.class))).thenReturn(egKeys);
        when(ctx.getCurrentPolicy()).thenReturn(policyInfo);
        when(endpointManager.getExtEpsNoLocForGroup(Mockito.any(EgKey.class))).thenReturn(endpoints);

        IngressNatMapperFlows flows = mock(IngressNatMapperFlows.class);
        IngressNatMapper ingressNatMapper = new IngressNatMapper(ctx, tableId);
        ingressNatMapper.syncFlows(flows, endpoint, ofWriter);

        // Verify usage
        verify(flows, times(1)).baseFlow(Mockito.anyShort(), Mockito.anyInt(), eq(ofWriter));
        verify(flows, times(1)).createNatFlow(Mockito.anyShort(), Mockito.any(EndpointL3.class),
                Mockito.any(OrdinalFactory.EndpointFwdCtxOrdinals.class), Mockito.anyInt(), eq(ofWriter));
        verify(flows, times(1)).createArpFlow(Mockito.any(IndexedTenant.class), Mockito.any(EndpointL3.class),
                Mockito.anyInt(), eq(ofWriter));
        verify(flows, times(1)).createIngressExternalNatFlows(Mockito.anyShort(), Mockito.any(Endpoint.class),
                Mockito.any(OrdinalFactory.EndpointFwdCtxOrdinals.class), Mockito.anyInt(), eq(ofWriter));
        verify(flows, times(1)).createIngressExternalArpFlows(Mockito.anyShort(), Mockito.any(Endpoint.class),
                Mockito.any(OrdinalFactory.EndpointFwdCtxOrdinals.class), Mockito.anyInt(), eq(ofWriter));

        // Verify order
        InOrder order = inOrder(ctx, flows);
        order.verify(ctx, times(1)).getPolicyManager();
        order.verify(flows, times(1)).baseFlow(Mockito.anyShort(), Mockito.anyInt(), eq(ofWriter));
        order.verify(flows, times(1)).createNatFlow(Mockito.anyShort(), Mockito.any(EndpointL3.class),
                Mockito.any(OrdinalFactory.EndpointFwdCtxOrdinals.class), Mockito.anyInt(), eq(ofWriter));
        order.verify(ctx, times(1)).getTenant(Mockito.any(TenantId.class));
        order.verify(flows, times(1)).createArpFlow(Mockito.any(IndexedTenant.class), Mockito.any(EndpointL3.class),
                Mockito.anyInt(), eq(ofWriter));
        order.verify(ctx, times(1)).getCurrentPolicy();
        order.verify(ctx, times(1)).getEndpointManager();
        order.verify(flows, times(1)).createIngressExternalNatFlows(Mockito.anyShort(), Mockito.any(Endpoint.class),
                Mockito.any(OrdinalFactory.EndpointFwdCtxOrdinals.class), Mockito.anyInt(), eq(ofWriter));
        order.verify(flows, times(1)).createIngressExternalArpFlows(Mockito.anyShort(), Mockito.any(Endpoint.class),
                Mockito.any(OrdinalFactory.EndpointFwdCtxOrdinals.class), Mockito.anyInt(), eq(ofWriter));
    }
}
