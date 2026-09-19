package com.selfheal.starter.audit;

import java.util.List;

public interface RecoveryAuditTrail {

    void record(RecoveryAuditEntry entry);

    List<RecoveryAuditEntry> findAll();

    void clear();

    int size();
}