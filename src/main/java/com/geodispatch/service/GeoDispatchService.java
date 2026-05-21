package com.geodispatch.service;

import com.geodispatch.model.RegionSummary;
import com.geodispatch.model.RoutingDecision;
import com.geodispatch.model.RoutingSummary;
import com.geodispatch.model.TerritoryState;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class GeoDispatchService {
    private final List<TerritoryState> territories = buildTerritories();

    public List<TerritoryState> getAllTerritories() {
        return territories;
    }

    public List<TerritoryState> search(String query) {
        if (query == null || query.isBlank()) {
            return territories;
        }

        String normalized = query.toLowerCase(Locale.ROOT).trim();

        return territories.stream()
                .filter(territory ->
                        territory.name().toLowerCase(Locale.ROOT).contains(normalized)
                                || territory.code().toLowerCase(Locale.ROOT).contains(normalized)
                                || territory.region().toLowerCase(Locale.ROOT).contains(normalized)
                                || territory.serviceTier().toLowerCase(Locale.ROOT).contains(normalized)
                                || territory.supportQueue().toLowerCase(Locale.ROOT).contains(normalized)
                                || territory.cloudRegion().toLowerCase(Locale.ROOT).contains(normalized))
                .sorted(Comparator.comparing(TerritoryState::region).thenComparing(TerritoryState::name))
                .toList();
    }

    public List<TerritoryState> filterByRegion(String region) {
        if (region == null || region.equalsIgnoreCase("All")) {
            return territories;
        }

        String normalized = region.toLowerCase(Locale.ROOT).trim();

        return territories.stream()
                .filter(territory -> territory.region().toLowerCase(Locale.ROOT).equals(normalized))
                .sorted(Comparator.comparing(TerritoryState::name))
                .toList();
    }

    public List<TerritoryState> filterByTier(String tier) {
        if (tier == null || tier.equalsIgnoreCase("All")) {
            return territories;
        }

        String normalized = tier.toLowerCase(Locale.ROOT).trim();

        return territories.stream()
                .filter(territory -> territory.serviceTier().toLowerCase(Locale.ROOT).equals(normalized))
                .sorted(Comparator.comparing(TerritoryState::region).thenComparing(TerritoryState::name))
                .toList();
    }

    public Optional<TerritoryState> findByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }

        String normalized = code.toUpperCase(Locale.ROOT).trim();

        return territories.stream()
                .filter(territory -> territory.code().equals(normalized))
                .findFirst();
    }

    public RoutingDecision routeByStateCode(String code) {
        TerritoryState territory = findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("No covered territory found for state code: " + code));

        return buildRoutingDecision(territory);
    }

    public RoutingSummary buildRoutingSummary() {
        int enterprise = countByTier("Enterprise");
        int premium = countByTier("Premium");
        int standard = countByTier("Standard");
        int activeIncidents = territories.stream().mapToInt(TerritoryState::activeIncidents).sum();
        int averageSla = Math.round((float) territories.stream().mapToInt(TerritoryState::slaMinutes).average().orElse(0));

        String portfolioState = activeIncidents >= 30
                ? "High Load"
                : activeIncidents >= 18
                    ? "Watch"
                    : "Operational";

        List<RegionSummary> regions = territories.stream()
                .map(TerritoryState::region)
                .distinct()
                .sorted()
                .map(this::buildRegionSummary)
                .toList();

        return new RoutingSummary(
                territories.size(),
                enterprise,
                premium,
                standard,
                activeIncidents,
                averageSla,
                portfolioState,
                regions
        );
    }

    private RoutingDecision buildRoutingDecision(TerritoryState territory) {
        boolean escalationRequired = territory.serviceTier().equals("Enterprise") || territory.activeIncidents() >= 3;
        String escalationZone = escalationRequired
                ? territory.region() + " Escalation Desk"
                : territory.region() + " Standard Dispatch";

        String recommendation;

        if (territory.activeIncidents() >= 4) {
            recommendation = "Escalate immediately. Territory has elevated incident load and needs regional dispatch review.";
        } else if (territory.serviceTier().equals("Enterprise")) {
            recommendation = "Route to enterprise support queue and confirm SLA ownership within 30 minutes.";
        } else if (territory.serviceTier().equals("Premium")) {
            recommendation = "Route to premium dispatch queue and monitor for SLA drift.";
        } else {
            recommendation = "Route through standard coverage queue. No escalation required unless incident volume increases.";
        }

        return new RoutingDecision(
                territory.name(),
                territory.code(),
                territory.region(),
                territory.coverageStatus(),
                territory.serviceTier(),
                territory.slaMinutes(),
                territory.cloudRegion(),
                territory.primaryDataCenter(),
                territory.supportQueue(),
                escalationRequired,
                escalationZone,
                recommendation
        );
    }

    private RegionSummary buildRegionSummary(String region) {
        List<TerritoryState> regionTerritories = filterByRegion(region);

        return new RegionSummary(
                region,
                regionTerritories.size(),
                (int) regionTerritories.stream().filter(territory -> territory.serviceTier().equals("Enterprise")).count(),
                (int) regionTerritories.stream().filter(territory -> territory.serviceTier().equals("Premium")).count(),
                (int) regionTerritories.stream().filter(territory -> territory.serviceTier().equals("Standard")).count(),
                regionTerritories.stream().mapToInt(TerritoryState::activeIncidents).sum()
        );
    }

    private int countByTier(String tier) {
        return (int) territories.stream()
                .filter(territory -> territory.serviceTier().equals(tier))
                .count();
    }

    private List<TerritoryState> buildTerritories() {
        List<String[]> baseStates = List.of(
                state("Alabama", "AL", "South"),
                state("Alaska", "AK", "West"),
                state("Arizona", "AZ", "West"),
                state("Arkansas", "AR", "South"),
                state("California", "CA", "West"),
                state("Colorado", "CO", "West"),
                state("Connecticut", "CT", "Northeast"),
                state("Delaware", "DE", "South"),
                state("Florida", "FL", "South"),
                state("Georgia", "GA", "South"),
                state("Hawaii", "HI", "West"),
                state("Idaho", "ID", "West"),
                state("Illinois", "IL", "Midwest"),
                state("Indiana", "IN", "Midwest"),
                state("Iowa", "IA", "Midwest"),
                state("Kansas", "KS", "Midwest"),
                state("Kentucky", "KY", "South"),
                state("Louisiana", "LA", "South"),
                state("Maine", "ME", "Northeast"),
                state("Maryland", "MD", "South"),
                state("Massachusetts", "MA", "Northeast"),
                state("Michigan", "MI", "Midwest"),
                state("Minnesota", "MN", "Midwest"),
                state("Mississippi", "MS", "South"),
                state("Missouri", "MO", "Midwest"),
                state("Montana", "MT", "West"),
                state("Nebraska", "NE", "Midwest"),
                state("Nevada", "NV", "West"),
                state("New Hampshire", "NH", "Northeast"),
                state("New Jersey", "NJ", "Northeast"),
                state("New Mexico", "NM", "West"),
                state("New York", "NY", "Northeast"),
                state("North Carolina", "NC", "South"),
                state("North Dakota", "ND", "Midwest"),
                state("Ohio", "OH", "Midwest"),
                state("Oklahoma", "OK", "South"),
                state("Oregon", "OR", "West"),
                state("Pennsylvania", "PA", "Northeast"),
                state("Rhode Island", "RI", "Northeast"),
                state("South Carolina", "SC", "South"),
                state("South Dakota", "SD", "Midwest"),
                state("Tennessee", "TN", "South"),
                state("Texas", "TX", "South"),
                state("Utah", "UT", "West"),
                state("Vermont", "VT", "Northeast"),
                state("Virginia", "VA", "South"),
                state("Washington", "WA", "West"),
                state("West Virginia", "WV", "South"),
                state("Wisconsin", "WI", "Midwest"),
                state("Wyoming", "WY", "West")
        );

        return baseStates.stream()
                .map(this::enrichState)
                .sorted(Comparator.comparing(TerritoryState::region).thenComparing(TerritoryState::name))
                .toList();
    }

    private TerritoryState enrichState(String[] state) {
        String name = state[0];
        String code = state[1];
        String region = state[2];

        Set<String> enterpriseCodes = Set.of("CA", "TX", "NY", "FL", "WA", "VA", "IL", "GA", "NJ", "MA");
        Set<String> premiumCodes = Set.of("AZ", "CO", "NC", "OH", "PA", "MI", "OR", "MD", "TN", "MN", "CT", "UT");

        String serviceTier = enterpriseCodes.contains(code)
                ? "Enterprise"
                : premiumCodes.contains(code) ? "Premium" : "Standard";

        int slaMinutes = switch (serviceTier) {
            case "Enterprise" -> 30;
            case "Premium" -> 60;
            default -> 120;
        };

        String cloudRegion = switch (region) {
            case "West" -> "us-west-2";
            case "Midwest" -> "us-east-2";
            case "Northeast" -> "us-east-1";
            default -> "us-east-1";
        };

        String dataCenter = switch (region) {
            case "West" -> "Oregon Cloud Edge";
            case "Midwest" -> "Ohio Resilience Hub";
            case "Northeast" -> "New Jersey Edge Hub";
            default -> "Virginia Cloud Hub";
        };

        String supportQueue = switch (serviceTier) {
            case "Enterprise" -> "Enterprise Routing Desk";
            case "Premium" -> "Premium Dispatch Queue";
            default -> "Standard Coverage Queue";
        };

        int activeIncidents = Map.ofEntries(
                Map.entry("CA", 4),
                Map.entry("TX", 3),
                Map.entry("NY", 4),
                Map.entry("FL", 2),
                Map.entry("WA", 2),
                Map.entry("VA", 1),
                Map.entry("IL", 2),
                Map.entry("GA", 1),
                Map.entry("NJ", 2),
                Map.entry("MA", 1),
                Map.entry("AZ", 1),
                Map.entry("OH", 1),
                Map.entry("PA", 2),
                Map.entry("NC", 1),
                Map.entry("CO", 1)
        ).getOrDefault(code, 0);

        String coverageStatus = serviceTier.equals("Enterprise")
                ? "Priority Coverage"
                : activeIncidents >= 3 ? "SLA Watch" : "Covered";

        return new TerritoryState(
                name,
                code,
                region,
                cloudRegion,
                dataCenter,
                supportQueue,
                serviceTier,
                slaMinutes,
                coverageStatus,
                activeIncidents
        );
    }

    private String[] state(String name, String code, String region) {
        return new String[]{name, code, region};
    }
}
