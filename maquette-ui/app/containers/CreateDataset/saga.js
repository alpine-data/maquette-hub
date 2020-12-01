import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';
import { CREATE } from './constants';
import { createSuccess, createFailure } from './actions'
import { push } from 'connected-react-router';

import { makeSelectCurrentUser } from '../App/selectors';

import { command } from 'utils/request';

function* onCreate(action) {
  try {
    window.scrollTo(0, 0);

    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets create', _.omit(action, 'type'), user);

    yield put(createSuccess(action.name, data));
    yield put(push(`/shop/datasets/${action.name}`))
  } catch (error) {
    yield put(createFailure(error));
  }
}

export default function* createSaga() {
  yield takeLatest(CREATE, onCreate);
}
