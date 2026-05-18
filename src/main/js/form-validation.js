/**
 * Bootstrap form validation — activates validation styles on submit.
 * Works for any form on the page that has the HTML5 constraint API.
 */
document.addEventListener('DOMContentLoaded', function () {
    'use strict'
    const forms = document.querySelectorAll('form')
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener(
            'submit',
            function (event) {
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

