/**
 * Traffic Lights API — fetch functions for HTTP requests.
 * Reusable across pages.
 */
import { csrfHeaderName, csrfToken } from './csrf.js'

const API_BASE_URL = '/api'

export async function fetchTrafficLightsForIntersection(intersectionId) {
    const response = await fetch(`${API_BASE_URL}/intersections/${intersectionId}/traffic-lights`, {
        method: 'GET',
        headers: { Accept: 'application/json' }
    })
    if (response.status === 204) {
        return []
    }
    if (!response.ok) {
        const error = await response.json()
        throw new Error(error.message || 'Failed to fetch traffic lights')
    }
    return await response.json()
}

export async function createTrafficLight(status, installationDate, direction, type, rightArrow, intersectionId) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            [csrfHeaderName]: csrfToken
        },
        body: JSON.stringify({ status, installationDate, direction, type, rightArrow, intersectionId })
    })
    if (!response.ok) {
        const error = await response.json()
        throw new Error(error.message || 'Failed to create traffic light')
    }
    return await response.json()
}

export async function patchTrafficLight(trafficLightId, fields) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights/${trafficLightId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeaderName]: csrfToken
        },
        body: JSON.stringify(fields)
    })
    if (!response.ok) {
        const error = await response.json()
        throw new Error(error.message || 'Failed to update traffic light')
    }
    return true
}

export async function deleteTrafficLight(trafficLightId) {
    const response = await fetch(`${API_BASE_URL}/traffic-lights/${trafficLightId}`, {
        method: 'DELETE',
        headers: {
            Accept: 'application/json',
            [csrfHeaderName]: csrfToken
        }
    })
    if (!response.ok) {
        const error = await response.json()
        throw new Error(error.message || 'Failed to delete traffic light')
    }
    return true
}
