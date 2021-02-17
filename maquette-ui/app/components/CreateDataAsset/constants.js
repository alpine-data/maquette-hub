import _ from 'lodash';

export default function(container) {
    return {
        FETCH: `app/${_.upperFirst(container)}/FETCH`,
        FETCH_SUCCESS: `app/${_.upperFirst(container)}/FETCH_SUCCESS`,
        FETCH_FAILURE: `app/${_.upperFirst(container)}/FETCH_FAILURE`,

        CREATE: `app/${_.upperFirst(container)}/CREATE`,
        CREATE_SUCCESS: `app/${_.upperFirst(container)}/CREATE_SUCCESS`,
        CREATE_FAILURE: `app/${_.upperFirst(container)}/CREATE_FAILURE`,
    }
}