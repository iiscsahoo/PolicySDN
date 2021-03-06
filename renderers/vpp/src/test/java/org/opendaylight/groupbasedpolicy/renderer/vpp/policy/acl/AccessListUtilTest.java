/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.renderer.vpp.policy.acl;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.groupbasedpolicy.renderer.vpp.policy.PolicyContext;

public class AccessListUtilTest extends TestResources {

    private PolicyContext ctx;

    @Before
    public void init() {
        ctx = super.createPolicyContext();
    }

    @Test
    public void resolveAclsOnInterfaceTest() {
        // TODO add more checking
        List<AccessListWrapper> acls =
                AccessListUtil.resolveAclsOnInterface(rendererEndpoint(l2AddrEp2).build().getKey(), ctx);
        Assert.assertEquals(2, acls.size());
        Assert.assertEquals(2, acls.stream().map(AccessListWrapper::getDirection).collect(Collectors.toSet()).size());
        acls.stream().forEach(ace -> {
            // allow peer + deny rest of tenant net + permit external
            if (ace instanceof IngressAccessListWrapper) {
                Assert.assertEquals(3, ace.readRules().size());
            } else if (ace instanceof EgressAccessListWrapper) {
                Assert.assertEquals(3, ace.readRules().size());
            }
        });
    }
}
