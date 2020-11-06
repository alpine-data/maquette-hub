import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { getProjectSuccess, getProjectFailed } from './actions';
import { GET_PROJECT } from './constants';

import { command } from 'utils/request';

export function* getProject(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects get', { "name": action.name }, user);
    
    yield put(getProjectSuccess(action.name, data));
  } catch (err) {
    yield put(getProjectFailed(action.name, err));
  }
}

// Individual exports for testing
export default function* projectSaga() {
  yield takeLatest(GET_PROJECT, getProject);
}
