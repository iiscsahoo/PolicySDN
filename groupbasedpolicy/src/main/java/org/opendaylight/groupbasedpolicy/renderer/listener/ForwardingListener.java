/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.renderer.listener;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.groupbasedpolicy.renderer.RendererManager;
import org.opendaylight.groupbasedpolicy.util.DataTreeChangeHandler;
import org.opendaylight.yang.gen.v1.urn.opendaylight.groupbasedpolicy.forwarding.rev160427.Forwarding;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public class ForwardingListener extends DataTreeChangeHandler<Forwarding> implements AutoCloseable {

    private final RendererManager rendererManager;

    public ForwardingListener(RendererManager rendererManager, DataBroker dataProvider) {
        super(dataProvider);
        this.rendererManager = rendererManager;
        registerDataTreeChangeListener(new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL,
                InstanceIdentifier.create(Forwarding.class)));
    }

    @Override
    protected void onWrite(DataObjectModification<Forwarding> rootNode, InstanceIdentifier<Forwarding> rootIdentifier) {
        rendererManager.forwardingUpdated(rootNode.getDataAfter());
    }

    @Override
    protected void onDelete(DataObjectModification<Forwarding> rootNode,
            InstanceIdentifier<Forwarding> rootIdentifier) {
        rendererManager.forwardingUpdated(rootNode.getDataAfter());
    }

    @Override
    protected void onSubtreeModified(DataObjectModification<Forwarding> rootNode,
            InstanceIdentifier<Forwarding> rootIdentifier) {
        rendererManager.forwardingUpdated(rootNode.getDataAfter());
    }

}
