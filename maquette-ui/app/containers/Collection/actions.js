/*
 *
 * Collection actions
 *
 */

import { 
  LOAD,
  UPDATE,
  FAILED,
  FETCHED } from './constants';

export function load(collection, clear = false) {
  return {
    type: LOAD,
    collection,
    clear
  };
};

export function update(command, request) {
  return {
    type: UPDATE,
    command,
    request
  }
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
  };
};

export function dismissError() {
  return {
    type: DISMISS_ERROR
  };
};