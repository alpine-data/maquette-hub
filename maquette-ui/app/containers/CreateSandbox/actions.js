/*
 *
 * CreateSandbox actions
 *
 */

import { 
  INIT, FAILED, FETCHED,
  SUBMIT, SUBMIT_FAILED, SUBMIT_SUCCESS } from './constants';

export function init() {
  return {
    type: INIT
  };
};

export function failed(error) {
  return {
    type: FAILED,
    error
  };
};

export function fetched(response) {
  return {
    type: FETCHED,
    response
  }
};

export function submit(request) {
  return {
      type: SUBMIT,
      request
  };
};

export function submit_failed(error) {
  return {
    type: SUBMIT_FAILED,
    error
  };
};

export function submit_success(response) {
  return {
    type: SUBMIT_SUCCESS,
    response
  };
};