import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the dataset state domain
 */

const selectDatasetDomain = state => state.dataset || initialState;

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
