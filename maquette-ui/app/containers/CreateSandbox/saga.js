import _ from 'lodash';
import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { 
  createSandboxSuccess,
  failed, 
  getProjectsSuccess, 
  getStacksSuccess } from './actions';

import {
  CREATE_SANDBOX, 
  INITIALIZE } from './constants';

import { command } from 'utils/request';
import { push } from 'connected-react-router';

export function* initialize() {
  const user = yield select(makeSelectCurrentUser());

  try {
    const data = yield call(command, 'sandboxes stacks', {}, user);
    yield put(getStacksSuccess(data));
  } catch (err) {
    yield put(failed('stacks', err));
  }

  try {
    const data = yield call(command, 'projects list', {}, user);
    yield put(getProjectsSuccess(data));
  } catch (err) {
    yield put(failed('projects', err));
  }
}

export function* createSandbox(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'sandboxes create', _.omit(action, 'type'), user);
    yield put(createSandboxSuccess(data));
    yield put(push(`/${action.project}/sandboxes`));
  } catch (err) {
    yield put(failed('create-sandbox', err));
  }
}

// Individual exports for testing
export default function* createSandboxSaga() {
  yield takeLatest(INITIALIZE, initialize);
  yield takeLatest(CREATE_SANDBOX, createSandbox);
}
