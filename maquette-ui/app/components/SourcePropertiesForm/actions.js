import constants from './constants';

export default function(container) { 
    const { TEST_CONNECTION, TEST_CONNECTION_SUCCESS } = constants(container);

    return {
        testConnection: (data) => {
            return {
                type: TEST_CONNECTION,
                ...data
            };
        },
        testConnectionSuccess: (response) => {
            return {
              type: TEST_CONNECTION_SUCCESS,
              response
            };
        }
    };
};