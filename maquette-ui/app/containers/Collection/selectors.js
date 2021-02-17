import { createSelector } from 'reselect';

/**
 * Direct selector to the collection state domain
 */

const selectCollectionDomain = state => state.collection || {};

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
