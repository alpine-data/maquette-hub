import constants from './constants';

export default function(container) { 
    const { FETCH, FETCH_SUCCESS, FETCH_FAILURE, UPDATE, UPDATE_FAILURE, UPDATE_SUCCESS, DISMISS_ERROR } = constants(container);
  
    return {
      fetch: (command, request, clear) => {
        return {
          type: FETCH,
          command,
          request,
          clear
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
  
      update: (command, request) => {
        return {
          type: UPDATE,
          command,
          request
        };
      },

      updateSuccess: (response) => {
        return {
          type: UPDATE_SUCCESS,
          response
        };
      },

      updateFailed: (error) => {
        return {
          type: UPDATE_FAILURE,
          error
        };
      },

      dismissError: () => {
        return {
          type: DISMISS_ERROR
        };
      }
    }
}