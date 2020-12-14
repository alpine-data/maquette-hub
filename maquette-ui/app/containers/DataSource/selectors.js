import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the dataSource state domain
 */

const selectDataSourceDomain = state => state.dataSource || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by DataSource
 */

const makeSelectDataSource = () =>
  createSelector(
    selectDataSourceDomain,
    substate => substate,
  );

export default makeSelectDataSource;
export { selectDataSourceDomain };
