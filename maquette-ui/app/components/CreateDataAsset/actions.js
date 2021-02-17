import constants from './constants';

export default function(container) { 
    const { FETCH, FETCH_SUCCESS, FETCH_FAILURE, CREATE, CREATE_SUCCESS, CREATE_FAILURE } = constants(container);
  
    return {
      fetch: (command, request) => {
        return {
          type: FETCH,
          command,
          request
        };
      },
  
      fetchSuccess: (response) => {
        return {
          type: FETCH_SUCCESS,
          response
        };
      },
  
      fetchFailure: (error) => {
        return {
          type: FETCH_FAILURE,
          error
        };
      },
  
      create: (command, request) => {
        return {
          type: CREATE,
          command,
          request
        }
      },
  
      createSuccess: (request, response) => {
        return {
          type: CREATE_SUCCESS,
          response,
          request
        };
      },
  
      createFailure: (error) => {
        return {
          type: CREATE_FAILURE,
          error
        }
      }
    }
}