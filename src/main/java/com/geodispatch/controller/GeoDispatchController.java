package com.geodispatch.controller;

import com.geodispatch.model.RoutingDecision;
import com.geodispatch.model.RoutingSummary;
import com.geodispatch.model.TerritoryState;
import com.geodispatch.service.GeoDispatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeoDispatchController {
    private final GeoDispatchService service;

    public GeoDispatchController(GeoDispatchService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "healthy",
                "service", "GeoDispatch Cloud Service Territory & SLA Routing Console",
                "version", "2.0.0",
                "timestamp", Instant.now().toString()
        );
    }

    @GetMapping("/territories")
    public Map<String, Object> territories(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "All") String region,
            @RequestParam(required = false, defaultValue = "All") String tier
    ) {
        List<TerritoryState> result = service.search(q);

        if (!region.equalsIgnoreCase("All")) {
            result = result.stream()
                    .filter(territory -> territory.region().equalsIgnoreCase(region))
                    .toList();
        }

        if (!tier.equalsIgnoreCase("All")) {
            result = result.stream()
                    .filter(territory -> territory.serviceTier().equalsIgnoreCase(tier))
                    .toList();
        }

        return Map.of(
                "count", result.size(),
                "territories", result
        );
    }

    @GetMapping("/coverage/{stateCode}")
    public ResponseEntity<?> coverage(@PathVariable String stateCode) {
        return service.findByCode(stateCode)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
                        "error", "No covered territory found for state code: " + stateCode
                )));
    }

    @GetMapping("/route/{stateCode}")
    public ResponseEntity<?> route(@PathVariable String stateCode) {
        try {
            RoutingDecision decision = service.routeByStateCode(stateCode);
            return ResponseEntity.ok(decision);
        } catch (IllegalArgumentException error) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", error.getMessage()
            ));
        }
    }

    @GetMapping("/routing-summary")
    public RoutingSummary routingSummary() {
        return service.buildRoutingSummary();
    }
}
