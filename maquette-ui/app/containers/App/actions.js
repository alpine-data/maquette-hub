/*
 *
 * App actions
 *
 */

import { CHANGE_USER, DEFAULT_ACTION } from './constants';

export function changeUser(id) {
  return {
    type: CHANGE_USER,
    id
  }
}

export function defaultAction() {
  return {
    type: DEFAULT_ACTION,
  };
}
