/*
 *
 * Dashboard actions
 *
 */

import { 
  LOAD,
  FAILED,
  FETCHED } from './constants';

export function load(clear = false) {
  return {
    type: LOAD,
    clear
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
  };
};
