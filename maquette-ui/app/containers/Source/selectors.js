import { createSelector } from 'reselect';

/**
 * Direct selector to the source state domain
 */

const selectSourceDomain = state => state.source || {};

/**
 * Other specific selectors
 */

/**
 * Default selector used by Source
 */

const makeSelectSource = () =>
  createSelector(
    selectSourceDomain,
    substate => substate,
  );

export default makeSelectSource;
export { selectSourceDomain };
