package com.selfheal.starter.history;

import com.selfheal.starter.failure.FailureType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RecoveryHistory {

    private final List<RecoveryRecord> records =
            Collections.synchronizedList(
                    new ArrayList<>()
            );


    // =========================================================
    // STATISTICS
    // =========================================================

    private int totalRecoveryProcesses = 0;

    private int successfulRecoveries = 0;

    private int failedRecoveries = 0;

    private int totalAttempts = 0;


    // =========================================================
    // RECORD RECOVERY
    // =========================================================

    public synchronized void record(
            RecoveryRecord record) {

        records.add(record);

        totalRecoveryProcesses++;

        totalAttempts += record.getAttempts();

        if (record.isSuccessful()) {

            successfulRecoveries++;

        } else {

            failedRecoveries++;
        }
    }


    // =========================================================
    // GET ALL RECORDS
    // =========================================================

    public List<RecoveryRecord> getRecords() {

        synchronized (records) {

            return new ArrayList<>(records);
        }
    }


    // =========================================================
    // GET TOTAL RECOVERY PROCESSES
    // =========================================================

    public synchronized int getTotalRecoveryProcesses() {

        return totalRecoveryProcesses;
    }


    // =========================================================
    // GET SUCCESSFUL RECOVERIES
    // =========================================================

    public synchronized int getSuccessfulRecoveries() {

        return successfulRecoveries;
    }


    // =========================================================
    // GET FAILED RECOVERIES
    // =========================================================

    public synchronized int getFailedRecoveries() {

        return failedRecoveries;
    }


    // =========================================================
    // GET TOTAL ATTEMPTS
    // =========================================================

    public synchronized int getTotalAttempts() {

        return totalAttempts;
    }


    // =========================================================
    // GET SUCCESS RATE
    // =========================================================

    public synchronized double getSuccessRate() {

        if (totalRecoveryProcesses == 0) {
            return 0.0;
        }

        return
                ((double) successfulRecoveries
                        / totalRecoveryProcesses)
                        * 100.0;
    }


    // =========================================================
    // COUNT BY FAILURE TYPE
    // =========================================================

    public synchronized Map<FailureType, Integer>
    getFailureTypeCounts() {

        Map<FailureType, Integer> counts =
                new EnumMap<>(FailureType.class);

        for (RecoveryRecord record : records) {

            FailureType type =
                    record.getFailureType();

            if (type == null) {
                continue;
            }

            counts.merge(
                    type,
                    1,
                    Integer::sum
            );
        }

        return counts;
    }


    // =========================================================
    // CLEAR HISTORY
    // =========================================================

    public synchronized void clear() {

        records.clear();

        totalRecoveryProcesses = 0;

        successfulRecoveries = 0;

        failedRecoveries = 0;

        totalAttempts = 0;
    }
}