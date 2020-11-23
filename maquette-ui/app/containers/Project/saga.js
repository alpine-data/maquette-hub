import { takeEvery, takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { 
  failed,
  getProjectSuccess,
  getDataAssetsSuccess,
  getSandboxesSuccess, 
  getStacksSuccess,
  grantAccessFailed, grantAccessSuccess,
  revokeAccessFailed, revokeAccessSuccess,
  updateProjectFailed, updateProjectSuccess } from './actions';

import { 
  INIT, 
  GRANT_ACCESS,
  REVOKE_ACCESS,
  UPDATE_PROJECT } from './constants';

import { command } from 'utils/request';
import { push } from 'connected-react-router';

export function* init(action) {
  const user = yield select(makeSelectCurrentUser());
  try {
    const data = yield call(command, 'projects get', { "name": action.project }, user);
    yield put(getProjectSuccess(data));
  } catch (err) {
    yield put(failed('project', err));
  }

  try {
    const data = yield call(command, 'projects data-assets list', _.omit(action, 'type'), user);
    yield put(getDataAssetsSuccess(data));
  } catch (err) {
    yield put(failed('data-assets', err));
  }

  try {
    const data = yield call(command, 'sandboxes list', _.omit(action, 'type'), user);
    yield put(getSandboxesSuccess(data));
  } catch (err) {
    yield put(failed('sandboxes', err));
  }

  try {
    const data = yield call(command, 'sandboxes stacks', {}, user);
    yield put(getStacksSuccess(data));
  } catch (err) {
    yield put(failed('stacks', err));
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
  yield takeLatest(INIT, init);

  yield takeEvery(GRANT_ACCESS, grantAccess);
  yield takeEvery(REVOKE_ACCESS, revokeAccess);

  yield takeLatest(UPDATE_PROJECT, updateProject);
}
