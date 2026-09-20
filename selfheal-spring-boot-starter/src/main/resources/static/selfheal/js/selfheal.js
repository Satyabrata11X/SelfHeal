const API_BASE = "/selfheal/management";

let busy = false;
let toastTimer = null;


// =========================================================
// API
// =========================================================

async function api(path) {

    const response = await fetch(
        API_BASE + path,
        {
            headers: {
                "Accept": "application/json"
            },
            cache: "no-store"
        }
    );

    if (!response.ok) {
        throw new Error("HTTP " + response.status);
    }

    return response.json();
}


// =========================================================
// HELPERS
// =========================================================

function escapeHtml(value) {

    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


function setText(id, value) {

    const element = document.getElementById(id);

    if (element) {
        element.textContent = value;
    }
}


function showToast(message) {

    const toast = document.getElementById("toast");

    if (!toast) {
        return;
    }

    toast.textContent = message;

    toast.classList.add("show");

    clearTimeout(toastTimer);

    toastTimer = setTimeout(
        () => toast.classList.remove("show"),
        2200
    );
}


function formatDate(value) {

    if (!value) {
        return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return String(value);
    }

    return date.toLocaleString(
        [],
        {
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        }
    );
}


// =========================================================
// CONNECTION
// =========================================================

function setConnectionStatus(connected) {

    const indicators = [
        document.getElementById("connectionIndicator"),
        document.getElementById("sidebarStatusDot")
    ];

    indicators.forEach(indicator => {

        if (!indicator) {
            return;
        }

        indicator.classList.toggle(
            "online",
            connected
        );

        indicator.classList.toggle(
            "offline",
            !connected
        );
    });

    setText(
        "connectionText",
        connected
            ? "Connected"
            : "Disconnected"
    );

    setText(
        "sidebarStatusText",
        connected
            ? "Connected"
            : "Disconnected"
    );
}


// =========================================================
// SYSTEM STATUS
// =========================================================

async function loadStatus() {

    try {

        const data =
            await api("/status");

        const healthy =
            Boolean(data.healthy);

        const healthClass =
            healthy
                ? "up"
                : "down";

        setText(
            "componentName",
            data.componentName || "-"
        );

        setText(
            "healthPill",
            healthy
                ? "UP"
                : "DOWN"
        );

        setText(
            "healthMessage",
            healthy
                ? "All reported system health checks are currently passing."
                : "The system reports an unhealthy state."
        );

        setText(
            "recoveryState",
            data.recoveryState || "-"
        );

        setText(
            "cooldownState",
            data.recoveryCooldownActive
                ? "ACTIVE"
                : "INACTIVE"
        );

        setText(
            "lastResponseTimeHero",
            (data.lastResponseTime ?? 0) + " ms"
        );

        setText(
            "healthOrbText",
            healthy
                ? "✓"
                : "!"
        );

        setText(
            "healthOrbCaption",
            healthy
                ? "System is healthy"
                : "Attention required"
        );

        const healthPill =
            document.getElementById("healthPill");

        if (healthPill) {

            healthPill.className =
                "pill " + healthClass;
        }

        const healthOrb =
            document.getElementById("healthOrb");

        if (healthOrb) {

            healthOrb.className =
                "orb " + healthClass;
        }

        setConnectionStatus(true);

    } catch (error) {

        console.error(
            "Status error:",
            error
        );

        setConnectionStatus(false);

        setText(
            "healthPill",
            "OFFLINE"
        );

        setText(
            "healthOrbText",
            "!"
        );

        setText(
            "healthOrbCaption",
            "API unavailable"
        );

        setText(
            "healthMessage",
            "Unable to reach the SelfHeal management API."
        );
    }
}


// =========================================================
// METRICS
// =========================================================

async function loadMetrics() {

    try {

        const data =
            await api("/metrics");

        setText(
            "totalHealthChecks",
            (data.totalHealthChecks ?? 0)
                .toLocaleString()
        );

        setText(
            "failedHealthChecks",
            (data.failedHealthChecks ?? 0)
                .toLocaleString()
        );

        setText(
            "totalFailures",
            (data.totalFailuresDetected ?? 0)
                .toLocaleString()
        );

        setText(
            "totalRecoveryProcesses",
            (data.totalRecoveryProcesses ?? 0)
                .toLocaleString()
        );

        setText(
            "successfulRecoveries",
            (data.successfulRecoveries ?? 0)
                .toLocaleString()
        );

        setText(
            "failedRecoveries",
            (data.failedRecoveries ?? 0)
                .toLocaleString()
        );

        setText(
            "recoverySuccessRate",
            (data.recoverySuccessRate ?? 0) + "%"
        );

        setText(
            "lastResponseTime",
            (data.lastResponseTime ?? 0) + " ms"
        );

    } catch (error) {

        console.error(
            "Metrics error:",
            error
        );
    }
}


// =========================================================
// DEPENDENCIES
// =========================================================

async function loadDependencies() {

    const container =
        document.getElementById(
            "dependencies"
        );

    try {

        const data =
            await api("/dependencies");

        const dependencies =
            Object.values(data || {});

        const counts = {
            UP: 0,
            DOWN: 0,
            DEGRADED: 0,
            UNKNOWN: 0
        };

        dependencies.forEach(
            dependency => {

                const status =
                    String(
                        dependency.status ||
                        "UNKNOWN"
                    ).toUpperCase();

                if (counts[status] !== undefined) {
                    counts[status]++;
                }
            }
        );

        const summary =
            document.getElementById(
                "dependencySummary"
            );

        if (summary) {

            summary.innerHTML = `
                <span>
                    Total
                    <b>${dependencies.length}</b>
                </span>

                <span>
                    UP
                    <b>${counts.UP}</b>
                </span>

                <span>
                    DEGRADED
                    <b>${counts.DEGRADED}</b>
                </span>

                <span>
                    DOWN
                    <b>${counts.DOWN}</b>
                </span>

                <span>
                    UNKNOWN
                    <b>${counts.UNKNOWN}</b>
                </span>
            `;
        }

        if (dependencies.length === 0) {

            container.innerHTML = `
                <div class="empty">
                    No dependencies registered.
                </div>
            `;

            return;
        }

        container.innerHTML =
            dependencies
                .map(dependency => {

                    const status =
                        String(
                            dependency.status ||
                            "UNKNOWN"
                        ).toUpperCase();

                    return `
                        <article
                            class="dependency-card ${status.toLowerCase()}">

                            <div class="dep-top">

                                <span class="dep-name">
                                    ${escapeHtml(
                                        dependency.name
                                    )}
                                </span>

                                <span
                                    class="dep-status ${status}">
                                    ${escapeHtml(status)}
                                </span>

                            </div>

                            <div class="dep-type">
                                ${escapeHtml(
                                    dependency.type ||
                                    "UNKNOWN"
                                )}
                            </div>

                            <div class="dep-bottom">

                                <span
                                    class="dep-msg"
                                    title="${escapeHtml(
                                        dependency.message
                                    )}">

                                    ${escapeHtml(
                                        dependency.message ||
                                        "No health message"
                                    )}

                                </span>

                                <span class="dep-time">
                                    ${dependency.responseTime ?? 0} ms
                                </span>

                            </div>

                        </article>
                    `;
                })
                .join("");

    } catch (error) {

        console.error(
            "Dependencies error:",
            error
        );

        container.innerHTML = `
            <div class="error">
                Unable to load dependencies.
            </div>
        `;
    }
}


// =========================================================
// GRAPH ROOT
// =========================================================

function findRootNode(
    nodes,
    edges
) {

    const targets =
        new Set(
            edges.map(
                edge => edge.target
            )
        );

    const sources =
        new Set(
            edges.map(
                edge => edge.source
            )
        );

    const root =
        nodes.find(
            node =>
                sources.has(node.name) &&
                !targets.has(node.name)
        );

    return root
        ? root.name
        : nodes[0]?.name;
}


// =========================================================
// GRAPH POSITIONS
// =========================================================

function createGraphPositions(
    nodes,
    rootName,
    width,
    height
) {

    const positions =
        new Map();

    positions.set(
        rootName,
        {
            x: width / 2,
            y: height / 2
        }
    );

    const dependencies =
        nodes.filter(
            node =>
                node.name !== rootName
        );

    if (dependencies.length === 0) {
        return positions;
    }

    const radiusX =
        Math.min(
            width * 0.34,
            300
        );

    const radiusY =
        Math.min(
            height * 0.31,
            145
        );

    dependencies.forEach(
        (node, index) => {

            const angle =
                (-Math.PI / 2) +
                index *
                (
                    2 *
                    Math.PI /
                    dependencies.length
                );

            positions.set(
                node.name,
                {
                    x:
                        width / 2 +
                        Math.cos(angle) *
                        radiusX,

                    y:
                        height / 2 +
                        Math.sin(angle) *
                        radiusY
                }
            );
        }
    );

    return positions;
}


// =========================================================
// DEPENDENCY GRAPH
// =========================================================

async function loadDependencyGraph() {

    const container =
        document.getElementById(
            "dependencyGraph"
        );

    try {

        const data =
            await api(
                "/dependency-graph"
            );

        const nodes =
            data.nodes || [];

        const edges =
            data.edges || [];

        setText(
            "graphNodeCount",
            data.nodeCount ??
            nodes.length
        );

        setText(
            "graphEdgeCount",
            data.edgeCount ??
            edges.length
        );

        setText(
            "graphUpdated",
            "Updated " +
            new Date().toLocaleTimeString()
        );

        renderTopologyGraph(
            data
        );

    } catch (error) {

        console.error(
            "Dependency graph error:",
            error
        );

        container.innerHTML = `
            <div class="error">
                Unable to load dependency graph.
            </div>
        `;
    }
}


// =========================================================
// RENDER GRAPH
// =========================================================

function renderTopologyGraph(data) {

    const container =
        document.getElementById(
            "dependencyGraph"
        );

    const nodes =
        Array.isArray(data.nodes)
            ? data.nodes
            : [];

    const edges =
        Array.isArray(data.edges)
            ? data.edges
            : [];

    container.innerHTML = "";

    if (nodes.length === 0) {

        container.innerHTML = `
            <div class="empty">
                No graph nodes registered.
            </div>
        `;

        return;
    }

    const width =
        Math.max(
            container.clientWidth,
            620
        );

    const height =
        container.clientHeight ||
        440;

    const rootName =
        findRootNode(
            nodes,
            edges
        );

    const positions =
        createGraphPositions(
            nodes,
            rootName,
            width,
            height
        );


    // SVG
    const svg =
        document.createElementNS(
            "http://www.w3.org/2000/svg",
            "svg"
        );

    svg.setAttribute(
        "viewBox",
        `0 0 ${width} ${height}`
    );


    // Draw connections
    edges.forEach(
        edge => {

            const source =
                positions.get(
                    edge.source
                );

            const target =
                positions.get(
                    edge.target
                );

            if (!source || !target) {
                return;
            }

            const line =
                document.createElementNS(
                    "http://www.w3.org/2000/svg",
                    "line"
                );

            line.setAttribute(
                "x1",
                source.x
            );

            line.setAttribute(
                "y1",
                source.y
            );

            line.setAttribute(
                "x2",
                target.x
            );

            line.setAttribute(
                "y2",
                target.y
            );

            line.setAttribute(
                "class",
                "edge"
            );

            svg.appendChild(line);
        }
    );

    container.appendChild(svg);


    // Draw nodes
    nodes.forEach(
        node => {

            const position =
                positions.get(
                    node.name
                );

            if (!position) {
                return;
            }

            const status =
                String(
                    node.status ||
                    "UNKNOWN"
                ).toUpperCase();

            const element =
                document.createElement(
                    "div"
                );

            const isRoot =
                node.name === rootName;

            element.className =
                "node " +
                (isRoot ? "root" : "");

            element.style.left =
                position.x + "px";

            element.style.top =
                position.y + "px";

            element.innerHTML = `

                <div class="node-head">

                    <i
                        class="node-dot ${status}">
                    </i>

                    <span class="node-name">
                        ${escapeHtml(
                            node.name
                        )}
                    </span>

                </div>

                <div class="node-type">
                    ${escapeHtml(
                        node.type ||
                        "UNKNOWN"
                    )}
                </div>

                <div class="node-meta">

                    <span>
                        ${escapeHtml(status)}
                    </span>

                    <span>
                        ${node.responseTime ?? 0} ms
                    </span>

                </div>
            `;


            // Node click
            element.addEventListener(
                "click",
                () => {

                    document
                        .querySelectorAll(
                            ".node.selected"
                        )
                        .forEach(
                            selected =>
                                selected.classList.remove(
                                    "selected"
                                )
                        );

                    element.classList.add(
                        "selected"
                    );

                    showGraphDetails(
                        node,
                        edges
                    );
                }
            );

            container.appendChild(
                element
            );
        }
    );
}


// =========================================================
// GRAPH DETAILS
// =========================================================

function showGraphDetails(
    node,
    edges
) {

    const dependencies =
        edges
            .filter(
                edge =>
                    edge.source ===
                    node.name
            )
            .map(
                edge =>
                    edge.target
            );

    const dependents =
        edges
            .filter(
                edge =>
                    edge.target ===
                    node.name
            )
            .map(
                edge =>
                    edge.source
            );

    const details =
        document.getElementById(
            "graphDetails"
        );

    if (!details) {
        return;
    }

    details.classList.remove(
        "hidden"
    );

    details.innerHTML = `

        <strong>
            ${escapeHtml(node.name)}
        </strong>

        · ${escapeHtml(
            node.type
        )}

        · Status:
        <strong>
            ${escapeHtml(
                node.status
            )}
        </strong>

        · Response:
        <strong>
            ${node.responseTime ?? 0} ms
        </strong>

        · Dependencies:
        <strong>
            ${
                dependencies.length
                    ? dependencies
                        .map(escapeHtml)
                        .join(", ")
                    : "None"
            }
        </strong>

        · Dependents:
        <strong>
            ${
                dependents.length
                    ? dependents
                        .map(escapeHtml)
                        .join(", ")
                    : "None"
            }
        </strong>
    `;
}


// =========================================================
// CIRCUIT BREAKERS
// =========================================================

async function loadCircuitBreakers() {

    const container =
        document.getElementById(
            "circuitBreakers"
        );

    try {

        const data =
            await api(
                "/circuit-breakers"
            );

        const entries =
            Object.entries(
                data || {}
            );

        if (entries.length === 0) {

            container.innerHTML = `
                <div class="empty">
                    No circuit breakers registered.
                </div>
            `;

            return;
        }

        container.innerHTML =
            entries
                .map(
                    ([name, breaker]) => {

                        const state =
                            String(
                                breaker.state ||
                                "UNKNOWN"
                            );

                        const bad =
                            state.toLowerCase() ===
                            "open";

                        return `
                            <div class="stack-item">

                                <div>

                                    <b>
                                        ${escapeHtml(name)}
                                    </b>

                                    <small>
                                        Failures
                                        ${breaker.failureCount ?? 0}

                                        ·

                                        Half-open calls
                                        ${breaker.halfOpenCalls ?? 0}
                                    </small>

                                </div>

                                <span
                                    class="badge ${
                                        bad
                                            ? "bad"
                                            : ""
                                    }">

                                    ${escapeHtml(
                                        state
                                    )}

                                </span>

                            </div>
                        `;
                    }
                )
                .join("");

    } catch (error) {

        console.error(
            "Circuit breaker error:",
            error
        );

        container.innerHTML = `
            <div class="error">
                Unable to load circuit breakers.
            </div>
        `;
    }
}


// =========================================================
// ESCALATIONS
// =========================================================

async function loadEscalations() {

    const container =
        document.getElementById(
            "escalations"
        );

    try {

        const data =
            await api(
                "/escalations"
            );

        if (
            !Array.isArray(data) ||
            data.length === 0
        ) {

            container.innerHTML = `
                <div class="empty">
                    No recovery escalations.
                </div>
            `;

            return;
        }

        container.innerHTML =
            data
                .slice()
                .reverse()
                .map(
                    escalation => `
                        <div class="stack-item">

                            <div>

                                <b>
                                    ${escapeHtml(
                                        escalation.componentName
                                    )}
                                </b>

                                <small>

                                    ${escapeHtml(
                                        escalation.failureType
                                    )}

                                    ·

                                    ${escapeHtml(
                                        escalation.recoveryStrategy
                                    )}

                                    ·

                                    ${escalation.attempts ?? 0}
                                    attempts

                                </small>

                            </div>

                            <span class="badge bad">
                                ESCALATED
                            </span>

                        </div>
                    `
                )
                .join("");

    } catch (error) {

        console.error(
            "Escalation error:",
            error
        );

        container.innerHTML = `
            <div class="error">
                Unable to load escalations.
            </div>
        `;
    }
}


// =========================================================
// AUDIT
// =========================================================

async function loadAudit() {

    const container =
        document.getElementById(
            "audit"
        );

    try {

        const data =
            await api(
                "/audit"
            );

        if (
            !Array.isArray(data) ||
            data.length === 0
        ) {

            container.innerHTML = `
                <div class="empty">
                    No recovery audit records.
                </div>
            `;

            return;
        }

        container.innerHTML =
            data
                .slice()
                .reverse()
                .map(
                    entry => `

                        <div class="audit-row">

                            <div class="audit-event">

                                <b>
                                    ${escapeHtml(
                                        entry.componentName
                                    )}
                                </b>

                                <small>

                                    ${escapeHtml(
                                        entry.failureType
                                    )}

                                    ·

                                    ${formatDate(
                                        entry.timestamp
                                    )}

                                </small>

                            </div>

                            <div class="audit-cell">
                                ${escapeHtml(
                                    entry.strategy
                                )}
                            </div>

                            <div class="audit-cell">
                                ${entry.attempts ?? 0}
                            </div>

                            <div class="audit-cell">
                                ${entry.duration ?? 0} ms
                            </div>

                            <div
                                class="result ${
                                    entry.successful
                                        ? "success"
                                        : "failed"
                                }">

                                ${
                                    entry.successful
                                        ? "SUCCESS"
                                        : "FAILED"
                                }

                            </div>

                        </div>
                    `
                )
                .join("");

    } catch (error) {

        console.error(
            "Audit error:",
            error
        );

        container.innerHTML = `
            <div class="error">
                Unable to load audit trail.
            </div>
        `;
    }
}


// =========================================================
// LOAD DASHBOARD
// =========================================================

async function loadDashboard(
    showMessage = false
) {

    if (busy) {
        return;
    }

    busy = true;

    try {

        await Promise.allSettled([
            loadStatus(),
            loadMetrics(),
            loadDependencies(),
            loadDependencyGraph(),
            loadCircuitBreakers(),
            loadEscalations(),
            loadAudit()
        ]);

        if (showMessage) {
            showToast(
                "Dashboard refreshed"
            );
        }

    } finally {

        busy = false;
    }
}


// =========================================================
// NAVIGATION
// =========================================================

document
    .querySelectorAll("nav a")
    .forEach(
        link => {

            link.addEventListener(
                "click",
                () => {

                    document
                        .querySelectorAll(
                            "nav a"
                        )
                        .forEach(
                            item =>
                                item.classList.remove(
                                    "active"
                                )
                        );

                    link.classList.add(
                        "active"
                    );
                }
            );
        }
    );


// =========================================================
// INITIAL LOAD
// =========================================================

loadDashboard();


// =========================================================
// AUTO REFRESH
// =========================================================

setInterval(
    () => {
        loadDashboard();
    },
    5000
);


// =========================================================
// GRAPH RESPONSIVE UPDATE
// =========================================================

window.addEventListener(
    "resize",
    () => {

        clearTimeout(
            window.graphResizeTimer
        );

        window.graphResizeTimer =
            setTimeout(
                () => {
                    loadDependencyGraph();
                },
                250
            );
    }
);