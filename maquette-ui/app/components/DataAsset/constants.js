import _ from 'lodash';

export default function(container) {
    return {
        FETCH: `app/${_.upperFirst(container)}/FETCH`,
        FETCH_SUCCESS: `app/${_.upperFirst(container)}/FETCH_SUCCESS`,
        FETCH_FAILURE: `app/${_.upperFirst(container)}/FETCH_FAILURE`,

        UPDATE: `app/${_.upperFirst(container)}/UPDATE`,
        UPDATE_SUCCESS: `app/${_.upperFirst(container)}/UPDATE_SUCCESS`,
        UPDATE_FAILURE: `app/${_.upperFirst(container)}/UPDATE_FAILURE`,

        DISMISS_ERROR: `app/${_.upperFirst(container)}/DISMISS_ERROR`
    }
}