import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';
import { CREATE } from './constants';
import { createSuccess, createFailure } from './actions'
import { push } from 'connected-react-router';

import { makeSelectCurrentUser } from '../App/selectors';

import { command } from 'utils/request';

function* onCreate(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'collections create', _.omit(action, 'type'), user);

    yield put(createSuccess(action.name, data));
    yield put(push(`/shop/collections/${action.name}`));
    window.scrollTo(0, 0);
  } catch (error) {
    yield put(createFailure(error));
  }
}

export default function* createCollectionSaga() {
  yield takeLatest(CREATE, onCreate);
}
