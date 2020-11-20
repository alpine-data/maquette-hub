import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the sandbox state domain
 */

const selectSandboxDomain = state => state.sandbox || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by Sandbox
 */

const makeSelectSandbox = () =>
  createSelector(
    selectSandboxDomain,
    substate => substate,
  );

export default makeSelectSandbox;
export { selectSandboxDomain };
