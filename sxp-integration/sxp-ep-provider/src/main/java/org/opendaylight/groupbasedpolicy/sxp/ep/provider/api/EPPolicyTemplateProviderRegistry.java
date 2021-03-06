/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.groupbasedpolicy.sxp.ep.provider.api;

import org.opendaylight.yangtools.concepts.ObjectRegistration;

/**
 * Purpose: injection point for a {@link EPPolicyTemplateProvider}
 */
public interface EPPolicyTemplateProviderRegistry extends AutoCloseable {

    /**
     * @param templateProvider provider to register
     * @return corresponding registration
     */
    ObjectRegistration<EPPolicyTemplateProvider> registerTemplateProvider(EPPolicyTemplateProvider templateProvider);

    /**
     * @param templateProviderDistributionTarget consumer of template provider
     */
    void addDistributionTarget(TemplateProviderDistributionTarget<EPPolicyTemplateProvider> templateProviderDistributionTarget);

    @Override
    void close();
}
