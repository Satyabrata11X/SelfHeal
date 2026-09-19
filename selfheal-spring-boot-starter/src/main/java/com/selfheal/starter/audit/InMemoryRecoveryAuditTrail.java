package com.selfheal.starter.audit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryRecoveryAuditTrail
        implements RecoveryAuditTrail {

    private final List<RecoveryAuditEntry> entries =
            Collections.synchronizedList(
                    new ArrayList<>()
            );

    @Override
    public void record(RecoveryAuditEntry entry) {

        if (entry == null) {
            return;
        }

        entries.add(entry);

        System.out.println(
                "[SELFHEAL-AUDIT] "
                        + "component="
                        + entry.getComponentName()
                        + ", failureType="
                        + entry.getFailureType()
                        + ", strategy="
                        + entry.getStrategy()
                        + ", attempts="
                        + entry.getAttempts()
                        + ", successful="
                        + entry.isSuccessful()
                        + ", duration="
                        + entry.getDuration()
                        + "ms"
        );
    }

    @Override
    public List<RecoveryAuditEntry> findAll() {

        synchronized (entries) {
            return new ArrayList<>(entries);
        }
    }

    @Override
    public void clear() {

        synchronized (entries) {
            entries.clear();
        }
    }

    @Override
    public int size() {
        return entries.size();
    }
}