package com.geodispatch.model;

public record RoutingDecision(
        String stateName,
        String stateCode,
        String region,
        String coverageStatus,
        String serviceTier,
        int slaMinutes,
        String cloudRegion,
        String primaryDataCenter,
        String supportQueue,
        boolean escalationRequired,
        String escalationZone,
        String recommendation
) {
}
