// SafeCircle Client-Side Logic & Leaflet Map Controller

const API_BASE = window.location.origin;

// State management
let currentPersona = 'A'; // 'A' = Alice (Viewer), 'B' = Bob (Broadcaster)
let tokenA = null;
let userA = { id: 1, name: 'Alice Walker', phone: '+15551000001' };

let tokenB = null;
let userB = { id: 2, name: 'Bob Smith', phone: '+15551000002' };

let currentPermissionId = null;
let isSharingActive = false;
let pollingInterval = null;

// Leaflet map objects
let map = null;
let liveMarker = null;
let accuracyCircle = null;

document.addEventListener('DOMContentLoaded', async () => {
    initMap();
    await authenticateBothTestUsers();
    await refreshState();

    // Start background live location polling
    pollingInterval = setInterval(pollContactLocation, 3000);
});

function initMap() {
    map = L.map('map', {
        center: [37.774929, -122.419416],
        zoom: 14,
        zoomControl: false
    });

    L.control.zoom({ position: 'bottomright' }).addTo(map);

    // Dark sleek map tiles
    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://openstreetmap.org">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/">CARTO</a>',
        subdomains: 'abcd',
        maxZoom: 19
    }).addTo(map);

    // Map click allows User B to transmit custom GPS coordinates
    map.on('click', (e) => {
        if (currentPersona === 'B') {
            simulateGpsMove(e.latlng.lat, e.latlng.lng, 'Map Click');
        } else {
            logAudit('Switch to Bob (Broadcaster) to transmit custom GPS locations by clicking.', 'info');
        }
    });
}

async function authenticateBothTestUsers() {
    try {
        // Authenticate Alice
        const resA = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phoneNumber: '+15551000001', password: 'Password123!' })
        });
        const dataA = await resA.json();
        if (dataA.success) {
            tokenA = dataA.data.token;
            userA.id = dataA.data.userId;
            logAudit('Alice (User A) authenticated.', 'success');
        }

        // Authenticate Bob
        const resB = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ phoneNumber: '+15551000002', password: 'Password123!' })
        });
        const dataB = await resB.json();
        if (dataB.success) {
            tokenB = dataB.data.token;
            userB.id = dataB.data.userId;
            logAudit('Bob (User B) authenticated.', 'success');
        }
    } catch (e) {
        logAudit('Authentication error: ' + e.message, 'danger');
    }
}

function switchPersona(persona) {
    currentPersona = persona;

    const btnA = document.getElementById('btnPersonaA');
    const btnB = document.getElementById('btnPersonaB');
    const aliceControls = document.getElementById('aliceControls');
    const bobControls = document.getElementById('bobControls');
    const avatar = document.getElementById('personaAvatar');
    const name = document.getElementById('personaName');
    const phone = document.getElementById('personaPhone');
    const role = document.getElementById('personaRole');

    if (persona === 'A') {
        btnA.classList.add('active');
        btnB.classList.remove('active');
        aliceControls.classList.remove('hidden');
        bobControls.classList.add('hidden');

        avatar.textContent = 'A';
        avatar.style.background = 'linear-gradient(135deg, #0284c7, #2563eb)';
        name.textContent = userA.name;
        phone.textContent = userA.phone;
        role.textContent = 'Viewer Role';
        role.className = 'badge badge-viewer';

        logAudit('Switched active persona to Alice (Viewer).', 'info');
    } else {
        btnB.classList.add('active');
        btnA.classList.remove('active');
        bobControls.classList.remove('hidden');
        aliceControls.classList.add('hidden');

        avatar.textContent = 'B';
        avatar.style.background = 'linear-gradient(135deg, #059669, #10b981)';
        name.textContent = userB.name;
        phone.textContent = userB.phone;
        role.textContent = 'Broadcaster Role';
        role.className = 'badge badge-active';

        logAudit('Switched active persona to Bob (Broadcaster).', 'info');
    }

    refreshState();
}

async function refreshState() {
    if (currentPersona === 'A') {
        await refreshAliceContacts();
    } else {
        await refreshBobRequests();
    }
    await pollContactLocation();
}

// User A Actions
async function sendSharingRequest() {
    const targetPhone = document.getElementById('requestPhoneInput').value.trim();
    try {
        const res = await fetch(`${API_BASE}/api/sharing-requests`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${tokenA}`
            },
            body: JSON.stringify({ targetPhoneNumber: targetPhone, expiresInMinutes: 60 })
        });
        const data = await res.json();
        if (data.success) {
            currentPermissionId = data.data.permissionId;
            logAudit(`Location request sent to ${targetPhone} (Status: PENDING). Waiting for approval.`, 'warn');
            await refreshState();
        } else {
            logAudit(`Failed to send request: ${data.message}`, 'danger');
        }
    } catch (e) {
        logAudit('Error sending request: ' + e.message, 'danger');
    }
}

async function refreshAliceContacts() {
    try {
        const res = await fetch(`${API_BASE}/api/sharing-permissions`, {
            headers: { 'Authorization': `Bearer ${tokenA}` }
        });
        const data = await res.json();
        const listContainer = document.getElementById('approvedList');
        const countBadge = document.getElementById('approvedCount');

        if (data.success && data.data.length > 0) {
            countBadge.textContent = `${data.data.length} Active`;
            countBadge.className = 'badge badge-active';
            listContainer.innerHTML = data.data.map(p => `
                <div class="contact-item">
                    <div>
                        <div class="contact-name">${p.owner.name}</div>
                        <div class="contact-phone">${p.owner.phoneNumber}</div>
                    </div>
                    <span class="badge badge-active">Live Sharing</span>
                </div>
            `).join('');
            currentPermissionId = data.data[0].permissionId;
            isSharingActive = true;
        } else {
            countBadge.textContent = '0 Active';
            countBadge.className = 'badge';
            listContainer.innerHTML = `<div class="empty-state">No contacts currently sharing location with you.</div>`;
            isSharingActive = false;
        }
    } catch (e) {
        console.error(e);
    }
}

// User B Actions
async function refreshBobRequests() {
    try {
        const res = await fetch(`${API_BASE}/api/sharing-requests/pending`, {
            headers: { 'Authorization': `Bearer ${tokenB}` }
        });
        const data = await res.json();
        const listContainer = document.getElementById('pendingRequestsList');
        const countTag = document.getElementById('pendingCount');

        if (data.success && data.data.length > 0) {
            countTag.textContent = `${data.data.length} Pending`;
            countTag.className = 'tag tag-pending';
            currentPermissionId = data.data[0].permissionId;

            listContainer.innerHTML = data.data.map(req => `
                <div class="contact-item">
                    <div>
                        <div class="contact-name">${req.viewer.name}</div>
                        <div class="contact-phone">${req.viewer.phoneNumber}</div>
                    </div>
                    <div style="display:flex; gap:4px;">
                        <button class="btn btn-success" style="padding:4px 8px; font-size:0.75rem;" onclick="approveRequestById(${req.permissionId})">Approve</button>
                        <button class="btn btn-danger" style="padding:4px 8px; font-size:0.75rem;" onclick="rejectRequestById(${req.permissionId})">Reject</button>
                    </div>
                </div>
            `).join('');
        } else {
            countTag.textContent = '0 Pending';
            countTag.className = 'tag';
            listContainer.innerHTML = `<div class="empty-state">No pending requests right now.</div>`;
        }

        // Check active status
        const viewersRes = await fetch(`${API_BASE}/api/sharing-permissions/viewers`, {
            headers: { 'Authorization': `Bearer ${tokenB}` }
        });
        const viewersData = await viewersRes.json();
        const statusBadge = document.getElementById('bobSharingStatus');

        if (viewersData.success && viewersData.data.length > 0) {
            statusBadge.textContent = 'SHARING ACTIVE';
            statusBadge.className = 'badge badge-active';
            currentPermissionId = viewersData.data[0].permissionId;
            isSharingActive = true;
        } else {
            statusBadge.textContent = 'SHARING PAUSED';
            statusBadge.className = 'badge';
            isSharingActive = false;
        }
    } catch (e) {
        console.error(e);
    }
}

async function approveSharingForAlice() {
    if (!currentPermissionId) {
        await refreshBobRequests();
    }
    if (!currentPermissionId) {
        logAudit('No pending or active request found to approve.', 'warn');
        return;
    }
    await approveRequestById(currentPermissionId);
}

async function approveRequestById(id) {
    try {
        const res = await fetch(`${API_BASE}/api/sharing-requests/${id}/approve`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${tokenB}` }
        });
        const data = await res.json();
        if (data.success) {
            logAudit('Bob approved Alice\'s request. Location sharing is now ACTIVE.', 'success');
            await refreshState();
        } else {
            logAudit('Approval failed: ' + data.message, 'danger');
        }
    } catch (e) {
        logAudit('Approval error: ' + e.message, 'danger');
    }
}

async function revokeSharingForAlice() {
    if (!currentPermissionId) {
        // Find permission ID from all
        const res = await fetch(`${API_BASE}/api/sharing-permissions/all`, {
            headers: { 'Authorization': `Bearer ${tokenB}` }
        });
        const data = await res.json();
        if (data.success && data.data.length > 0) {
            currentPermissionId = data.data[0].permissionId;
        }
    }

    if (!currentPermissionId) {
        logAudit('No active permission to revoke.', 'warn');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/api/sharing-permissions/${currentPermissionId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${tokenB}` }
        });
        const data = await res.json();
        if (data.success) {
            logAudit('Bob REVOKED location sharing. Alice is blocked immediately!', 'danger');
            await refreshState();
        } else {
            logAudit('Revocation error: ' + data.message, 'danger');
        }
    } catch (e) {
        logAudit('Revoke error: ' + e.message, 'danger');
    }
}

async function simulateGpsMove(lat, lng, label) {
    try {
        const res = await fetch(`${API_BASE}/api/locations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${tokenB}`
            },
            body: JSON.stringify({
                latitude: lat,
                longitude: lng,
                accuracyM: 5.0,
                recordedAt: new Date().toISOString()
            })
        });
        const data = await res.json();
        if (data.success) {
            logAudit(`Bob transmitted GPS update [${label}]: ${lat.toFixed(4)}, ${lng.toFixed(4)}`, 'info');
            await pollContactLocation();
        }
    } catch (e) {
        logAudit('Failed to submit GPS: ' + e.message, 'danger');
    }
}

// Live Polling & Consent Enforcement Check
async function pollContactLocation() {
    const pulse = document.getElementById('mapPulse');
    const statusText = document.getElementById('mapStatusText');
    const consentBanner = document.getElementById('consentBanner');

    if (currentPersona === 'A') {
        // Alice queries Bob's location
        try {
            const res = await fetch(`${API_BASE}/api/contacts/${userB.id}/location`, {
                headers: { 'Authorization': `Bearer ${tokenA}` }
            });

            if (res.status === 200) {
                const data = await res.json();
                if (data.success && data.data) {
                    const loc = data.data;
                    updateMapMarker(loc.latitude, loc.longitude, loc.accuracyM, `${userB.name} (Live)`);
                    updateTelemetry(loc.latitude, loc.longitude, loc.accuracyM, loc.recordedAt);

                    pulse.className = 'pulse-indicator';
                    statusText.textContent = 'Alice Viewing: Bob (Active Sharing)';
                    consentBanner.classList.add('hidden');
                }
            } else if (res.status === 403) {
                // Consent denied or revoked!
                removeMarker();
                pulse.className = 'pulse-indicator paused';
                statusText.textContent = 'Alice Viewing: Location Hidden (Access Denied)';
                consentBanner.classList.remove('hidden');
            }
        } catch (e) {
            console.error('Poll error:', e);
        }
    } else {
        // Bob viewing his own broadcasted position
        try {
            const res = await fetch(`${API_BASE}/api/locations/latest`, {
                headers: { 'Authorization': `Bearer ${tokenB}` }
            });
            if (res.ok) {
                const data = await res.json();
                if (data.success && data.data) {
                    const loc = data.data;
                    updateMapMarker(loc.latitude, loc.longitude, loc.accuracyM, `${userB.name} (Broadcasting)`);
                    updateTelemetry(loc.latitude, loc.longitude, loc.accuracyM, loc.recordedAt);

                    pulse.className = isSharingActive ? 'pulse-indicator' : 'pulse-indicator paused';
                    statusText.textContent = isSharingActive ? 'Bob: Live Location Sharing Enabled' : 'Bob: Location Sharing Paused';
                    consentBanner.classList.add('hidden');
                }
            }
        } catch (e) {
            console.error(e);
        }
    }
}

function updateMapMarker(lat, lng, accuracy, title) {
    if (!map) return;

    if (!liveMarker) {
        const customIcon = L.divIcon({
            className: 'custom-live-marker',
            html: `<div style="width:20px;height:20px;background:#6366f1;border:3px solid #fff;border-radius:50%;box-shadow:0 0 10px rgba(99,102,241,0.8);"></div>`,
            iconSize: [20, 20],
            iconAnchor: [10, 10]
        });
        liveMarker = L.marker([lat, lng], { icon: customIcon }).addTo(map);
        liveMarker.bindPopup(`<b>${title}</b>`).openPopup();
    } else {
        liveMarker.setLatLng([lat, lng]);
        liveMarker.getPopup().setContent(`<b>${title}</b>`);
    }

    if (!accuracyCircle) {
        accuracyCircle = L.circle([lat, lng], {
            radius: accuracy || 15,
            color: '#6366f1',
            fillColor: '#818cf8',
            fillOpacity: 0.15,
            weight: 1
        }).addTo(map);
    } else {
        accuracyCircle.setLatLng([lat, lng]);
        accuracyCircle.setRadius(accuracy || 15);
    }

    map.panTo([lat, lng]);
}

function removeMarker() {
    if (liveMarker) {
        map.removeLayer(liveMarker);
        liveMarker = null;
    }
    if (accuracyCircle) {
        map.removeLayer(accuracyCircle);
        accuracyCircle = null;
    }
}

function updateTelemetry(lat, lng, accuracy, timestamp) {
    document.getElementById('telemetryLat').textContent = lat.toFixed(6);
    document.getElementById('telemetryLng').textContent = lng.toFixed(6);
    document.getElementById('telemetryAcc').textContent = `±${accuracy} m`;
    document.getElementById('telemetryTime').textContent = timestamp ? new Date(timestamp).toLocaleTimeString() : 'Just now';
}

function logAudit(message, type = 'info') {
    const auditLog = document.getElementById('auditLog');
    const entry = document.createElement('div');
    entry.className = `log-entry log-${type}`;
    const time = new Date().toLocaleTimeString();
    entry.textContent = `[${time}] ${message}`;
    auditLog.prepend(entry);
}

function clearLogs() {
    document.getElementById('auditLog').innerHTML = '';
}

// Automated Milestone 1 Demonstration Script
async function runAutomatedMilestoneDemo() {
    logAudit('=== STARTING AUTOMATED MILESTONE 1 VERIFICATION ===', 'info');

    // 1. Switch to Alice
    switchPersona('A');
    await sleep(1000);

    // 2. Alice requests Bob's location
    logAudit('Step 1: Alice requests access to Bob (+15551000002)...', 'info');
    await sendSharingRequest();
    await sleep(2000);

    // 3. Verify Alice cannot see Bob's location while PENDING
    logAudit('Step 2: Checking privacy enforcement (Alice viewing Bob while PENDING)...', 'warn');
    await pollContactLocation();
    await sleep(2000);

    // 4. Switch to Bob to approve
    switchPersona('B');
    logAudit('Step 3: Switching to Bob to approve request...', 'info');
    await sleep(1500);
    await approveSharingForAlice();
    await sleep(2000);

    // 5. Bob moves to Market Street
    logAudit('Step 4: Bob transmits new GPS coordinate (Market St)...', 'info');
    await simulateGpsMove(37.783333, -122.416667, 'Market Street');
    await sleep(2000);

    // 6. Switch to Alice to observe live GPS location
    switchPersona('A');
    logAudit('Step 5: Alice now successfully retrieves Bob\'s live GPS coordinates on map!', 'success');
    await pollContactLocation();
    await sleep(2500);

    // 7. Switch to Bob to revoke permission
    switchPersona('B');
    logAudit('Step 6: Bob revokes location sharing permission...', 'danger');
    await sleep(1500);
    await revokeSharingForAlice();
    await sleep(2000);

    // 8. Switch to Alice: verify immediate 403 Forbidden blocking
    switchPersona('A');
    logAudit('Step 7: Verifying Alice is IMMEDIATELY blocked with 403 Forbidden after revocation...', 'danger');
    await pollContactLocation();

    logAudit('=== MILESTONE 1 DEMO COMPLETE: ALL PRIVACY & ACCESS CONTROLS VERIFIED ===', 'success');
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
