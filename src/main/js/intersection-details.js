/**
 * intersection-details.js
 * Page-specific DOM logic for the intersection details page.
 * Depends on: modules/traffic-lights-api.js
 */
import dayjs from 'dayjs'
import {
    createTrafficLight,
    deleteTrafficLight,
    fetchTrafficLightsForIntersection,
    patchTrafficLight
} from './modules/traffic-lights-api.js'

function canModifyTrafficLight(trafficLight, container) {
    return container.dataset.isAdmin === 'true'
        || (container.dataset.currentUsername && container.dataset.currentUsername === trafficLight.ownerUsername)
}

function renderTrafficLightCard(trafficLight, canModify) {
    const statusClass = getStatusClass(trafficLight.status)
    const updateControls = canModify
        ? `
                    <div class="d-flex gap-2 align-items-center">
                        <select class="form-select form-select-sm w-auto" id="edit-status-${trafficLight.id}">
                            <option value="ACTIVE" ${trafficLight.status === 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="MAINTENANCE" ${
            trafficLight.status === 'MAINTENANCE' ? 'selected' : ''
        }>MAINTENANCE</option>
                            <option value="BROKEN" ${trafficLight.status === 'BROKEN' ? 'selected' : ''}>BROKEN</option>
                            <option value="PLANNED" ${
            trafficLight.status === 'PLANNED' ? 'selected' : ''
        }>PLANNED</option>
                        </select>
                        <button type="button" class="btn btn-warning btn-sm"
                                data-traffic-light-action="update-status"
                                data-traffic-light-id="${trafficLight.id}">
                            <i class="bi bi-pencil"></i> Update
                        </button>
                    </div>
        `
        : ''
    const deleteControls = canModify
        ? `
                <div class="card-footer">
                    <button type="button" class="btn btn-danger btn-sm"
                            data-traffic-light-action="delete"
                            data-traffic-light-id="${trafficLight.id}">
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </div>
        `
        : ''

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
                        <strong>Installation Date:</strong> ${
        dayjs(trafficLight.installationDate).format('DD MMM YYYY')
    }
                    </p>
                    ${updateControls}
                </div>
                ${deleteControls}
            </div>
        </div>
    `
}

function getStatusClass(status) {
    switch (status) {
        case 'ACTIVE':
            return 'bg-success'
        case 'BROKEN':
            return 'bg-danger'
        case 'MAINTENANCE':
            return 'bg-warning text-dark'
        case 'PLANNED':
            return 'bg-info'
        default:
            return 'bg-secondary'
    }
}

async function loadTrafficLightsForIntersection(intersectionId, containerId) {
    const container = document.getElementById(containerId)
    if (!container) {
        return
    }
    try {
        container.innerHTML =
            '<div class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>'
        const trafficLights = await fetchTrafficLightsForIntersection(intersectionId)
        if (trafficLights.length === 0) {
            container.innerHTML = '<div class="alert alert-info">No traffic lights found for this intersection.</div>'
            return
        }
        container.innerHTML = trafficLights
            .map(tl => renderTrafficLightCard(tl, canModifyTrafficLight(tl, container)))
            .join('')
    } catch (error) {
        console.error('Error loading traffic lights:', error)
        container.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`
    }
}

function refreshTrafficLights() {
    const container = document.getElementById('traffic-lights-container')
    if (container && container.dataset.intersectionId) {
        loadTrafficLightsForIntersection(
            Number.parseInt(container.dataset.intersectionId, 10),
            'traffic-lights-container'
        )
    }
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('alert-container')
    if (!alertContainer) {
        return
    }
    const alertId = 'alert-' + Date.now()
    alertContainer.insertAdjacentHTML(
        'beforeend',
        `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `
    )
    setTimeout(() => {
        const a = document.getElementById(alertId)
        if (a) {
            a.remove()
        }
    }, 5000)
}

async function handleUpdateStatus(trafficLightId) {
    const select = document.getElementById(`edit-status-${trafficLightId}`)
    if (!select) {
        return
    }
    const newStatus = select.value
    try {
        await patchTrafficLight(trafficLightId, { status: newStatus })
        const badge = document.getElementById(`status-badge-${trafficLightId}`)
        if (badge) {
            badge.className = `badge ${getStatusClass(newStatus)}`
            badge.textContent = newStatus
        }
        showAlert(`Traffic light #${trafficLightId} status updated to ${newStatus}`, 'success')
    } catch (error) {
        console.error('Error updating status:', error)
        showAlert(`Error: ${error.message}`, 'danger')
    }
}

async function handleAddTrafficLight(event) {
    event.preventDefault()
    const form = document.getElementById('add-traffic-light-form')
    const intersectionId = parseInt(form.dataset.intersectionId)
    const status = document.getElementById('new-status').value
    const installationDate = document.getElementById('new-installation-date').value
    const direction = document.getElementById('new-direction').value
    const type = document.getElementById('new-type').value
    const rightArrow = document.getElementById('new-right-arrow').checked
    try {
        const created = await createTrafficLight(status, installationDate, direction, type, rightArrow, intersectionId)
        const container = document.getElementById('traffic-lights-container')
        // The service assigns the authenticated creator as owner.
        container.insertAdjacentHTML(
            'beforeend',
            renderTrafficLightCard(created, true)
        )
        form.reset()
        showAlert(`Traffic light #${created.id} added successfully`, 'success')
    } catch (error) {
        console.error('Error adding traffic light:', error)
        showAlert(`Error: ${error.message}`, 'danger')
    }
}

async function handleDeleteTrafficLight(trafficLightId) {
    if (
        !confirm(
            'Are you sure you want to delete this traffic light? This will also delete all related maintenance logs.'
        )
    ) {
        return
    }
    try {
        await deleteTrafficLight(trafficLightId)
        const card = document.getElementById(`traffic-light-${trafficLightId}`)
        if (card) {
            card.remove()
        }
        showAlert('Traffic light deleted successfully', 'success')
    } catch (error) {
        console.error('Error deleting traffic light:', error)
        showAlert(`Error: ${error.message}`, 'danger')
    }
}

function handleTrafficLightAction(event) {
    if (!(event.target instanceof Element)) {
        return
    }

    const button = event.target.closest('button[data-traffic-light-action]')
    if (!button) {
        return
    }

    const trafficLightId = Number.parseInt(button.dataset.trafficLightId, 10)
    if (!Number.isInteger(trafficLightId)) {
        return
    }

    if (button.dataset.trafficLightAction === 'update-status') {
        handleUpdateStatus(trafficLightId)
    } else if (button.dataset.trafficLightAction === 'delete') {
        handleDeleteTrafficLight(trafficLightId)
    }
}

function initializeIntersectionDetails() {
    const container = document.getElementById('traffic-lights-container')
    if (container && container.dataset.intersectionId) {
        container.addEventListener('click', handleTrafficLightAction)
        loadTrafficLightsForIntersection(
            Number.parseInt(container.dataset.intersectionId, 10),
            'traffic-lights-container'
        )
    }

    const form = document.getElementById('add-traffic-light-form')
    if (form) {
        form.addEventListener('submit', handleAddTrafficLight)
    }

    document.getElementById('refresh-traffic-lights')?.addEventListener('click', refreshTrafficLights)
}

initializeIntersectionDetails()
