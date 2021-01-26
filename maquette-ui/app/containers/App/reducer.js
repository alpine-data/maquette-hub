/*
 *
 * App reducer
 *
 */
import produce from 'immer';
import { CHANGE_USER, CHANGED_USER } from './constants';

export const initialState = {
  loading: false,
  error: false,
  currentUser: {
    id: '',
    name: '',
    roles: []
  }
};

/* eslint-disable default-case, no-param-reassign */
const appReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CHANGE_USER:
        //draft.currentUser = users[action.id]
        break;
      case CHANGED_USER:
        draft.currentUser = action.user;
        draft.currentUser.id = action.user.username;
        break;
    }
  });

export default appReducer;
