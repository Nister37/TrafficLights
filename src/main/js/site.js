/**
 * site.js — sitewide entry point, loaded on every page.
 * Imports SCSS (produces bundle-site.css) and bootstraps Bootstrap JS.
 */
import '../scss/site.scss'
import 'bootstrap'
import { animate, stagger } from 'animejs'

// Fade-in + slide-up animation for dashboard cards on the home page.
// Only runs when .hover-card elements are present (index.html).
document.addEventListener('DOMContentLoaded', () => {
    const cards = document.querySelectorAll('.hover-card')
    if (cards.length === 0) return

    animate(cards, {
        opacity: [0, 1],
        translateY: [20, 0],
        duration: 600,
        delay: stagger(100),
        ease: 'outQuad',
    })
})


