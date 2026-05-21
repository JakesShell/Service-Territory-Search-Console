package com.geodispatch.model;

public record RegionSummary(
        String region,
        int totalStates,
        int enterpriseStates,
        int premiumStates,
        int standardStates,
        int activeIncidents
) {
}
