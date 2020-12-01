import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { failed, fetched, submit_failed, submit_success } from './actions';
import { INIT, SUBMIT } from './constants';
import { command } from 'utils/request';
import { push } from 'connected-react-router';

export function* onInit(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views create-data-access-request', _.omit(action, 'type'), user);

    yield put(fetched(data));
  } catch (err) {
    yield put(failed(err));
  }
}

export function* onSubmit(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets access-requests create', action.request, user);

    yield put(submit_success(data));
    yield put(push(`/shop/datasets/${action.request.dataset}/access-requests`));
  } catch (err) {
    yield put(submit_failed(err));
  }
}

// Individual exports for testing
export default function* createDataAccessRequestSaga() {
  yield takeLatest(INIT, onInit);
  yield takeLatest(SUBMIT, onSubmit);
}
