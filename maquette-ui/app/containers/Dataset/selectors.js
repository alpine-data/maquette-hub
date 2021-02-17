import { createSelector } from 'reselect';

/**
 * Direct selector to the dataset state domain
 */

const selectDatasetDomain = state => state.dataset || {};

/**
 * Other specific selectors
 */

/**
 * Default selector used by Dataset
 */

const makeSelectDataset = () =>
  createSelector(
    selectDatasetDomain,
    substate => substate,
  );

export default makeSelectDataset;
export { selectDatasetDomain };
