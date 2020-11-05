import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the project state domain
 */

const selectProjectDomain = state => state.project || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by Project
 */

const makeSelectProject = () =>
  createSelector(
    selectProjectDomain,
    substate => substate,
  );

export default makeSelectProject;
export { selectProjectDomain };
