import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';
import { CREATE, TEST_CONNECTION } from './constants';
import { createFailed, createSuccess, testConnectionSuccess } from './actions'

import { makeSelectCurrentUser } from '../App/selectors';
import { push } from 'connected-react-router';

import { command } from 'utils/request';


function* onCreate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'sources create', _.omit(action, 'type'), user);

    yield put(createSuccess(data));
    yield put(push(`/shop/sources/${action.name}`));
    window.scrollTo(0, 0);
  } catch (error) {
    yield put(createFailed(error));
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

export default function* createDataSourceSaga() {
  yield takeLatest(CREATE, onCreate);
  yield takeLatest(TEST_CONNECTION, onTestConnection);
}
