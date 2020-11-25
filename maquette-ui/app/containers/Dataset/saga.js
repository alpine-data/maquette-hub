import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { 
  init as initAction,
  failed,
  fetched,

  createDataAccessRequestFailed, 
  createDataAccessRequestSuccess,

  getDataAccessRequestsSuccess,
  getDatasetSuccess,
  getProjectSuccess,
  getVersionsSuccess,

  grantAccessFailed, 
  grantAccessSuccess,

  revokeAccessFailed, 
  revokeAccessSuccess,

  updateDataAccessRequestFailed,
  updateDataAccessRequestSuccess, 
  
  updateDatasetFailed,
  updateDatasetSuccess } from './actions';

import { 
  INIT,
  CREATE_DATA_ACCESS_REQUEST, 
  GRANT_ACCESS,
  REVOKE_ACCESS,
  UPDATE_DATA_ACCESS_REQUEST,
  UPDATE_DATASET } from './constants';

import { command } from 'utils/request';

import { push } from 'connected-react-router';

export function* fetch(key, cmd, params, user) {
  try {
    const data = yield call(command, cmd, params, user);
    yield put(fetched(key, data));
  } catch (err) {
    yield put(failed(key, err));
  }
}

export function* init(action) {
  const user = yield select(makeSelectCurrentUser());
  
  yield fetch('dataset', 'datasets get', _.omit(action, 'type'), user);
  yield fetch('requests', 'datasets access-requests list', _.omit(action, 'type'), user);
  yield fetch('project', 'projects get', { name: action.project }, user);
  yield fetch('versions', 'datasets revisions list', _.omit(action, 'type'), user);
}

export function* createDataAccessRequest(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets access-requests create', _.omit(action, 'type'), user);
    
    yield put(createDataAccessRequestSuccess(data));
  } catch (err) {
    yield put(createDataAccessRequestFailed(err));
  }
}

export function* grantAccess(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets grant', _.omit(action, 'type'), user);
    
    yield put(grantAccessSuccess(data));
    yield put(getDatasetAction(action.project, action.dataset, false));
  } catch (err) {
    yield put(grantAccessFailed(error));
  }
}

export function* revokeAccess(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets revoke', _.omit(action, 'type'), user);
    
    yield put(revokeAccessSuccess(data));
    yield put(getDatasetAction(action.project, action.dataset, false));
  } catch (err) {
    yield put(revokeAccessFailed(err));
  }
}

export function* updateDataAccessRequest(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, action.command, action.args, user);
    
    yield put(updateDataAccessRequestSuccess(action.args.project, action.args.dataset, data));
    yield put(getDataAccessRequestsAction(action.args.project, action.args.dataset));
  } catch (err) {
    yield put(updateDataAccessRequestFailed(err));
  }
}

export function* updateDataset(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets update', _.omit(action, 'type'), user);

    yield put(updateDatasetSuccess(data));

    if (action.dataset == action.name) {
      yield put(initAction(action.project, action.dataset));
    } else {
      yield put(push(`/${action.project}/resources/datasets/${action.name}`));
    }
  } catch (err) {
    yield put(updateDatasetFailed(err));
  }
}

// Individual exports for testing
export default function* datasetSaga() {
  yield takeLatest(CREATE_DATA_ACCESS_REQUEST, createDataAccessRequest);

  yield takeLatest(INIT, init);

  yield takeLatest(GRANT_ACCESS, grantAccess);
  yield takeLatest(REVOKE_ACCESS, revokeAccess);
  yield takeLatest(UPDATE_DATA_ACCESS_REQUEST, updateDataAccessRequest);
  yield takeLatest(UPDATE_DATASET, updateDataset);
}
