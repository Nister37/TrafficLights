/**
 * Traffic Lights API - JavaScript AJAX functions
 * Uses Fetch API for asynchronous HTTP requests
 */

const API_BASE_URL = '/api';

/**
 * Fetch all traffic lights for a specific intersection
 * @param {number} intersectionId - The ID of the intersection
 * @returns {Promise<Array>} - Array of traffic light objects
 */
async function fetchTrafficLightsForIntersection(intersectionId) {
    const response = await fetch(`${API_BASE_URL}/intersections/${intersectionId}/traffic-lights`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    });

    if (response.status === 204) {
        return []; // No content
    }

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to fetch traffic lights');
    }

    return await response.json();
}

/**
 * Fetch a single traffic light by ID
 * @param {number} trafficLightId - The ID of the traffic light
 * @returns {Promise<Object>} - Traffic light object
 */
async function fetchTrafficLight(trafficLightId) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights/${trafficLightId}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to fetch traffic light');
    }

    return await response.json();
}

/**
 * Delete a traffic light by ID
 * @param {number} trafficLightId - The ID of the traffic light to delete
 * @returns {Promise<boolean>} - True if deleted successfully
 */
async function deleteTrafficLight(trafficLightId) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights/${trafficLightId}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json'
        }
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to delete traffic light');
    }

    return true;
}

/**
 * Load and display traffic lights for an intersection
 * @param {number} intersectionId - The ID of the intersection
 * @param {string} containerId - The ID of the HTML container element
 */
async function loadTrafficLightsForIntersection(intersectionId, containerId) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error('Container not found:', containerId);
        return;
    }

    try {
        container.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>';

        const trafficLights = await fetchTrafficLightsForIntersection(intersectionId);

        if (trafficLights.length === 0) {
            container.innerHTML = '<div class="alert alert-info">No traffic lights found for this intersection.</div>';
            return;
        }

        container.innerHTML = trafficLights.map(tl => createTrafficLightCard(tl)).join('');

    } catch (error) {
        console.error('Error loading traffic lights:', error);
        container.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
    }
}

/**
 * Create HTML card for a traffic light
 * @param {Object} trafficLight - Traffic light object
 * @returns {string} - HTML string
 */
function createTrafficLightCard(trafficLight) {
    const statusClass = getStatusClass(trafficLight.status);
    return `
        <div class="col-md-6 col-lg-4 mb-3" id="traffic-light-${trafficLight.id}">
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title">ID: ${trafficLight.id}</h5>
                    <p class="card-text">
                        <strong>Status:</strong> <span class="badge ${statusClass}">${trafficLight.status}</span><br>
                        <strong>Type:</strong> ${trafficLight.type}<br>
                        <strong>Direction:</strong> ${trafficLight.direction}<br>
                        <strong>Category:</strong> ${trafficLight.category}<br>
                        <strong>Installation Date:</strong> ${trafficLight.installationDate}
                    </p>
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

/**
 * Get Bootstrap badge class for status
 * @param {string} status - Traffic light status
 * @returns {string} - Bootstrap badge class
 */
function getStatusClass(status) {
    switch (status) {
        case 'ACTIVE': return 'bg-success';
        case 'BROKEN': return 'bg-danger';
        case 'MAINTENANCE': return 'bg-warning text-dark';
        case 'PLANNED': return 'bg-info';
        default: return 'bg-secondary';
    }
}

/**
 * Handle delete button click - deletes traffic light without page refresh
 * @param {number} trafficLightId - The ID of the traffic light to delete
 */
async function handleDeleteTrafficLight(trafficLightId) {
    if (!confirm('Are you sure you want to delete this traffic light? This will also delete all related maintenance logs.')) {
        return;
    }

    try {
        await deleteTrafficLight(trafficLightId);

        // Remove the card from DOM without page refresh
        const card = document.getElementById(`traffic-light-${trafficLightId}`);
        if (card) {
            card.remove();
        }

        // Show success message
        showAlert('Traffic light deleted successfully', 'success');

    } catch (error) {
        console.error('Error deleting traffic light:', error);
        showAlert(`Error: ${error.message}`, 'danger');
    }
}

/**
 * Show a Bootstrap alert message
 * @param {string} message - The message to display
 * @param {string} type - Alert type (success, danger, warning, info)
 */
function showAlert(message, type) {
    const alertContainer = document.getElementById('alert-container');
    if (!alertContainer) {
        console.warn('Alert container not found');
        return;
    }

    const alertId = 'alert-' + Date.now();
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    alertContainer.insertAdjacentHTML('beforeend', alertHtml);

    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        const alert = document.getElementById(alertId);
        if (alert) {
            alert.remove();
        }
    }, 5000);
}

/**
 * Refresh traffic lights - reads intersection ID from data attribute
 */
function refreshTrafficLights() {
    const container = document.getElementById('traffic-lights-container');
    if (container && container.dataset.intersectionId) {
        const intersectionId = parseInt(container.dataset.intersectionId);
        loadTrafficLightsForIntersection(intersectionId, 'traffic-lights-container');
    }
}

/**
 * Auto-initialize on page load
 * Reads intersection ID from data-intersection-id attribute
 */
document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('traffic-lights-container');
    if (container && container.dataset.intersectionId) {
        const intersectionId = parseInt(container.dataset.intersectionId);
        loadTrafficLightsForIntersection(intersectionId, 'traffic-lights-container');
    }
});



