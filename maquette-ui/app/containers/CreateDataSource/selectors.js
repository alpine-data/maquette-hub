import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createDataSource state domain
 */

const selectCreateDataSourceDomain = state =>
  state.createDataSource || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateDataSource
 */

const makeSelectCreateDataSource = () =>
  createSelector(
    selectCreateDataSourceDomain,
    substate => substate,
  );

export default makeSelectCreateDataSource;
export { selectCreateDataSourceDomain };
