import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createDataAccessRequest state domain
 */

const selectCreateDataAccessRequestDomain = state =>
  state.createDataAccessRequest || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateDataAccessRequest
 */

const makeSelectCreateDataAccessRequest = () =>
  createSelector(
    selectCreateDataAccessRequestDomain,
    substate => substate,
  );

export default makeSelectCreateDataAccessRequest;
export { selectCreateDataAccessRequestDomain };
