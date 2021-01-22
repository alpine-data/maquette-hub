/*
 *
 * App actions
 *
 */

import { CHANGED_USER, CHANGE_USER, INITIALIZE } from './constants';

export function changeUser(id) {
  return {
    type: CHANGE_USER,
    id
  };
}

export function changedUser(user) {
  return {
    type: CHANGED_USER,
    user
  };
}

export function initialize() {
  return {
    type: INITIALIZE
  };
}
