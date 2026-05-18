/**
 * Bootstrap form validation — activates validation styles on submit.
 * Works for any form on the page that has the HTML5 constraint API.
 *
 * flatpickr replaces native <input type="date"> with a polished calendar picker.
 *
 * Extended with validator.js for add-intersection (/addIntersection):
 * validates latitude (-90..90) and longitude (-180..180) before submit.
 */
import validator from 'validator'
import flatpickr from 'flatpickr'

document.addEventListener('DOMContentLoaded', function () {
    'use strict'

    // --- flatpickr: replace every date input with a calendar picker --------
    document.querySelectorAll('input[type="date"]').forEach(input => {
        flatpickr(input, {
            // Y-m-d matches the Java LocalDate pattern Spring expects
            dateFormat: 'Y-m-d',
            // Allow typing a date directly — accessibility
            allowInput: true,
        })
    })

    // --- Bootstrap HTML5 validation + validator.js coordinate check --------
    const forms = document.querySelectorAll('form')
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener(
            'submit',
            function (event) {
                // Custom coordinate validation for the add-intersection form
                if (form.id === 'add-intersection-form') {
                    const latInput = document.getElementById('latitude')
                    const lngInput = document.getElementById('longitude')

                    if (latInput && !validator.isFloat(latInput.value, { min: -90, max: 90 })) {
                        event.preventDefault()
                        event.stopPropagation()
                        showFieldError(latInput, 'Latitude must be between -90 and 90.')
                    } else if (latInput) {
                        clearFieldError(latInput)
                    }

                    if (lngInput && !validator.isFloat(lngInput.value, { min: -180, max: 180 })) {
                        event.preventDefault()
                        event.stopPropagation()
                        showFieldError(lngInput, 'Longitude must be between -180 and 180.')
                    } else if (lngInput) {
                        clearFieldError(lngInput)
                    }
                }

                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            },
            false
        )
    })
})

function showFieldError(input, message) {
    input.classList.add('is-invalid')
    let feedback = input.nextElementSibling
    if (!feedback || !feedback.classList.contains('invalid-feedback')) {
        feedback = document.createElement('div')
        feedback.className = 'invalid-feedback'
        input.after(feedback)
    }
    feedback.textContent = message
}

function clearFieldError(input) {
    input.classList.remove('is-invalid')
}




