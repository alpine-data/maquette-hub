import { createSelector } from 'reselect';
import { initialState } from './reducer';

/**
 * Direct selector to the dataShop state domain
 */

const selectDataShopDomain = state => state.dataShop || initialState;

/**
 * Other specific selectors
 */

/**
 * Default selector used by DataShop
 */

const makeSelectDataShop = () =>
  createSelector(
    selectDataShopDomain,
    substate => substate,
  );

export default makeSelectDataShop;
export { selectDataShopDomain };
