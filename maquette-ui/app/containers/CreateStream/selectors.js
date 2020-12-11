import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createStream state domain
 */

const selectCreateStreamDomain = state => state.createStream || initialState;

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
