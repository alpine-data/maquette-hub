import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createSource state domain
 */

const selectCreateSourceDomain = state => state.createSource || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateSource
 */

const makeSelectCreateSource = () =>
  createSelector(
    selectCreateSourceDomain,
    substate => substate,
  );

export default makeSelectCreateSource;
export { selectCreateSourceDomain };
