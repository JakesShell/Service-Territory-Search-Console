const state = {
  territories: [],
  selectedCode: null,
  summary: null
};

const elements = {
  portfolioState: document.querySelector("#portfolio-state"),
  portfolioSummary: document.querySelector("#portfolio-summary"),
  total: document.querySelector("#metric-total"),
  enterprise: document.querySelector("#metric-enterprise"),
  incidents: document.querySelector("#metric-incidents"),
  sla: document.querySelector("#metric-sla"),
  table: document.querySelector("#territory-table"),
  detail: document.querySelector("#routing-detail"),
  regionSummary: document.querySelector("#region-summary"),
  searchInput: document.querySelector("#search-input"),
  regionFilter: document.querySelector("#region-filter"),
  tierFilter: document.querySelector("#tier-filter"),
  routeCalifornia: document.querySelector("#route-california"),
  resetFilters: document.querySelector("#reset-filters")
};

async function api(path) {
  const response = await fetch(path);
  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.error || "Request failed.");
  }

  return data;
}

function getQueryString() {
  const params = new URLSearchParams();

  const q = elements.searchInput.value.trim();
  const region = elements.regionFilter.value;
  const tier = elements.tierFilter.value;

  if (q) params.set("q", q);
  if (region !== "All") params.set("region", region);
  if (tier !== "All") params.set("tier", tier);

  const query = params.toString();
  return query ? `?${query}` : "";
}

async function loadDashboard() {
  const [territoryData, summary] = await Promise.all([
    api(`/api/territories${getQueryString()}`),
    api("/api/routing-summary")
  ]);

  state.territories = territoryData.territories;
  state.summary = summary;

  if (!state.selectedCode && state.territories.length > 0) {
    state.selectedCode = state.territories[0].code;
  }

  if (!state.territories.some((territory) => territory.code === state.selectedCode)) {
    state.selectedCode = state.territories[0]?.code || null;
  }

  renderSummary(summary);
  renderTable(state.territories);
  renderRegions(summary.regionSummary);
  await renderRoutingDetail();
}

function renderSummary(summary) {
  elements.portfolioState.textContent = summary.portfolioState;
  elements.total.textContent = summary.totalTerritories;
  elements.enterprise.textContent = summary.enterpriseTerritories;
  elements.incidents.textContent = summary.totalActiveIncidents;
  elements.sla.textContent = `${summary.averageSlaMinutes}m`;

  elements.portfolioSummary.textContent =
    `${summary.totalTerritories} territories covered. ${summary.totalActiveIncidents} active incidents. ${summary.enterpriseTerritories} enterprise SLA territories.`;

  elements.portfolioState.className = "";
  elements.portfolioState.classList.add(`state-${summary.portfolioState.toLowerCase().replace(/\s+/g, "-")}`);
}

function renderTable(territories) {
  if (!territories.length) {
    elements.table.innerHTML = `<tr><td colspan="6" class="empty-table">No territories match the current filters.</td></tr>`;
    return;
  }

  elements.table.innerHTML = territories.map((territory) => `
    <tr class="${territory.code === state.selectedCode ? "selected-row" : ""}" data-code="${territory.code}">
      <td>
        <strong>${territory.name}</strong>
        <span>${territory.code} · ${territory.coverageStatus}</span>
      </td>
      <td>
        <strong>${territory.region}</strong>
        <span>${territory.primaryDataCenter}</span>
      </td>
      <td>
        <span class="tier-pill tier-${territory.serviceTier.toLowerCase()}">${territory.serviceTier}</span>
        <small>${territory.supportQueue}</small>
      </td>
      <td>
        <span class="sla-pill">${territory.slaMinutes}m</span>
      </td>
      <td>
        <strong>${territory.cloudRegion}</strong>
      </td>
      <td>
        <span class="incident-pill">${territory.activeIncidents}</span>
      </td>
    </tr>
  `).join("");

  document.querySelectorAll("tbody tr[data-code]").forEach((row) => {
    row.addEventListener("click", async () => {
      state.selectedCode = row.dataset.code;
      renderTable(state.territories);
      await renderRoutingDetail();
    });
  });
}

async function renderRoutingDetail() {
  if (!state.selectedCode) {
    elements.detail.className = "empty-state";
    elements.detail.textContent = "No territory selected.";
    return;
  }

  const route = await api(`/api/route/${state.selectedCode}`);

  elements.detail.className = "routing-card";
  elements.detail.innerHTML = `
    <div class="detail-top">
      <div>
        <span class="state-code">${route.stateCode}</span>
        <h3>${route.stateName}</h3>
        <p>${route.recommendation}</p>
      </div>
      <span class="tier-pill tier-${route.serviceTier.toLowerCase()}">${route.serviceTier}</span>
    </div>

    <div class="detail-grid">
      <div><span>Region</span><strong>${route.region}</strong></div>
      <div><span>SLA</span><strong>${route.slaMinutes} minutes</strong></div>
      <div><span>Cloud Region</span><strong>${route.cloudRegion}</strong></div>
      <div><span>Coverage</span><strong>${route.coverageStatus}</strong></div>
    </div>

    <div class="recommendation-box">
      <span>Support Queue</span>
      <p>${route.supportQueue}</p>
    </div>

    <div class="recommendation-box soft">
      <span>Escalation Path</span>
      <p>${route.escalationRequired ? "Escalation required" : "No escalation required"} · ${route.escalationZone}</p>
    </div>

    <div class="recommendation-box">
      <span>Primary Cloud Hub</span>
      <p>${route.primaryDataCenter}</p>
    </div>
  `;
}

function renderRegions(regions) {
  elements.regionSummary.innerHTML = regions.map((region) => `
    <article class="region-card">
      <strong>${region.region}</strong>
      <p>${region.totalStates} states · ${region.activeIncidents} active incidents</p>
      <small>${region.enterpriseStates} enterprise · ${region.premiumStates} premium · ${region.standardStates} standard</small>
    </article>
  `).join("");
}

let debounceTimer;

function debouncedLoad() {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(loadDashboard, 220);
}

elements.searchInput.addEventListener("input", debouncedLoad);
elements.regionFilter.addEventListener("change", loadDashboard);
elements.tierFilter.addEventListener("change", loadDashboard);

elements.routeCalifornia.addEventListener("click", async () => {
  elements.searchInput.value = "California";
  elements.regionFilter.value = "All";
  elements.tierFilter.value = "All";
  state.selectedCode = "CA";
  await loadDashboard();
});

elements.resetFilters.addEventListener("click", async () => {
  elements.searchInput.value = "";
  elements.regionFilter.value = "All";
  elements.tierFilter.value = "All";
  state.selectedCode = null;
  await loadDashboard();
});

loadDashboard().catch((error) => {
  console.error(error);
  elements.detail.className = "empty-state";
  elements.detail.textContent = error.message;
});
