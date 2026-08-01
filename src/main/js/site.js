/**
 * site.js — sitewide entry point, loaded on every page.
 * Imports SCSS (produces bundle-site.css) and bootstraps Bootstrap JS.
 */
import '../scss/site.scss'
import 'bootstrap'
import { animate, stagger } from 'animejs'

function initializeHomeCardAnimation() {
    const cards = document.querySelectorAll('.hover-card')
    if (cards.length === 0) {
        return
    }

    animate(cards, {
        opacity: [ 0, 1 ],
        translateY: [ 20, 0 ],
        duration: 600,
        delay: stagger(100),
        ease: 'outQuad'
    })
}

function initializeDeleteConfirmations() {
    document.querySelectorAll('form[data-confirm-message]').forEach(form => {
        form.addEventListener('submit', event => {
            const message = form.dataset.confirmMessage
            if (message && !confirm(message)) {
                event.preventDefault()
            }
        })
    })
}

function initializeBrowserActions() {
    document.querySelector('[data-history-back]')?.addEventListener('click', () => {
        history.back()
    })

    document.querySelector('[data-reload-page]')?.addEventListener('click', () => {
        location.reload()
    })
}

initializeHomeCardAnimation()
initializeDeleteConfirmations()
initializeBrowserActions()
