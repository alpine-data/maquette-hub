/*
 *
 * TestComponent actions
 *
 */

import { CLICK_ACTION, DEFAULT_ACTION } from './constants';

export function defaultAction() {
  return {
    type: DEFAULT_ACTION,
  };
}

export function clickAction(foo) {
  return {
    type: CLICK_ACTION,
    foo
  };
}
