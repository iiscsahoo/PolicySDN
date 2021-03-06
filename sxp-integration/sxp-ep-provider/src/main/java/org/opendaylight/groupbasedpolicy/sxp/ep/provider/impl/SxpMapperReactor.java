/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.sxp.ep.provider.impl;

import com.google.common.util.concurrent.ListenableFuture;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.config.groupbasedpolicy.sxp.integration.sxp.ep.provider.model.rev160302.sxp.ep.mapper.EndpointForwardingTemplateBySubnet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.controller.config.groupbasedpolicy.sxp.integration.sxp.ep.provider.model.rev160302.sxp.ep.mapper.EndpointPolicyTemplateBySgt;
import org.opendaylight.yang.gen.v1.urn.opendaylight.sxp.database.rev160308.master.database.fields.MasterDatabaseBinding;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * Purpose: process given ip-sgt binding and EP-template in order to create L3-EP
 */
public interface SxpMapperReactor {

    /**
     * apply sgt/ip binding on policy template and delegate to appropriate GBP service
     *
     * @param epPolicyTemplate      policy template
     * @param epForwardingTemplate  forwarding template
     * @param masterDatabaseBinding sxpMasterDB item
     */
    ListenableFuture<RpcResult<Void>> processTemplatesAndSxpMasterDB(EndpointPolicyTemplateBySgt epPolicyTemplate,
                                                                     EndpointForwardingTemplateBySubnet epForwardingTemplate,
                                                                     MasterDatabaseBinding masterDatabaseBinding);
}
