import { createSelector } from 'reselect';

/**
 * Direct selector to the createStream state domain
 */

const selectCreateStreamDomain = state => state.createStream || {};

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateStream
 */

const makeSelectCreateStream = () =>
  createSelector(
    selectCreateStreamDomain,
    substate => substate,
  );

export default makeSelectCreateStream;
export { selectCreateStreamDomain };
