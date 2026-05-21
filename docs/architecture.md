# GeoDispatch Architecture Notes

## System Flow

```text
Territory and service coverage data
        |
        v
GeoDispatchService applies:
- Territory lookup
- Region filtering
- SLA tier rules
- Cloud region mapping
- Active incident load
- Escalation recommendation logic
        |
        v
Spring Boot REST API exposes:
- Health endpoint
- Territory search endpoint
- Coverage lookup endpoint
- Routing decision endpoint
- Routing summary endpoint
        |
        v
Frontend dashboard renders:
- Routing state
- Territory metrics
- Searchable coverage table
- Dispatch decision panel
- Regional coverage summary
- Cloud operations context
```

## Backend Responsibilities

- Serve the static frontend from `src/main/resources/static`.
- Expose REST endpoints for health, territories, coverage, routing, and summary data.
- Keep demo territory data in memory for portfolio demonstration.
- Enrich territory records with SLA tier, support queue, cloud region, and incident load.
- Generate routing decisions for selected state codes.
- Recommend escalation paths based on enterprise tier and active incident pressure.

## Frontend Responsibilities

- Present the routing state and service coverage metrics first.
- Let users search by state, code, region, tier, queue, or cloud region.
- Show a readable service territory table.
- Keep the selected routing decision visible in a side panel.
- Summarize regional dispatch load.
- Explain the cloud operations use case clearly for reviewers.

## Cloud Deployment Fit

GeoDispatch can be containerized and deployed as a small internal service-routing tool.

A production version could add authentication, persistent data storage, real geocoding, provider-region matching, incident integrations, queue ownership logs, and cloud monitoring.
