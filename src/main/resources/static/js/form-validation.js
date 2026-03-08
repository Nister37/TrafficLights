/**
 * Bootstrap form validation — activates validation styles on submit.
 * Works for any form on the page, with or without the .needs-validation class.
 */
(function () {
    'use strict'
    document.addEventListener('DOMContentLoaded', function () {
        var forms = document.querySelectorAll('form')
        Array.prototype.slice.call(forms).forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault()
                    event.stopPropagation()
                }
                form.classList.add('was-validated')
            }, false)
        })
    })
})()
