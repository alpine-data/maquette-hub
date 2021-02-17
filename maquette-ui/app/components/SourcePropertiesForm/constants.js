import _ from 'lodash';

export default function(container) {
    return {
        TEST_CONNECTION: `app/${_.upperFirst(container)}/TEST_CONNECTION`,
        TEST_CONNECTION_SUCCESS: `app/${_.upperFirst(container)}/TEST_CONNECTION_SUCCESS`
    }
}