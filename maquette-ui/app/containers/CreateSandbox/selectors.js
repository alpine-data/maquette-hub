import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createSandbox state domain
 */

const selectCreateSandboxDomain = state => state.createSandbox || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateSandbox
 */

const makeSelectCreateSandbox = () =>
  createSelector(
    selectCreateSandboxDomain,
    substate => substate,
  );

export default makeSelectCreateSandbox;
export { selectCreateSandboxDomain };
