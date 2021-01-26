import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, load } from './actions';
import { LOAD, UPDATE } from './constants';
import { CHANGED_USER } from '../App/constants';

import { command } from 'utils/request';

export function* onChangedUser() {
  try {
    const dataset = yield select(state => _.get(state, 'dataset.keys.dataset'));
    yield put(load(dataset));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onLoad(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views dataset', _.omit(action, 'type', 'clear'), user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onUpdate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    yield call(command, action.command, action.request, user);
    
    const dataset = yield select(state => _.get(state, 'dataset.keys.dataset'));
    yield put(load(dataset));
  } catch (err) {
    yield put(failed(err));
  }
}

export default function* datasetSaga() {
  yield takeLatest(CHANGED_USER, onChangedUser);
  yield takeLatest(LOAD, onLoad);
  yield takeLatest(UPDATE, onUpdate);
}
