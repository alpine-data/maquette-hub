import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the datasetDetails state domain
 */

const selectDatasetDetailsDomain = state =>
  state.datasetDetails || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by DatasetDetails
 */

const makeSelectDatasetDetails = () =>
  createSelector(
    selectDatasetDetailsDomain,
    substate => substate,
  );

export default makeSelectDatasetDetails;
export { selectDatasetDetailsDomain };
