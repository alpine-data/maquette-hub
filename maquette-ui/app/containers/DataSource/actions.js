/*
 *
 * DataSource actions
 *
 */

import { 
  LOAD,
  UPDATE,
  FAILED,
  FETCHED,
  TEST_CONNECTION,
  TEST_CONNECTION_SUCCESS } from './constants';

export function load(source, clear = false) {
  return {
    type: LOAD,
    source,
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

export function testConnection(data) {
  return {
    type: TEST_CONNECTION,
    ...data
  };
}

export function testConnectionSuccess(response) {
  return {
    type: TEST_CONNECTION_SUCCESS,
    response
  }
}
