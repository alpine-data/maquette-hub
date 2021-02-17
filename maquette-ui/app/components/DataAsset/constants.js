export default function(container) {
    return {
        FETCH: `app/${container}/FETCH`,
        FETCH_SUCCESS: `app/${container}/FETCH_SUCCESS`,
        FETCH_FAILURE: `app/${container}/FETCH_FAILURE`,

        UPDATE: `app/${container}/CREATE`,
        UPDATE_SUCCESS: `app/${container}/CREATE_SUCCESS`,
        UPDATE_FAILURE: `app/${container}/CREATE_FAILURE`,

        DISMISS_ERROR: `app/${container}/DISMISS_ERROR`
    }
}