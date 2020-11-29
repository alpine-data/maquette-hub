/*
 *
 * Dataset reducer
 *
 */
import produce from 'immer';
import { LOAD, UPDATE, FETCHED, FAILED, SELECT_VERSION, DISMISS_ERROR } from './constants';

export const initialState = {
  keys: {},
  data: false,
  version: '1.0.0',

  error: false,
  loading: false,
  updating: false
};

const datasetReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case LOAD:
        draft.keys = action;
        draft.error = false;

        if (action.dataset != state.keys.dataset) {
          draft.version = initialState.version;
        }

        if (action.clear || !draft.data) {
          draft.loading = true;
          draft.version = initialState.version;
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

      case SELECT_VERSION:
        draft.version = action.version;
        break;  
        
      case DISMISS_ERROR:
        draft.error = false;
        break;
    }
  });

export default datasetReducer;
