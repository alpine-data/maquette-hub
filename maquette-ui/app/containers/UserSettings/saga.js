import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, load } from './actions';
import { LOAD, UPDATE } from './constants';
import { CHANGED_USER } from '../App/constants';

import { command } from 'utils/request';

export function* onChangedUser() {
  try {
    const action = yield select(state => _.get(state, 'userSettings.loadAction'));
    yield put(action);
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onLoad() {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views user settings', { userId: user.id }, user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onUpdate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    yield call(command, 'users update', action.request, user);
    
    const loadAction = yield select(state => _.get(state, 'userSettings.loadAction'));
    yield put(loadAction);
  } catch (err) {
    yield put(failed(err));
  }
}

// Individual exports for testing
export default function* userSettingsSaga() {
  yield takeLatest(CHANGED_USER, onChangedUser);
  yield takeLatest(LOAD, onLoad);
  yield takeLatest(UPDATE, onUpdate);
}
