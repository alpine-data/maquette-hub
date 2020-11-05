import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the app state domain
 */

const selectAppDomain = state => state.app || initialState;

/**
 * Other specific selectors
 */
const makeSelectCurrentUser = () =>
  createSelector(
    selectAppDomain,
    substate => substate.currentUser
  );

const makeSelectLoading = () =>
  createSelector(
    selectAppDomain,
    substate => substate.loading
  );

const makeSelectError = () =>
  createSelector(
    selectAppDomain,
    substate => substate.error
  );


/**
 * Default selector used by App
 */
const makeSelectApp = () =>
  createSelector(
    selectAppDomain,
    substate => substate,
  );

export { makeSelectApp, makeSelectCurrentUser, makeSelectLoading, makeSelectError, selectAppDomain };
