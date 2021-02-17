export default function(container) {
    return {
        FETCH: `app/${container}/FETCH`,
        FETCH_SUCCESS: `app/${container}/FETCH_SUCCESS`,
        FETCH_FAILURE: `app/${container}/FETCH_FAILURE`,

        CREATE: `app/${container}/CREATE`,
        CREATE_SUCCESS: `app/${container}/CREATE_SUCCESS`,
        CREATE_FAILURE: `app/${container}/CREATE_FAILURE`,
    }
}