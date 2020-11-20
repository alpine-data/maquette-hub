import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { getProjectSuccess, getSandboxSuccess, getStacksSuccess } from './actions';

import {
  INITIALIZE
} from './constants'

import { command } from 'utils/request';
import { push } from 'connected-react-router';

export function* initialize(action) {
  const user = yield select(makeSelectCurrentUser());

  try {
    const data = yield call(command, 'sandboxes get', _.omit(action, 'type'), user);
    yield put(getSandboxSuccess(data));
  } catch (err) {
    yield put(failed('sandbox', err));
  }

  try {
    const data = yield call(command, 'projects get', { name: action.project }, user);
    yield put(getProjectSuccess(data));
  } catch (err) {
    yield put(failed('project', err));
  }

  try {
    const data = yield call(command, 'sandboxes stacks', {}, user);
    yield put(getStacksSuccess(data));
  } catch (err) {
    yield put(failed('stacks', err));
  }
}

// Individual exports for testing
export default function* sandboxSaga() {
  yield takeLatest(INITIALIZE, initialize);
}
