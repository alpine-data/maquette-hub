import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the stream state domain
 */

const selectStreamDomain = state => state.stream || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by Stream
 */

const makeSelectStream = () =>
  createSelector(
    selectStreamDomain,
    substate => substate,
  );

export default makeSelectStream;
export { selectStreamDomain };
