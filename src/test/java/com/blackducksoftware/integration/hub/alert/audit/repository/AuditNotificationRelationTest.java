/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.alert.audit.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.blackducksoftware.integration.hub.alert.audit.repository.relation.AuditNotificationRelation;
import com.blackducksoftware.integration.hub.alert.datasource.entity.RelationTest;

public class AuditNotificationRelationTest extends RelationTest<AuditNotificationRelation> {

    @Override
    public Class<AuditNotificationRelation> getEntityClass() {
        return AuditNotificationRelation.class;
    }

    @Override
    public void assertEntityFieldsNull(final AuditNotificationRelation entity) {
        assertNull(entity.getAuditEntryId());
        assertNull(entity.getNotificationId());
    }

    @Override
    public long entitySerialId() {
        return AuditNotificationRelation.getSerialversionuid();
    }

    @Override
    public int emptyEntityHashCode() {
        return 23273;
    }

    @Override
    public void assertEntityFieldsFull(final AuditNotificationRelation entity) {
        assertNotNull(entity.getAuditEntryId());
        assertNotNull(entity.getNotificationId());
    }

    @Override
    public int entityHashCode() {
        return 23771;
    }

    @Override
    public AuditNotificationRelation createMockRelation(final Long firstId, final Long secondId) {
        return new AuditNotificationRelation(firstId, secondId);
    }

    @Override
    public AuditNotificationRelation createMockEmptyRelation() {
        return new AuditNotificationRelation();
    }

}
