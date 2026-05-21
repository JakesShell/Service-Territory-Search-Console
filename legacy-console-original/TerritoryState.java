package com.territory;

public class TerritoryState {
    private final String name;
    private final String code;
    private final String region;

    public TerritoryState(String name, String code, String region) {
        this.name = name;
        this.code = code;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getRegion() {
        return region;
    }
}
