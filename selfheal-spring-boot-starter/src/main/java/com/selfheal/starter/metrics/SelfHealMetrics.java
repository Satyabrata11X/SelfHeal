package com.selfheal.starter.metrics;

import com.selfheal.starter.failure.FailureType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class SelfHealMetrics {

    // =========================================================
    // HEALTH CHECK METRICS
    // =========================================================

    private final AtomicLong totalHealthChecks =
            new AtomicLong(0);

    private final AtomicLong successfulHealthChecks =
            new AtomicLong(0);

    private final AtomicLong failedHealthChecks =
            new AtomicLong(0);


    // =========================================================
    // FAILURE METRICS
    // =========================================================

    private final AtomicLong totalFailuresDetected =
            new AtomicLong(0);

    private final Map<FailureType, AtomicLong> failureTypeCounts =
            createFailureTypeCounters();


    // =========================================================
    // RECOVERY METRICS
    // =========================================================

    private final AtomicLong totalRecoveryProcesses =
            new AtomicLong(0);

    private final AtomicLong successfulRecoveries =
            new AtomicLong(0);

    private final AtomicLong failedRecoveries =
            new AtomicLong(0);

    private final AtomicLong totalRecoveryAttempts =
            new AtomicLong(0);


    // =========================================================
    // LAST OBSERVED VALUES
    // =========================================================

    private final AtomicReference<Long> lastResponseTime =
            new AtomicReference<>(0L);

    private final AtomicReference<Long> lastRecoveryDuration =
            new AtomicReference<>(0L);

    private final AtomicReference<FailureType> lastFailureType =
            new AtomicReference<>(null);


    // =========================================================
    // CREATE FAILURE COUNTERS
    // =========================================================

    private Map<FailureType, AtomicLong>
    createFailureTypeCounters() {

        Map<FailureType, AtomicLong> counters =
                new EnumMap<>(FailureType.class);

        for (FailureType type : FailureType.values()) {

            counters.put(
                    type,
                    new AtomicLong(0)
            );
        }

        return counters;
    }


    // =========================================================
    // RECORD HEALTH CHECK
    // =========================================================

    public void recordHealthCheck(
            boolean healthy,
            long responseTime) {

        totalHealthChecks.incrementAndGet();

        lastResponseTime.set(
                responseTime
        );

        if (healthy) {

            successfulHealthChecks.incrementAndGet();

        } else {

            failedHealthChecks.incrementAndGet();
        }
    }


    // =========================================================
    // RECORD FAILURE
    // =========================================================

    public void recordFailureDetected(
            FailureType failureType) {

        totalFailuresDetected.incrementAndGet();

        lastFailureType.set(
                failureType
        );

        if (failureType != null) {

            AtomicLong counter =
                    failureTypeCounts.get(
                            failureType
                    );

            if (counter != null) {

                counter.incrementAndGet();
            }
        }
    }


    // =========================================================
    // RECORD RECOVERY
    // =========================================================

    public void recordRecovery(
            boolean successful,
            int attempts,
            long duration) {

        totalRecoveryProcesses.incrementAndGet();

        totalRecoveryAttempts.addAndGet(
                attempts
        );

        lastRecoveryDuration.set(
                duration
        );

        if (successful) {

            successfulRecoveries.incrementAndGet();

        } else {

            failedRecoveries.incrementAndGet();
        }
    }


    // =========================================================
    // GET HEALTH CHECK METRICS
    // =========================================================

    public long getTotalHealthChecks() {

        return totalHealthChecks.get();
    }

    public long getSuccessfulHealthChecks() {

        return successfulHealthChecks.get();
    }

    public long getFailedHealthChecks() {

        return failedHealthChecks.get();
    }


    // =========================================================
    // GET FAILURE METRICS
    // =========================================================

    public long getTotalFailuresDetected() {

        return totalFailuresDetected.get();
    }

    public FailureType getLastFailureType() {

        return lastFailureType.get();
    }

    public Map<FailureType, Long>
    getFailureTypeCounts() {

        Map<FailureType, Long> result =
                new EnumMap<>(FailureType.class);

        failureTypeCounts.forEach(
                (type, counter) ->
                        result.put(
                                type,
                                counter.get()
                        )
        );

        return Collections.unmodifiableMap(
                result
        );
    }


    // =========================================================
    // GET RECOVERY METRICS
    // =========================================================

    public long getTotalRecoveryProcesses() {

        return totalRecoveryProcesses.get();
    }

    public long getSuccessfulRecoveries() {

        return successfulRecoveries.get();
    }

    public long getFailedRecoveries() {

        return failedRecoveries.get();
    }

    public long getTotalRecoveryAttempts() {

        return totalRecoveryAttempts.get();
    }


    // =========================================================
    // GET LAST VALUES
    // =========================================================

    public long getLastResponseTime() {

        return lastResponseTime.get();
    }

    public long getLastRecoveryDuration() {

        return lastRecoveryDuration.get();
    }


    // =========================================================
    // HEALTH CHECK SUCCESS RATE
    // =========================================================

    public double getHealthCheckSuccessRate() {

        long total =
                totalHealthChecks.get();

        if (total == 0) {

            return 0.0;
        }

        return
                ((double) successfulHealthChecks.get()
                        / total)
                        * 100.0;
    }


    // =========================================================
    // RECOVERY SUCCESS RATE
    // =========================================================

    public double getRecoverySuccessRate() {

        long total =
                totalRecoveryProcesses.get();

        if (total == 0) {

            return 0.0;
        }

        return
                ((double) successfulRecoveries.get()
                        / total)
                        * 100.0;
    }


    // =========================================================
    // RESET
    // =========================================================

    public void reset() {

        totalHealthChecks.set(0);

        successfulHealthChecks.set(0);

        failedHealthChecks.set(0);

        totalFailuresDetected.set(0);

        totalRecoveryProcesses.set(0);

        successfulRecoveries.set(0);

        failedRecoveries.set(0);

        totalRecoveryAttempts.set(0);

        lastResponseTime.set(0L);

        lastRecoveryDuration.set(0L);

        lastFailureType.set(null);

        for (AtomicLong counter :
                failureTypeCounts.values()) {

            counter.set(0);
        }
    }
}