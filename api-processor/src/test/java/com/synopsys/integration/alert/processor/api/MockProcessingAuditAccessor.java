package com.synopsys.integration.alert.processor.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.alert.common.persistence.accessor.ProcessingAuditAccessor;

public class MockProcessingAuditAccessor implements ProcessingAuditAccessor {
    private Map<UUID, Set<Long>> auditEntries = new HashMap<>();

    @Override
    public void createOrUpdatePendingAuditEntryForJob(UUID jobId, Set<Long> notificationIds) {
        auditEntries.put(jobId, notificationIds);
    }

    @Override
    public void setAuditEntrySuccess(UUID jobId, Set<Long> notificationIds) {

    }

    @Override
    public void setAuditEntryFailure(UUID jobId, Set<Long> notificationIds, String errorMessage, @Nullable Throwable exception) {

    }

    public Map<UUID, Set<Long>> getAuditEntries() {
        return auditEntries;
    }
}
