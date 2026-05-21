package com.geodispatch.model;

public record TerritoryState(
        String name,
        String code,
        String region,
        String cloudRegion,
        String primaryDataCenter,
        String supportQueue,
        String serviceTier,
        int slaMinutes,
        String coverageStatus,
        int activeIncidents
) {
}
