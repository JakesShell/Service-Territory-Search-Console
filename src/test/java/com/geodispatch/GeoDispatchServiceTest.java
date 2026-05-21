package com.geodispatch;

import com.geodispatch.model.RoutingDecision;
import com.geodispatch.model.RoutingSummary;
import com.geodispatch.model.TerritoryState;
import com.geodispatch.service.GeoDispatchService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoDispatchServiceTest {
    private final GeoDispatchService service = new GeoDispatchService();

    @Test
    void returnsAllFiftyTerritories() {
        List<TerritoryState> territories = service.getAllTerritories();

        assertEquals(50, territories.size());
    }

    @Test
    void searchesByStateNameCodeRegionAndTier() {
        assertFalse(service.search("California").isEmpty());
        assertFalse(service.search("CA").isEmpty());
        assertFalse(service.search("West").isEmpty());
        assertFalse(service.search("Enterprise").isEmpty());
    }

    @Test
    void routesEnterpriseStateToEscalationPath() {
        RoutingDecision decision = service.routeByStateCode("CA");

        assertEquals("California", decision.stateName());
        assertEquals("Enterprise", decision.serviceTier());
        assertEquals(30, decision.slaMinutes());
        assertTrue(decision.escalationRequired());
        assertTrue(decision.recommendation().contains("Escalate") || decision.recommendation().contains("enterprise"));
    }

    @Test
    void routesStandardStateWithoutEscalationWhenLoadIsNormal() {
        RoutingDecision decision = service.routeByStateCode("IA");

        assertEquals("Iowa", decision.stateName());
        assertEquals("Standard", decision.serviceTier());
        assertEquals(120, decision.slaMinutes());
        assertFalse(decision.escalationRequired());
    }

    @Test
    void buildsRoutingSummary() {
        RoutingSummary summary = service.buildRoutingSummary();

        assertEquals(50, summary.totalTerritories());
        assertTrue(summary.enterpriseTerritories() > 0);
        assertTrue(summary.premiumTerritories() > 0);
        assertTrue(summary.standardTerritories() > 0);
        assertTrue(summary.totalActiveIncidents() > 0);
        assertFalse(summary.regionSummary().isEmpty());
    }
}
