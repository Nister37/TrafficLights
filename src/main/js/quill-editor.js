/**
 * quill-editor.js — rich-text editor for the maintenance log description field.
 *
 * Quill replaces the plain <textarea id="description"> with a Snow-themed editor.
 * On form submit, plain text is synced back to the hidden textarea so Spring
 * receives a regular string — no backend model changes needed.
 *
 * Loaded on: /addMaintenanceLog
 * Source: src/main/js/quill-editor.js
 */
import Quill from 'quill'
import 'quill/dist/quill.snow.css'

document.addEventListener('DOMContentLoaded', () => {
    const textarea = document.getElementById('description')
    if (!textarea) {
        return
    }

    // Hide original textarea — it stays in the DOM so its name attribute submits
    textarea.style.display = 'none'

    // Insert the Quill editor container directly before the hidden textarea
    const editorDiv = document.createElement('div')
    editorDiv.id = 'quill-editor-container'
    textarea.parentNode.insertBefore(editorDiv, textarea)

    const quill = new Quill('#quill-editor-container', {
        theme: 'snow',
        modules: {
            toolbar: [
                [ 'bold', 'italic', 'underline' ],
                [ { list: 'ordered' }, { list: 'bullet' } ],
                [ 'clean' ]
            ]
        },
        placeholder: 'Describe the maintenance work performed…'
    })

    // Pre-fill the editor when the form re-renders with a validation error
    if (textarea.value) {
        quill.setText(textarea.value)
    }

    // Sync plain text to the real textarea before the form is submitted
    const form = textarea.closest('form')
    if (form) {
        form.addEventListener(
            'submit',
            () => {
                // getText() returns plain text — keeps the backend model as String
                textarea.value = quill.getText().trim()
            },
            { capture: true }
        )
    }
})
