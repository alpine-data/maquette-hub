/*
 *
 * App reducer
 *
 */
import produce, { isDraft } from 'immer';
import { CHANGE_USER, DEFAULT_ACTION } from './constants';

const users = {
  alice: {
    id: 'alice',
    name: 'Alice',
    roles: [ 'foo', 'bob' ]
  },
  bob: {
    id: 'bob',
    name: 'Bob',
    roles: [ 'foo', 'bob' ]
  },
  clair: {
    id: 'clair',
    name: 'Clair',
    roles: [ 'foo', 'bar' ]
  }
}

export const initialState = {
  loading: false,
  error: false,
  currentUser: users.alice
};

/* eslint-disable default-case, no-param-reassign */
const appReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case CHANGE_USER:
        draft.currentUser = users[action.id]
        break;
      case DEFAULT_ACTION:
        break;
    }
  });

export default appReducer;
