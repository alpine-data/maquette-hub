import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';
import { CREATE } from './constants';
import { createFailed, createSuccess, testConnectionSuccess } from './actions'

import { makeSelectCurrentUser } from '../App/selectors';
import { push } from 'connected-react-router';

import { command } from 'utils/request';


function* onCreate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'streams create', _.omit(action, 'type'), user);

    yield put(createSuccess(data));
    yield put(push(`/shop/streams/${action.name}`));
    window.scrollTo(0, 0);
  } catch (error) {
    yield put(createFailed(error));
  }
}

export default function* createStreamSaga() {
  yield takeLatest(CREATE, onCreate);
}
