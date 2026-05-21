package com.geodispatch.model;

import java.util.List;

public record RoutingSummary(
        int totalTerritories,
        int enterpriseTerritories,
        int premiumTerritories,
        int standardTerritories,
        int totalActiveIncidents,
        int averageSlaMinutes,
        String portfolioState,
        List<RegionSummary> regionSummary
) {
}
