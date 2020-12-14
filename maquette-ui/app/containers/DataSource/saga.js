import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, load, testConnectionSuccess } from './actions';
import { LOAD, UPDATE, TEST_CONNECTION } from './constants';
import { CHANGE_USER } from '../App/constants';

import { command } from 'utils/request';

export function* onChangeUser() {
  try {
    const source = yield select(state => _.get(state, 'dataSource.keys.source'));
    yield put(load(source));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onLoad(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views source', _.omit(action, 'type', 'clear'), user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onUpdate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    yield call(command, action.command, action.request, user);
    
    const source = yield select(state => _.get(state, 'dataSource.keys.source'));
    yield put(load(source));
  } catch (err) {
    yield put(failed(err));
  }
}

function* onTestConnection(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'sources test', _.omit(action, 'type'), user);

    yield put(testConnectionSuccess(data));
  } catch (error) {
    yield put(testConnectionSuccess({ "result": "failed", "message": "Could not connect to server to test connection." }));
  }
}

export default function* dataSourceSaga() {
  yield takeLatest(CHANGE_USER, onChangeUser);
  yield takeLatest(LOAD, onLoad);
  yield takeLatest(UPDATE, onUpdate);
  yield takeLatest(TEST_CONNECTION, onTestConnection);
}
