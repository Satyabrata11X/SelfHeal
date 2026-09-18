package com.selfheal.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "selfheal")
public class SelfHealProperties {

    private boolean enabled = true;

    private Recovery recovery = new Recovery();

    private Monitoring monitoring = new Monitoring();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Recovery getRecovery() {
        return recovery;
    }

    public void setRecovery(Recovery recovery) {
        this.recovery = recovery;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    // =========================================================
    // RECOVERY CONFIGURATION
    // =========================================================

    public static class Recovery {

        private int maxAttempts = 3;

        private long delay = 1000;

        private String strategy = "retry";

        private double backoffMultiplier = 2.0;

        private long maxDelay = 10000;

        private long cooldown = 10000;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getDelay() {
            return delay;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        public String getStrategy() {
            return strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }

        public double getBackoffMultiplier() {
            return backoffMultiplier;
        }

        public void setBackoffMultiplier(
                double backoffMultiplier) {

            this.backoffMultiplier = backoffMultiplier;
        }

        public long getMaxDelay() {
            return maxDelay;
        }

        public void setMaxDelay(long maxDelay) {
            this.maxDelay = maxDelay;
        }

        public long getCooldown() {
            return cooldown;
        }

        public void setCooldown(long cooldown) {
            this.cooldown = cooldown;
        }
    }

    // =========================================================
    // MONITORING CONFIGURATION
    // =========================================================

    public static class Monitoring {

        private long interval = 5000;

        private long latencyThreshold = 1000;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public long getLatencyThreshold() {
            return latencyThreshold;
        }

        public void setLatencyThreshold(
                long latencyThreshold) {

            this.latencyThreshold = latencyThreshold;
        }
    }
}