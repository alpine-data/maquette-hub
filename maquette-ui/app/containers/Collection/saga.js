import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, load } from './actions';
import { LOAD, UPDATE } from './constants';
import { CHANGE_USER } from '../App/constants';

import { command } from 'utils/request';

export function* onChangeUser() {
  try {
    const collection = yield select(state => _.get(state, 'collection.keys.collection'));
    yield put(load(collection));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onLoad(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views collection', _.omit(action, 'type', 'clear'), user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onUpdate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    yield call(command, action.command, action.request, user);
    
    const collection = yield select(state => _.get(state, 'collection.keys.collection'));
    yield put(load(collection));
  } catch (err) {
    yield put(failed(err));
  }
}
export default function* collectionSaga() {
  yield takeLatest(CHANGE_USER, onChangeUser);
  yield takeLatest(LOAD, onLoad);
  yield takeLatest(UPDATE, onUpdate);
}