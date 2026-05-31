export function getCsrfConfig() {
    const tokenMeta = document.querySelector('meta[name="_csrf"]')
    const headerMeta = document.querySelector('meta[name="_csrf_header"]')

    if (!tokenMeta || !headerMeta) {
        return null
    }

    return {
        token: tokenMeta.getAttribute('content'),
        headerName: headerMeta.getAttribute('content')
    }
}

export function withCsrf(headers = {}) {
    const csrf = getCsrfConfig()
    if (!csrf || !csrf.token || !csrf.headerName) {
        return headers
    }

    return {
        ...headers,
        [csrf.headerName]: csrf.token
    }
}
