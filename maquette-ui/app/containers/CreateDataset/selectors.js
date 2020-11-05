import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createDataset state domain
 */

const selectCreateDatasetDomain = state => state.createDataset || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateDataset
 */

const makeSelectCreateDataset = () =>
  createSelector(
    selectCreateDatasetDomain,
    substate => substate,
  );

export default makeSelectCreateDataset;
export { selectCreateDatasetDomain };
