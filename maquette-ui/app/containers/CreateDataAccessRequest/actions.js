/*
 *
 * CreateDataAccessRequest actions
 *
 */

import { 
  INIT, FAILED, FETCHED,
  SUBMIT, SUBMIT_FAILED, SUBMIT_SUCCESS } from './constants';

export function init(asset, assetType) {
  return {
    type: INIT,
    asset,
    assetType
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

export function submit(request, assetType) {
  return {
      type: SUBMIT,
      request,
      assetType
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
