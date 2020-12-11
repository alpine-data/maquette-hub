/*
 *
 * CreateStream actions
 *
 */

import { 
  CREATE, CREATE_SUCCESS, CREATE_FAILED } from './constants';

  export function create(data) {
    return {
      type: CREATE,
      ...data
    }
  }
  
  export function createFailed(error) {
    return {
      type: CREATE_FAILED,
      error
    }
  }
  
  export function createSuccess(response) {
    return {
      type: CREATE_SUCCESS,
      response
    }
  }
