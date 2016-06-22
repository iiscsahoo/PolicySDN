/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.renderer.ios_xe_provider.impl.util;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.groupbasedpolicy.api.sf.ChainActionDefinition;
import org.opendaylight.groupbasedpolicy.renderer.ios_xe_provider.impl.manager.PolicyManagerImpl;
import org.opendaylight.groupbasedpolicy.renderer.ios_xe_provider.impl.writer.PolicyWriter;
import org.opendaylight.sfc.provider.api.SfcProviderRenderedPathAPI;
import org.opendaylight.sfc.provider.api.SfcProviderServiceForwarderAPI;
import org.opendaylight.sfc.provider.api.SfcProviderServicePathAPI;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.RspName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SfName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SfcName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SffName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.common.rev151017.SfpName;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.CreateRenderedPathInput;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.RenderedServicePath;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.RenderedServicePathBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.rendered.service.path.RenderedServicePathHop;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.rsp.rev140701.rendered.service.paths.rendered.service.path.RenderedServicePathHopBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarderBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.ServiceFunctionPaths;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.ServiceFunctionPathsBuilder;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.ServiceFunctionPath;
import org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sfp.rev140701.service.function.paths.ServiceFunctionPathBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddress;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.ServiceChain;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.ServiceChainBuilder;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.policy.map.Class;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.ServicePathBuilder;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.service.function.forwarder.Local;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.service.function.forwarder.LocalBuilder;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.service.function.forwarder.ServiceFfName;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.service.path.config.service.chain.path.mode.service.index.services.ServiceTypeChoice;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.service.path.config.service.chain.path.mode.service.index.services.service.type.choice.ServiceFunction;
import org.opendaylight.yang.gen.v1.urn.ios.rev160308._native.service.chain.service.path.config.service.chain.path.mode.service.index.services.service.type.choice.ServiceFunctionForwarder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.common.rev140421.ParameterName;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.common.rev140421.TenantId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.policy.rev140421.subject.feature.instance.ParameterValue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.policy.rev140421.subject.feature.instance.ParameterValueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.renderer.rev151103.has.rule.group.with.renderer.endpoint.participation.RuleGroupWithRendererEndpointParticipationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.renderer.rev151103.renderers.renderer.renderer.policy.configuration.renderer.endpoints.renderer.endpoint.PeerEndpointWithPolicy;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.renderer.rev151103.renderers.renderer.renderer.policy.configuration.renderer.endpoints.renderer.endpoint.PeerEndpointWithPolicyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.resolved.policy.rev150828.has.actions.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.resolved.policy.rev150828.has.actions.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.sxp.database.rev160308.Sgt;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test for {@link ServiceChainingUtil}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ServiceChainingUtil.class,
        SfcProviderServicePathAPI.class,
        SfcProviderRenderedPathAPI.class,
        SfcProviderServiceForwarderAPI.class
})
public class ServiceChainingUtilTest {

    @Captor
    private ArgumentCaptor<RspName> rspNameCaptor;
    @Captor
    private ArgumentCaptor<SffName> sffNameCaptor;
    @Captor
    private ArgumentCaptor<ServiceFunctionPath> sfpCaptor;
    @Captor
    private ArgumentCaptor<CreateRenderedPathInput> createRspCaptor;
    @Captor
    private ArgumentCaptor<RenderedServicePath> rspCaptor;
    @Captor
    private ArgumentCaptor<List<Class>> listClassCaptor;
    @Mock
    private PolicyWriter policyWriter;
    @Mock
    private DataBroker dataBroker;
    @Mock
    private ReadWriteTransaction rwTx;

    @Before
    public void setUp() throws Exception {
        final NodeId currentNodeId = new NodeId("unit-node-01");
        Mockito.when(policyWriter.getCurrentNodeId()).thenReturn(currentNodeId);

        final String managementIpAddress = "1.2.3.5";
        Mockito.when(policyWriter.getManagementIpAddress()).thenReturn(managementIpAddress);

        Mockito.when(policyWriter.getCurrentMountpoint()).thenReturn(dataBroker);
        Mockito.when(dataBroker.newReadWriteTransaction()).thenReturn(rwTx);
    }

    @Test
    public void testGetServicePath() throws Exception {
        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, false);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final ServiceFunctionPath servicePath = ServiceChainingUtil.getServicePath(Lists.newArrayList(
                createParameterValue("sfc-chain-name", sfcNameValue)
        ));
        Assert.assertEquals(sfcPath, servicePath);
    }

    private ParameterValue createParameterValue(final String name, final String value) {
        return new ParameterValueBuilder().setName(new ParameterName(name)).setStringValue(value).build();
    }

    @Test
    public void testResolveChainAction_full() throws Exception {
        final PeerEndpointWithPolicy peerEndpoint = createPeerEndpointWithPolicy();
        final Sgt sourceSgt = new Sgt(1);
        final Sgt destinationSgt = new Sgt(2);
        final Map<PolicyManagerImpl.ActionCase, Action> actionMap = createActionMap();
        final String classMapName = "unit-class-map-name-01";

        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, false);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        stub(method(SfcProviderRenderedPathAPI.class, "readRenderedServicePath")).toReturn(rsp);
        stub(method(ServiceChainingUtil.class, "setSfcPart")).toReturn(true);

        ServiceChainingUtil.resolveChainAction(peerEndpoint, sourceSgt, destinationSgt, actionMap, classMapName, policyWriter);

        Mockito.verify(policyWriter).cache(listClassCaptor.capture());
        Mockito.verifyNoMoreInteractions(policyWriter);
        Assert.assertEquals(1, listClassCaptor.getValue().size());
    }

    @Test
    public void testResolveChainAction_fullSymmetric() throws Exception {
        final PeerEndpointWithPolicy peerEndpoint = createPeerEndpointWithPolicy();
        final Sgt sourceSgt = new Sgt(1);
        final Sgt destinationSgt = new Sgt(2);
        final Map<PolicyManagerImpl.ActionCase, Action> actionMap = createActionMap();
        final String classMapName = "unit-class-map-name-01";

        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, true);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        stub(method(SfcProviderRenderedPathAPI.class, "readRenderedServicePath")).toReturn(rsp);
        stub(method(ServiceChainingUtil.class, "setSfcPart")).toReturn(true);

        ServiceChainingUtil.resolveChainAction(peerEndpoint, sourceSgt, destinationSgt, actionMap, classMapName, policyWriter);

        Mockito.verify(policyWriter).cache(listClassCaptor.capture());
        Mockito.verifyNoMoreInteractions(policyWriter);
        Assert.assertEquals(2, listClassCaptor.getValue().size());
    }

    @Test
    public void testResolveChainAction_partial01() throws Exception {
        final PeerEndpointWithPolicy peerEndpoint = createPeerEndpointWithPolicy();
        final Sgt sourceSgt = new Sgt(1);
        final Sgt destinationSgt = new Sgt(2);
        final Map<PolicyManagerImpl.ActionCase, Action> actionMap = createActionMap();
        final String classMapName = "unit-class-map-name-01";

        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, true);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        stub(method(SfcProviderRenderedPathAPI.class, "readRenderedServicePath")).toReturn(rsp);
        stub(method(ServiceChainingUtil.class, "setSfcPart")).toReturn(true);
        stub(method(ServiceChainingUtil.class, "createSymmetricRenderedPath")).toReturn(null);

        ServiceChainingUtil.resolveChainAction(peerEndpoint, sourceSgt, destinationSgt, actionMap, classMapName, policyWriter);

        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testResolveChainAction_partial02() throws Exception {
        final PeerEndpointWithPolicy peerEndpoint = createPeerEndpointWithPolicy();
        final Sgt sourceSgt = new Sgt(1);
        final Sgt destinationSgt = new Sgt(2);
        final Map<PolicyManagerImpl.ActionCase, Action> actionMap = createActionMap();
        final String classMapName = "unit-class-map-name-01";

        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, false);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        stub(method(SfcProviderRenderedPathAPI.class, "readRenderedServicePath")).toReturn(rsp);
        stub(method(ServiceChainingUtil.class, "setSfcPart")).toReturn(false);

        ServiceChainingUtil.resolveChainAction(peerEndpoint, sourceSgt, destinationSgt, actionMap, classMapName, policyWriter);

        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testResolveChainAction_partial03() throws Exception {
        final PeerEndpointWithPolicy peerEndpoint = createPeerEndpointWithPolicy(null);
        final Sgt sourceSgt = new Sgt(1);
        final Sgt destinationSgt = new Sgt(2);
        final Map<PolicyManagerImpl.ActionCase, Action> actionMap = createActionMap();
        final String classMapName = "unit-class-map-name-01";

        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, false);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        stub(method(SfcProviderRenderedPathAPI.class, "readRenderedServicePath")).toReturn(rsp);

        ServiceChainingUtil.resolveChainAction(peerEndpoint, sourceSgt, destinationSgt, actionMap, classMapName, policyWriter);

        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testResolveChainAction_partial04() throws Exception {
        final PeerEndpointWithPolicy peerEndpoint = createPeerEndpointWithPolicy(null);
        final Sgt sourceSgt = new Sgt(1);
        final Sgt destinationSgt = new Sgt(2);
        final Map<PolicyManagerImpl.ActionCase, Action> actionMap = createActionMap(false);
        final String classMapName = "unit-class-map-name-01";

        ServiceChainingUtil.resolveChainAction(peerEndpoint, sourceSgt, destinationSgt, actionMap, classMapName, policyWriter);

        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    private Map<PolicyManagerImpl.ActionCase, Action> createActionMap() {
        return createActionMap(true);
    }

    private Map<PolicyManagerImpl.ActionCase, Action> createActionMap(final boolean fillParamValue) {
        final Map<PolicyManagerImpl.ActionCase,Action> actionMap = new HashMap<>();
        final ActionBuilder actionValue = new ActionBuilder();
        if (fillParamValue) {
            actionValue.setParameterValue(Collections.singletonList(new ParameterValueBuilder()
                    .setName(new ParameterName(ChainActionDefinition.SFC_CHAIN_NAME))
                    .setStringValue("123")
                    .build()));
        }
        actionMap.put(PolicyManagerImpl.ActionCase.CHAIN, actionValue.build());
        return actionMap;
    }

    private PeerEndpointWithPolicy createPeerEndpointWithPolicy() {
        return createPeerEndpointWithPolicy(new TenantId("unit-tenant-06"));
    }

    private PeerEndpointWithPolicy createPeerEndpointWithPolicy(final TenantId tenantId) {
        return new PeerEndpointWithPolicyBuilder()
                .setRuleGroupWithRendererEndpointParticipation(Collections.singletonList(
                        new RuleGroupWithRendererEndpointParticipationBuilder()
                                .setTenantId(tenantId)
                                .build()
                ))
                .build();
    }

    @Test
    public void testCreateRenderedPath() throws Exception {
        final String sfcNameValue = "123";
        final ServiceFunctionPath sfp = createSfp(sfcNameValue, false);
        final TenantId tenantId = new TenantId("unit-tennant-01");

        final RenderedServicePath rsp = createRsp("unit-rsp-01");

        PowerMockito.mockStatic(SfcProviderRenderedPathAPI.class);
        final SfcProviderRenderedPathAPI api = PowerMockito.mock(SfcProviderRenderedPathAPI.class);
        PowerMockito.when(api.readRenderedServicePath(rspNameCaptor.capture())).thenReturn(rsp);

        final RenderedServicePath renderedPath = ServiceChainingUtil.createRenderedPath(sfp, tenantId);

        Assert.assertEquals("123_plainunit-tennant-01-gbp-rsp", rspNameCaptor.getValue().getValue());
        Assert.assertEquals(rsp, renderedPath);
    }

    @Test
    public void testCreateRenderedPath_notExisting() throws Exception {
        final String sfcNameValue = "123";
        final ServiceFunctionPath sfp = createSfp(sfcNameValue, false);
        final TenantId tenantId = new TenantId("unit-tennant-01");

        final RenderedServicePath rsp = createRsp("unit-rsp-01");

        PowerMockito.mockStatic(SfcProviderRenderedPathAPI.class);
        final SfcProviderRenderedPathAPI api = PowerMockito.mock(SfcProviderRenderedPathAPI.class);
        PowerMockito.when(api.readRenderedServicePath(rspNameCaptor.capture())).thenReturn(null);
        PowerMockito.when(api.createRenderedServicePathAndState(
                sfpCaptor.capture(), createRspCaptor.capture()
        )).thenReturn(rsp);

        final RenderedServicePath renderedPath = ServiceChainingUtil.createRenderedPath(sfp, tenantId);

        Assert.assertEquals("123_plainunit-tennant-01-gbp-rsp", rspNameCaptor.getValue().getValue());

        final ServiceFunctionPath serviceFunctionPath = sfpCaptor.getValue();
        Assert.assertEquals("123_plain", serviceFunctionPath.getName().getValue());
        Assert.assertFalse(serviceFunctionPath.isSymmetric());

        final CreateRenderedPathInput createRPInput = createRspCaptor.getValue();
        Assert.assertFalse(createRPInput.isSymmetric());
        Assert.assertEquals("123_plain", createRPInput.getParentServiceFunctionPath());
        Assert.assertEquals("123_plainunit-tennant-01-gbp-rsp", createRPInput.getName());

        Assert.assertEquals(rsp, renderedPath);
    }

    private RenderedServicePath createRsp(final String rspNameValue) {
        return new RenderedServicePathBuilder()
                .setName(new RspName(rspNameValue))
                .setRenderedServicePathHop(Lists.newArrayList(createRspHop("rsp-hop-01-sf")))
                .build();
    }

    private RenderedServicePathHop createRspHop(final String sfNameValue) {
        return new RenderedServicePathHopBuilder()
                .setServiceFunctionName(new SfName(sfNameValue))
                .setServiceFunctionForwarder(new SffName(sfNameValue + "+sff"))
                .build();
    }

    private ServiceFunctionPath createSfp(final String sfcNameValue, final boolean symmetric) {
        return new ServiceFunctionPathBuilder()
                .setServiceChainName(new SfcName(sfcNameValue))
                .setName(new SfpName(sfcNameValue + "_plain"))
                .setSymmetric(symmetric)
                .build();
    }

    @Test
    public void testCreateSymmetricRenderedPath() throws Exception {
        final ServiceFunctionPath sfp = createSfp("unit-sfp-02", false);
        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        final TenantId tennantId = new TenantId("tenant-02");

        PowerMockito.mockStatic(SfcProviderRenderedPathAPI.class);
        final SfcProviderRenderedPathAPI api = PowerMockito.mock(SfcProviderRenderedPathAPI.class);
        PowerMockito.when(api.readRenderedServicePath(rspNameCaptor.capture())).thenReturn(rsp);

        final RenderedServicePath symmetricRenderedPath = ServiceChainingUtil.createSymmetricRenderedPath(sfp, rsp, tennantId);

        Assert.assertEquals("unit-sfp-02_plaintenant-02-gbp-rsp-Reverse", rspNameCaptor.getValue().getValue());
        Assert.assertEquals(rsp, symmetricRenderedPath);
    }

    @Test
    public void testCreateSymmetricRenderedPath_notExisting() throws Exception {
        final ServiceFunctionPath sfp = createSfp("unit-sfp-02", false);
        final RenderedServicePath rsp = createRsp("unit-rsp-02");
        final TenantId tennantId = new TenantId("tenant-02");

        PowerMockito.mockStatic(SfcProviderRenderedPathAPI.class);
        final SfcProviderRenderedPathAPI api = PowerMockito.mock(SfcProviderRenderedPathAPI.class);
        PowerMockito.when(api.readRenderedServicePath(rspNameCaptor.capture())).thenReturn(null);
        PowerMockito.when(api.createSymmetricRenderedServicePathAndState(rspCaptor.capture())).thenReturn(rsp);

        final RenderedServicePath symmetricRenderedPath = ServiceChainingUtil.createSymmetricRenderedPath(sfp, rsp, tennantId);

        Assert.assertEquals("unit-sfp-02_plaintenant-02-gbp-rsp-Reverse", rspNameCaptor.getValue().getValue());
        Assert.assertEquals(rsp, rspCaptor.getValue());
        Assert.assertEquals(rsp, symmetricRenderedPath);
    }

    @Test
    public void testCheckLocalForwarderPresence() throws Exception {
        final Local local = new LocalBuilder().build();

        Mockito.when(rwTx.read(Matchers.eq(LogicalDatastoreType.CONFIGURATION), Matchers.<InstanceIdentifier<Local>>any()))
                .thenReturn(Futures.<Optional<Local>, ReadFailedException>immediateCheckedFuture(Optional.of(local)))
                .thenReturn(Futures.<Optional<Local>, ReadFailedException>immediateCheckedFuture(Optional.absent()))
                .thenReturn(Futures.<Optional<Local>, ReadFailedException>immediateFailedCheckedFuture(new ReadFailedException("n/a")));

        Assert.assertTrue(ServiceChainingUtil.checkLocalForwarderPresence(dataBroker));
        Assert.assertFalse(ServiceChainingUtil.checkLocalForwarderPresence(dataBroker));
        Assert.assertFalse(ServiceChainingUtil.checkLocalForwarderPresence(dataBroker));
        Assert.assertFalse(ServiceChainingUtil.checkLocalForwarderPresence(dataBroker));
    }

    @Test
    public void testCheckServicePathPresence() throws Exception {
        final ServiceChain serviceChainOk = new ServiceChainBuilder()
                .setServicePath(Collections.singletonList(new ServicePathBuilder().build()))
                .build();
        final ServiceChain serviceChainBad1 = new ServiceChainBuilder()
                .setServicePath(Collections.emptyList())
                .build();
        final ServiceChain serviceChainBad2 = new ServiceChainBuilder().build();

        Mockito.when(rwTx.read(Matchers.eq(LogicalDatastoreType.CONFIGURATION), Matchers.<InstanceIdentifier<ServiceChain>>any()))
                .thenReturn(Futures.<Optional<ServiceChain>, ReadFailedException>immediateCheckedFuture(Optional.of(serviceChainOk)))
                .thenReturn(Futures.<Optional<ServiceChain>, ReadFailedException>immediateCheckedFuture(Optional.of(serviceChainBad1)))
                .thenReturn(Futures.<Optional<ServiceChain>, ReadFailedException>immediateCheckedFuture(Optional.of(serviceChainBad2)))
                .thenReturn(Futures.<Optional<ServiceChain>, ReadFailedException>immediateCheckedFuture(Optional.absent()))
                .thenReturn(Futures.<Optional<ServiceChain>, ReadFailedException>immediateFailedCheckedFuture(new ReadFailedException("n/a")));

        Assert.assertFalse(ServiceChainingUtil.checkServicePathPresence(dataBroker));
        Assert.assertTrue(ServiceChainingUtil.checkServicePathPresence(dataBroker));
        Assert.assertTrue(ServiceChainingUtil.checkServicePathPresence(dataBroker));
        Assert.assertTrue(ServiceChainingUtil.checkServicePathPresence(dataBroker));
        Assert.assertFalse(ServiceChainingUtil.checkServicePathPresence(dataBroker));
    }

    @Test
    public void testFindServiceFunctionPath() throws Exception {
        final String sfcNameValue = "123";
        final ServiceFunctionPath sfcPath = createSfp(sfcNameValue, false);
        final ServiceFunctionPaths sfcPaths = new ServiceFunctionPathsBuilder()
                .setServiceFunctionPath(Collections.singletonList(sfcPath))
                .build();

        stub(method(SfcProviderServicePathAPI.class, "readAllServiceFunctionPaths")).toReturn(sfcPaths);

        final ServiceFunctionPath servicePath = ServiceChainingUtil.findServiceFunctionPath(new SfcName(sfcNameValue));
        Assert.assertEquals(sfcPath, servicePath);
    }

    @Test
    public void testSetSfcPart_success() throws Exception {
        final RenderedServicePath rsp = createRsp("unit-rsp-03");
        final org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarder
                sff = new ServiceFunctionForwarderBuilder()
                .setName(new SffName("unit-sff-03"))
                .setIpMgmtAddress(new IpAddress(new Ipv4Address("1.2.3.4")))
                .build();

        stub(method(ServiceChainingUtil.class, "checkLocalForwarderPresence")).toReturn(true);

        PowerMockito.mockStatic(SfcProviderServiceForwarderAPI.class);
        final SfcProviderServiceForwarderAPI api = PowerMockito.mock(SfcProviderServiceForwarderAPI.class);
        PowerMockito.when(api.readServiceFunctionForwarder(sffNameCaptor.capture())).thenReturn(sff);


        final boolean outcome = ServiceChainingUtil.setSfcPart(rsp, policyWriter);

        Assert.assertEquals("rsp-hop-01-sf+sff", sffNameCaptor.getValue().getValue());
        Assert.assertTrue(outcome);

        Mockito.verify(policyWriter).cache(Matchers.<ServiceFfName>any());
        Mockito.verify(policyWriter).cache(Matchers.<ServiceChain>any());
        Mockito.verify(policyWriter).getCurrentNodeId();
        Mockito.verify(policyWriter).getCurrentMountpoint();
        Mockito.verify(policyWriter).getManagementIpAddress();
        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testSetSfcPart_success_newRsp() throws Exception {
        final RenderedServicePath rsp = createRsp("unit-rsp-03");
        final org.opendaylight.yang.gen.v1.urn.cisco.params.xml.ns.yang.sfc.sff.rev140701.service.function.forwarders.ServiceFunctionForwarder
                sff = new ServiceFunctionForwarderBuilder()
                .setName(new SffName("unit-sff-03"))
                .setIpMgmtAddress(new IpAddress(new Ipv4Address("1.2.3.4")))
                .build();

        stub(method(ServiceChainingUtil.class, "checkLocalForwarderPresence")).toReturn(false);

        PowerMockito.mockStatic(SfcProviderServiceForwarderAPI.class);
        final SfcProviderServiceForwarderAPI api = PowerMockito.mock(SfcProviderServiceForwarderAPI.class);
        PowerMockito.when(api.readServiceFunctionForwarder(sffNameCaptor.capture())).thenReturn(sff);


        final boolean outcome = ServiceChainingUtil.setSfcPart(rsp, policyWriter);

        Assert.assertEquals("rsp-hop-01-sf+sff", sffNameCaptor.getValue().getValue());
        Assert.assertTrue(outcome);

        Mockito.verify(policyWriter).cache(Matchers.<Local>any());
        Mockito.verify(policyWriter).cache(Matchers.<ServiceFfName>any());
        Mockito.verify(policyWriter).cache(Matchers.<ServiceChain>any());
        Mockito.verify(policyWriter).getCurrentMountpoint();
        Mockito.verify(policyWriter, Mockito.times(2)).getManagementIpAddress();
        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testSetSfcPart_fail01() throws Exception {
        Assert.assertFalse(ServiceChainingUtil.setSfcPart(null, policyWriter));

        final RenderedServicePathBuilder rspBuilder = new RenderedServicePathBuilder().setName(new RspName("unit-rsp-05"));
        Assert.assertFalse(ServiceChainingUtil.setSfcPart(rspBuilder.build(), policyWriter));

        rspBuilder.setRenderedServicePathHop(Collections.emptyList());
        Assert.assertFalse(ServiceChainingUtil.setSfcPart(rspBuilder.build(), policyWriter));

        rspBuilder.setRenderedServicePathHop(Collections.singletonList(null));
        Assert.assertFalse(ServiceChainingUtil.setSfcPart(rspBuilder.build(), policyWriter));

        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testSetSfcPart_fail02() throws Exception {
        final RenderedServicePath rsp = createRsp("unit-rsp-03");

        Mockito.doReturn(Futures.immediateCheckedFuture(Optional.absent()))
                .when(rwTx).read(Mockito.eq(LogicalDatastoreType.CONFIGURATION), Mockito.<InstanceIdentifier<Local>>any());

        PowerMockito.mockStatic(SfcProviderServiceForwarderAPI.class);
        final SfcProviderServiceForwarderAPI api = PowerMockito.mock(SfcProviderServiceForwarderAPI.class);
        PowerMockito.when(api.readServiceFunctionForwarder(sffNameCaptor.capture())).thenReturn(null);

        final boolean outcome = ServiceChainingUtil.setSfcPart(rsp, policyWriter);

        Assert.assertEquals("rsp-hop-01-sf+sff", sffNameCaptor.getValue().getValue());
        Assert.assertFalse(outcome);

        Mockito.verify(policyWriter).getCurrentMountpoint();
        Mockito.verify(policyWriter).getManagementIpAddress();
        Mockito.verify(policyWriter).cache(Matchers.<Local>any());
        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testSetSfcPart_fail03() throws Exception {
        final RenderedServicePath rsp = createRsp("unit-rsp-03");
        final ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder()
                .setName(new SffName("unit-sff-03"))
                .setIpMgmtAddress(null);

        stub(method(ServiceChainingUtil.class, "checkLocalForwarderPresence")).toReturn(true);

        PowerMockito.mockStatic(SfcProviderServiceForwarderAPI.class);
        final SfcProviderServiceForwarderAPI api = PowerMockito.mock(SfcProviderServiceForwarderAPI.class);
        PowerMockito.when(api.readServiceFunctionForwarder(sffNameCaptor.capture())).thenReturn(
                sffBuilder.build());

        Assert.assertFalse(ServiceChainingUtil.setSfcPart(rsp, policyWriter));

        Assert.assertEquals("rsp-hop-01-sf+sff", sffNameCaptor.getValue().getValue());

        Mockito.verify(policyWriter).getCurrentMountpoint();
        Mockito.verify(policyWriter).getCurrentNodeId();
        Mockito.verifyNoMoreInteractions(policyWriter);
    }

    @Test
    public void testSetSfcPart_fail04() throws Exception {
        final RenderedServicePath rsp = createRsp("unit-rsp-03");
        final ServiceFunctionForwarderBuilder sffBuilder = new ServiceFunctionForwarderBuilder()
                .setName(new SffName("unit-sff-03"))
                .setIpMgmtAddress(new IpAddress((Ipv4Address) null));

        stub(method(ServiceChainingUtil.class, "checkLocalForwarderPresence")).toReturn(true);

        PowerMockito.mockStatic(SfcProviderServiceForwarderAPI.class);
        final SfcProviderServiceForwarderAPI api = PowerMockito.mock(SfcProviderServiceForwarderAPI.class);
        PowerMockito.when(api.readServiceFunctionForwarder(sffNameCaptor.capture())).thenReturn(
                sffBuilder.build());

        Assert.assertFalse(ServiceChainingUtil.setSfcPart(rsp, policyWriter));

        Assert.assertEquals("rsp-hop-01-sf+sff", sffNameCaptor.getValue().getValue());

        Mockito.verify(policyWriter).getCurrentMountpoint();
        Mockito.verify(policyWriter).getCurrentNodeId();
        Mockito.verifyNoMoreInteractions(policyWriter);
    }


    @Test
    public void testForwarderTypeChoice() throws Exception {
        final String sffValue = "unit-xx";
        final ServiceTypeChoice fwChoice = ServiceChainingUtil.forwarderTypeChoice(sffValue);

        Assert.assertTrue(fwChoice instanceof ServiceFunctionForwarder);
        final ServiceFunctionForwarder sff = (ServiceFunctionForwarder) fwChoice;
        Assert.assertEquals(sffValue, sff.getServiceFunctionForwarder());
    }

    @Test
    public void testFunctionTypeChoice() throws Exception {
        final String stcValue = "unit-xx";
        final ServiceTypeChoice srvTypeChoice = ServiceChainingUtil.functionTypeChoice(stcValue);

        Assert.assertTrue(srvTypeChoice instanceof ServiceFunction);
        final ServiceFunction stc = (ServiceFunction) srvTypeChoice;
        Assert.assertEquals(stcValue, stc.getServiceFunction());
    }
}