import { takeLatest, call, put, select } from 'redux-saga/effects';
import { CREATE_PROJECT, CREATE_PROJECT_SUCCESS } from './constants';
import { createProjectSuccess, CreateProjectFailed } from './actions';
import { push } from 'connected-react-router';

import { makeSelectCurrentUser } from '../App/selectors';

import { command } from 'utils/request';

export function* createProject(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects create', _.pick(action, 'title', 'name', 'summary'), user);
    
    yield put(createProjectSuccess(action.name, data));
  } catch (err) {
    yield put(CreateProjectFailed(action.name, err));
  }
}

export function* createProjectSuccessSaga(action) {
  yield put(push(`/${action.name}`));
}

// Individual exports for testing
export default function* createProjectSaga() {
  yield takeLatest(CREATE_PROJECT, createProject);
  yield takeLatest(CREATE_PROJECT_SUCCESS, createProjectSuccessSaga)
}
