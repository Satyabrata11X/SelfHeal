package com.selfheal.starter.actuator;

import com.selfheal.starter.management.SelfHealManagementService;
import com.selfheal.starter.management.SelfHealStatus;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

import java.util.LinkedHashMap;
import java.util.Map;

public class SelfHealInfoContributor
        implements InfoContributor {

    private final SelfHealManagementService managementService;

    public SelfHealInfoContributor(
            SelfHealManagementService managementService) {

        this.managementService = managementService;
    }

    @Override
    public void contribute(Info.Builder builder) {

        SelfHealStatus status =
                managementService.getStatus();

        Map<String, Object> selfHealInfo =
                new LinkedHashMap<>();

        selfHealInfo.put(
                "component",
                status.getComponentName()
        );

        selfHealInfo.put(
                "healthy",
                status.isHealthy()
        );

        selfHealInfo.put(
                "recoveryState",
                status.getRecoveryState()
        );

        selfHealInfo.put(
                "cooldownActive",
                status.isCooldownActive()
        );

        selfHealInfo.put(
                "remainingCooldown",
                status.getRemainingCooldown() + "ms"
        );

        builder.withDetail(
                "selfheal",
                selfHealInfo
        );
    }
}