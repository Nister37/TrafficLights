/**
 * Traffic Lights API - fetch functions for HTTP requests.
 * Reusable across pages.
 */
const API_BASE_URL = '/api';
async function fetchTrafficLightsForIntersection(intersectionId) {
    const response = await fetch(`${API_BASE_URL}/intersections/${intersectionId}/traffic-lights`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    });
    if (response.status === 204) return [];
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to fetch traffic lights');
    }
    return await response.json();
}
async function createTrafficLight(status, installationDate, direction, type, rightArrow, intersectionId) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights`, {
        method: 'POST',
        headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' },
        body: JSON.stringify({ status, installationDate, direction, type, rightArrow, intersectionId })
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to create traffic light');
    }
    return await response.json();
}
async function patchTrafficLight(trafficLightId, fields) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights/${trafficLightId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(fields)
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to update traffic light');
    }
    return true;
}
async function deleteTrafficLight(trafficLightId) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights/${trafficLightId}`, {
        method: 'DELETE',
        headers: { 'Accept': 'application/json' }
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Failed to delete traffic light');
    }
    return true;
}
