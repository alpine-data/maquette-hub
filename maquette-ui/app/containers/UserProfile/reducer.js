/*
 *
 * UserProfile reducer
 *
 */
import produce, { isDraft } from 'immer';
import { LOAD, FAILED, FETCHED } from './constants';

export const initialState = {
  loadAction: false,
  data: false,

  error: false,
  loading: true
};

/* eslint-disable default-case, no-param-reassign */
const userProfileReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case LOAD:
        draft.loadAction = action;
        draft.error = false;

        if (action.clear || !draft.data) {
          draft.loading = true;
          draft.data = false;
        }

        break;

      case FAILED:
        draft.loading = false;
        draft.updating = false;
        draft.error = _.get(action, 'error.response.message') || 'Sorry, some error has occurred.';
        window.scrollTo(0, 0);
        break;

      case FETCHED:
        draft.loading = false;
        draft.data = action.response;
        break;
    }
  });

export default userProfileReducer;
