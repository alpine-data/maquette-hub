import _ from 'lodash';
import { takeLatest, put } from 'redux-saga/effects';

import { changedUser } from './actions';
import { CHANGE_USER, INITIALIZE } from './constants';

import request from 'utils/request';

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

export function* onChangeUser(action) {
  try {
    yield request('/impersonate', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username: action.id }) });
    const user = yield request('/api/auth/user');
    yield put(changedUser(user));
  } catch (err) {
    yield put(changedUser(users[action.id]));
  }
}

export function* onInitialize() {
  try {
    const user = yield request('/api/auth/user');
    yield put(changedUser(user));
  } catch (err) {
    yield put(changedUser(users.alice));
  }
}
// Individual exports for testing
export default function* appSaga() {
  yield takeLatest(CHANGE_USER, onChangeUser);
  yield takeLatest(INITIALIZE, onInitialize);
}
