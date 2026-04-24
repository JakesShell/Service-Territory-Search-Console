package com.territory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TerritorySearchService {
    private final List<TerritoryState> states = Arrays.asList(
            new TerritoryState("Alabama", "AL", "South"),
            new TerritoryState("Alaska", "AK", "West"),
            new TerritoryState("Arizona", "AZ", "West"),
            new TerritoryState("Arkansas", "AR", "South"),
            new TerritoryState("California", "CA", "West"),
            new TerritoryState("Colorado", "CO", "West"),
            new TerritoryState("Connecticut", "CT", "Northeast"),
            new TerritoryState("Delaware", "DE", "South"),
            new TerritoryState("Florida", "FL", "South"),
            new TerritoryState("Georgia", "GA", "South"),
            new TerritoryState("Hawaii", "HI", "West"),
            new TerritoryState("Idaho", "ID", "West"),
            new TerritoryState("Illinois", "IL", "Midwest"),
            new TerritoryState("Indiana", "IN", "Midwest"),
            new TerritoryState("Iowa", "IA", "Midwest"),
            new TerritoryState("Kansas", "KS", "Midwest"),
            new TerritoryState("Kentucky", "KY", "South"),
            new TerritoryState("Louisiana", "LA", "South"),
            new TerritoryState("Maine", "ME", "Northeast"),
            new TerritoryState("Maryland", "MD", "South"),
            new TerritoryState("Massachusetts", "MA", "Northeast"),
            new TerritoryState("Michigan", "MI", "Midwest"),
            new TerritoryState("Minnesota", "MN", "Midwest"),
            new TerritoryState("Mississippi", "MS", "South"),
            new TerritoryState("Missouri", "MO", "Midwest"),
            new TerritoryState("Montana", "MT", "West"),
            new TerritoryState("Nebraska", "NE", "Midwest"),
            new TerritoryState("Nevada", "NV", "West"),
            new TerritoryState("New Hampshire", "NH", "Northeast"),
            new TerritoryState("New Jersey", "NJ", "Northeast"),
            new TerritoryState("New Mexico", "NM", "West"),
            new TerritoryState("New York", "NY", "Northeast"),
            new TerritoryState("North Carolina", "NC", "South"),
            new TerritoryState("North Dakota", "ND", "Midwest"),
            new TerritoryState("Ohio", "OH", "Midwest"),
            new TerritoryState("Oklahoma", "OK", "South"),
            new TerritoryState("Oregon", "OR", "West"),
            new TerritoryState("Pennsylvania", "PA", "Northeast"),
            new TerritoryState("Rhode Island", "RI", "Northeast"),
            new TerritoryState("South Carolina", "SC", "South"),
            new TerritoryState("South Dakota", "SD", "Midwest"),
            new TerritoryState("Tennessee", "TN", "South"),
            new TerritoryState("Texas", "TX", "South"),
            new TerritoryState("Utah", "UT", "West"),
            new TerritoryState("Vermont", "VT", "Northeast"),
            new TerritoryState("Virginia", "VA", "South"),
            new TerritoryState("Washington", "WA", "West"),
            new TerritoryState("West Virginia", "WV", "South"),
            new TerritoryState("Wisconsin", "WI", "Midwest"),
            new TerritoryState("Wyoming", "WY", "West")
    );

    public List<TerritoryState> getAllStates() {
        return states;
    }

    public List<TerritoryState> searchByName(String query) {
        String normalized = query.toLowerCase(Locale.ROOT).trim();
        List<TerritoryState> matches = new ArrayList<>();

        for (TerritoryState state : states) {
            if (state.getName().toLowerCase(Locale.ROOT).contains(normalized)) {
                matches.add(state);
            }
        }

        return matches;
    }

    public TerritoryState searchByCode(String code) {
        String normalized = code.toUpperCase(Locale.ROOT).trim();

        for (TerritoryState state : states) {
            if (state.getCode().equals(normalized)) {
                return state;
            }
        }

        return null;
    }

    public List<TerritoryState> filterByRegion(String region) {
        String normalized = region.toLowerCase(Locale.ROOT).trim();
        List<TerritoryState> matches = new ArrayList<>();

        for (TerritoryState state : states) {
            if (state.getRegion().toLowerCase(Locale.ROOT).equals(normalized)) {
                matches.add(state);
            }
        }

        return matches;
    }

    public String buildCoverageSummary() {
        int northeast = 0;
        int midwest = 0;
        int south = 0;
        int west = 0;

        for (TerritoryState state : states) {
            switch (state.getRegion()) {
                case "Northeast" -> northeast++;
                case "Midwest" -> midwest++;
                case "South" -> south++;
                case "West" -> west++;
            }
        }

        return """
                Coverage Summary
                ----------------
                Northeast States: %d
                Midwest States: %d
                South States: %d
                West States: %d
                Total States: %d
                """.formatted(northeast, midwest, south, west, states.size());
    }
}
