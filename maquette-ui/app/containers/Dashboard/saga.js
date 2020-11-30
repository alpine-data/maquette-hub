import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, load } from './actions';
import { LOAD } from './constants';
import { CHANGE_USER } from '../App/constants';

import { command } from 'utils/request';

export function* onChangeUser() {
  try {
    yield put(load(true));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onLoad(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views dashboard', {}, user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

export default function* dashboardSaga() {
  yield takeLatest(CHANGE_USER, onChangeUser);
  yield takeLatest(LOAD, onLoad);
}
