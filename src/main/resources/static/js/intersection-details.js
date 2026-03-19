/**
 * intersection-details.js
 * Page-specific DOM logic for the intersection details page.
 * Depends on: traffic-lights-api.js
 */
import {
    createTrafficLight,
    deleteTrafficLight,
    fetchTrafficLightsForIntersection,
    patchTrafficLight
} from './traffic-lights-api.js';

function renderTrafficLightCard(trafficLight) {
    const statusClass = getStatusClass(trafficLight.status);
    return `
        <div class="col-md-6 col-lg-4 mb-3" id="traffic-light-${trafficLight.id}">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title">ID: ${trafficLight.id}</h5>
                    <p class="card-text">
                        <strong>Status:</strong> <span class="badge ${statusClass}" id="status-badge-${trafficLight.id}">${trafficLight.status}</span><br>
                        <strong>Type:</strong> ${trafficLight.type}<br>
                        <strong>Direction:</strong> ${trafficLight.direction}<br>
                        <strong>Category:</strong> ${trafficLight.category}<br>
                        <strong>Installation Date:</strong> ${trafficLight.installationDate}
                    </p>
                    <div class="d-flex gap-2 align-items-center">
                        <select class="form-select form-select-sm w-auto" id="edit-status-${trafficLight.id}">
                            <option value="ACTIVE" ${trafficLight.status === 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="MAINTENANCE" ${trafficLight.status === 'MAINTENANCE' ? 'selected' : ''}>MAINTENANCE</option>
                            <option value="BROKEN" ${trafficLight.status === 'BROKEN' ? 'selected' : ''}>BROKEN</option>
                            <option value="PLANNED" ${trafficLight.status === 'PLANNED' ? 'selected' : ''}>PLANNED</option>
                        </select>
                        <button class="btn btn-warning btn-sm" onclick="handleUpdateStatus(${trafficLight.id})">
                            <i class="bi bi-pencil"></i> Update
                        </button>
                    </div>
                </div>
                <div class="card-footer">
                    <button class="btn btn-danger btn-sm" onclick="handleDeleteTrafficLight(${trafficLight.id})">
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </div>
            </div>
        </div>
    `;
}
function getStatusClass(status) {
    switch (status) {
        case 'ACTIVE': return 'bg-success';
        case 'BROKEN': return 'bg-danger';
        case 'MAINTENANCE': return 'bg-warning text-dark';
        case 'PLANNED': return 'bg-info';
        default: return 'bg-secondary';
    }
}
async function loadTrafficLightsForIntersection(intersectionId, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    try {
        container.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>';
        const trafficLights = await fetchTrafficLightsForIntersection(intersectionId);
        if (trafficLights.length === 0) {
            container.innerHTML = '<div class="alert alert-info">No traffic lights found for this intersection.</div>';
            return;
        }
        container.innerHTML = trafficLights.map(tl => renderTrafficLightCard(tl)).join('');
    } catch (error) {
        console.error('Error loading traffic lights:', error);
        container.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
    }
}
function refreshTrafficLights() {
    const container = document.getElementById('traffic-lights-container');
    if (container && container.dataset.intersectionId) {
        loadTrafficLightsForIntersection(parseInt(container.dataset.intersectionId), 'traffic-lights-container');
    }
}
function showAlert(message, type) {
    const alertContainer = document.getElementById('alert-container');
    if (!alertContainer) return;
    const alertId = 'alert-' + Date.now();
    alertContainer.insertAdjacentHTML('beforeend', `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `);
    setTimeout(() => { const a = document.getElementById(alertId); if (a) a.remove(); }, 5000);
}
async function handleUpdateStatus(trafficLightId) {
    const select = document.getElementById(`edit-status-${trafficLightId}`);
    if (!select) return;
    const newStatus = select.value;
    try {
        await patchTrafficLight(trafficLightId, { status: newStatus });
        const badge = document.getElementById(`status-badge-${trafficLightId}`);
        if (badge) {
            badge.className = `badge ${getStatusClass(newStatus)}`;
            badge.textContent = newStatus;
        }
        showAlert(`Traffic light #${trafficLightId} status updated to ${newStatus}`, 'success');
    } catch (error) {
        console.error('Error updating status:', error);
        showAlert(`Error: ${error.message}`, 'danger');
    }
}
async function handleAddTrafficLight(event) {
    event.preventDefault();
    const form = document.getElementById('add-traffic-light-form');
    const intersectionId = parseInt(form.dataset.intersectionId);
    const status = document.getElementById('new-status').value;
    const installationDate = document.getElementById('new-installation-date').value;
    const direction = document.getElementById('new-direction').value;
    const type = document.getElementById('new-type').value;
    const rightArrow = document.getElementById('new-right-arrow').checked;
    try {
        const created = await createTrafficLight(status, installationDate, direction, type, rightArrow, intersectionId);
        const container = document.getElementById('traffic-lights-container');
        container.insertAdjacentHTML('beforeend', renderTrafficLightCard(created));
        form.reset();
        showAlert(`Traffic light #${created.id} added successfully`, 'success');
    } catch (error) {
        console.error('Error adding traffic light:', error);
        showAlert(`Error: ${error.message}`, 'danger');
    }
}
async function handleDeleteTrafficLight(trafficLightId) {
    if (!confirm('Are you sure you want to delete this traffic light? This will also delete all related maintenance logs.')) return;
    try {
        await deleteTrafficLight(trafficLightId);
        const card = document.getElementById(`traffic-light-${trafficLightId}`);
        if (card) card.remove();
        showAlert('Traffic light deleted successfully', 'success');
    } catch (error) {
        console.error('Error deleting traffic light:', error);
        showAlert(`Error: ${error.message}`, 'danger');
    }
}
document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('traffic-lights-container');
    if (container && container.dataset.intersectionId) {
        loadTrafficLightsForIntersection(parseInt(container.dataset.intersectionId), 'traffic-lights-container');
    }
    const form = document.getElementById('add-traffic-light-form');
    if (form) {
        form.addEventListener('submit', handleAddTrafficLight);
    }
});
