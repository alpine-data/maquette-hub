import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { 
  createDataAccessRequestFailed, createDataAccessRequestSuccess,
  getDatasetFailed, getDatasetSuccess, 
  getDataAccessRequests as getDataAccessRequestsAction, getDataAccessRequestsFailed, getDataAccessRequestsSuccess,
  getProjectFailed, getProjectSuccess,
  getProjectsFailed, getProjectsSuccess, 
  updateDataAccessRequestSuccess, updateDataAccessRequestFailed } from './actions';
import { 
  CREATE_DATA_ACCESS_REQUEST, 
  GET_DATASET, 
  GET_DATA_ACCESS_REQUESTS,
  UPDATE_DATA_ACCESS_REQUEST } from './constants';

import { command } from 'utils/request';

export function* createDataAccessRequest(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets access-requests create', _.omit(action, 'type'), user);
    
    yield put(createDataAccessRequestSuccess(data));
  } catch (err) {
    yield put(createDataAccessRequestFailed(err));
  }
}

export function* getDataset(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets get', _.omit(action, 'type'), user);
    
    yield put(getDatasetSuccess(action.project, action.dataset, data));
  } catch (err) {
    yield put(getDatasetFailed(err));
  }
}

export function* getDataAccessRequests(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets access-requests list', _.omit(action, 'type'), user);
    
    yield put(getDataAccessRequestsSuccess(data));
  } catch (err) {
    yield put(getDataAccessRequestsFailed(err));
  }
}

export function* getProject(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects get', { name: action.project }, user);
    
    yield put(getProjectSuccess(action.project, data));
  } catch (err) {
    yield put(getProjectFailed(err));
  }
}

export function* getProjects() {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects list', { }, user);
    
    yield put(getProjectsSuccess(data));
  } catch (err) {
    yield put(getProjectsFailed(err));
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

// Individual exports for testing
export default function* datasetSaga() {
  yield takeLatest(CREATE_DATA_ACCESS_REQUEST, createDataAccessRequest);

  yield takeLatest(GET_DATASET, getDataset);
  yield takeLatest(GET_DATASET, getDataAccessRequests);
  yield takeLatest(GET_DATASET, getProject);
  yield takeLatest(GET_DATASET, getProjects);

  yield takeLatest(GET_DATA_ACCESS_REQUESTS, getDataAccessRequests);

  yield takeLatest(UPDATE_DATA_ACCESS_REQUEST, updateDataAccessRequest)

}
