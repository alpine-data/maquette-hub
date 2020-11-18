import { takeEvery, takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { 
  getProject as getProjectAction,
  getProjectSuccess, getProjectFailed, 
  getDatasetsSuccess, getDatasetsFailed,
  getSandboxesSuccess, getSandboxesFailed,
  getStacksSuccess, getStacksFailed,
  grantAccessFailed, grantAccessSuccess,
  revokeAccessFailed, revokeAccessSuccess,
  updateProjectFailed, updateProjectSuccess } from './actions';

import { 
  GET_PROJECT, 
  GRANT_ACCESS,
  REVOKE_ACCESS,
  UPDATE_PROJECT } from './constants';

import { command } from 'utils/request';
import { push } from 'connected-react-router';

export function* getProject(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects get', { "name": action.name }, user);
    
    yield put(getProjectSuccess(action.name, data));
  } catch (err) {
    yield put(getProjectFailed(action.name, err));
  }
}

export function* getDatasets(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets list', { "project": action.name }, user);
    
    yield put(getDatasetsSuccess(action.name, data));
  } catch (err) {
    yield put(getDatasetsFailed(action.name, err));
  }
}

export function* getSandboxes(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'sandboxes list', { "project": action.name }, user);
    
    yield put(getSandboxesSuccess(data));
  } catch (err) {
    yield put(getSandboxesFailed(err));
  }
}

export function* getStacks(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'sandboxes stacks', {}, user);
    
    yield put(getStacksSuccess(data));
  } catch (err) {
    yield put(getStacksFailed(err));
  }
}

export function* grantAccess(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects grant', { "project": action.project, "type": action.authorizationType, "name": action.name }, user);
    
    yield put(grantAccessSuccess(data));
    yield put(getProjectAction(action.project, false));
  } catch (err) {
    yield put(grantAccessFailed(action.project, err));
  }
}

export function* revokeAccess(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects revoke', { "project": action.project, "type": action.authorizationType, "name": action.name }, user);
    
    yield put(revokeAccessSuccess(data));
    yield put(getProjectAction(action.project, false));
  } catch (err) {
    yield put(revokeAccessFailed(action.project, err));
  }
}

export function* updateProject(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects update', _.omit(action, 'type'), user);
    
    yield put(updateProjectSuccess(data));

    if (action.project == action.name) {
      yield put(getProjectAction(action.project, false));
    } else {
      yield put(push(`/${action.name}`))
    }
  } catch (err) {
    yield put(updateProjectFailed(action.project, err));
  }
}

// Individual exports for testing
export default function* projectSaga() {
  yield takeLatest(GET_PROJECT, getProject);
  yield takeLatest(GET_PROJECT, getDatasets);
  yield takeLatest(GET_PROJECT, getSandboxes);
  yield takeLatest(GET_PROJECT, getStacks);

  yield takeEvery(GRANT_ACCESS, grantAccess);
  yield takeEvery(REVOKE_ACCESS, revokeAccess);

  yield takeLatest(UPDATE_PROJECT, updateProject);
}
