/*
 *
 * UserProfile actions
 *
 */

import { LOAD, FAILED, FETCHED } from './constants';

export function load(userId, clear = false) {
  return {
    type: LOAD,
    userId,
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
  }
}
