import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, load } from './actions';
import { LOAD } from './constants';
import { CHANGED_USER } from '../App/constants';

import { command } from 'utils/request';

export function* onChangedUser() {
  try {
    const loadAction = yield select(state => _.get(state, 'userProfile.loadAction'));
    if (loadAction) {
      yield put(loadAction);
    }
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onLoad(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views user', _.omit(action, 'type', 'clear'), user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

// Individual exports for testing
export default function* projectSaga() {
  yield takeLatest(CHANGED_USER, onChangedUser);
  yield takeLatest(LOAD, onLoad);
}
