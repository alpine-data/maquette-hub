import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the collection state domain
 */

const selectCollectionDomain = state => state.collection || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by Collection
 */

const makeSelectCollection = () =>
  createSelector(
    selectCollectionDomain,
    substate => substate,
  );

export default makeSelectCollection;
export { selectCollectionDomain };
