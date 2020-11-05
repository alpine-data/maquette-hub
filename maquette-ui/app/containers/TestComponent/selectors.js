import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the testComponent state domain
 */

const selectTestComponentDomain = state => state.testComponent || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by TestComponent
 */

const makeSelectTestComponent = () =>
  createSelector(
    selectTestComponentDomain,
    substate => substate,
  );

export default makeSelectTestComponent;
export { selectTestComponentDomain };
