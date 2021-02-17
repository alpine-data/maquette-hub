/*
 *
 * UserSettings reducer
 *
 */
import produce from 'immer';
import { LOAD, UPDATE, FAILED, FETCHED } from './constants';

export const initialState = {
  loadAction: {},
  data: false,

  error: false,
  loading: true,
  updating: false
};

/* eslint-disable default-case, no-param-reassign */
const userSettingsReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case LOAD:
        draft.loadAction = action;
        draft.error = false;

        if (action.clear || !draft.data) {
          draft.loading = true;
          draft.data = false;
        } else {
          draft.updating = true;
        }

        break;

      case UPDATE:
        draft.updating = true;
        break;

      case FAILED:
        draft.loading = false;
        draft.updating = false;
        draft.error = _.get(action, 'error.response.message') || 'Sorry, some error has occurred.';
        window.scrollTo(0, 0);
        break;

      case FETCHED:
        draft.loading = false;
        draft.updating = false;
        draft.data = action.response;
        break;
    }
  });

export default userSettingsReducer;
