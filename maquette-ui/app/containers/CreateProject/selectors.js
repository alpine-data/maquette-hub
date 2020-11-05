import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the createProject state domain
 */

const selectCreateProjectDomain = state => state.createProject || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by CreateProject
 */
const makeSelectCreateProject = () =>
  createSelector(
    selectCreateProjectDomain,
    substate => substate,
  );

export default makeSelectCreateProject;
export { selectCreateProjectDomain };
