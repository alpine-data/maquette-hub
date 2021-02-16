import { createSelector } from 'reselect';

/**
 * Direct selector to the createDataset state domain
 */

const selectCreateDatasetDomain = state => state.createDataset || {};

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
