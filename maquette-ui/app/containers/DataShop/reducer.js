/*
 *
 * DataShop reducer
 *
 */
import produce from 'immer';
import { LOAD, FAILED, FETCHED } from './constants';

export const initialState = {
  data: false,

  error: false,
  loading: false
};

/* eslint-disable default-case, no-param-reassign */
const dataShopReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case LOAD:
        draft.error = false;

        if (action.clear || !draft.data) {
          draft.loading = true;
          draft.data = false;
        }

        break;

      case FAILED:
        draft.loading = false;
        draft.error = _.get(action, 'error.response.message') || 'Sorry, some error has occurred.';
        window.scrollTo(0, 0);
        break;

      case FETCHED:
        draft.loading = false;
        draft.data = action.response;
        break;
    }
  });

export default dataShopReducer;
