import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createCollection state domain
 */

const selectCreateCollectionDomain = state =>
  state.createCollection || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateCollection
 */

const makeSelectCreateCollection = () =>
  createSelector(
    selectCreateCollectionDomain,
    substate => substate,
  );

export default makeSelectCreateCollection;
export { selectCreateCollectionDomain };
