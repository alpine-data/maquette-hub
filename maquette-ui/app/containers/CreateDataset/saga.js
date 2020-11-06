import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';
import { CREATE_DATASET, CREATE_DATASET_SUCCESS, LOAD_PROJECTS } from './constants';
import { createDatasetSuccess, createDatasetFailure, loadProjectsSuccess, loadProjectsFailure } from './actions'
import { push } from 'connected-react-router';

import { makeSelectCurrentUser } from '../App/selectors';

import { command } from 'utils/request';

function* createDataset(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'datasets create', _.omit(action, 'type'), user);

    yield put(createDatasetSuccess(action.project, action.name, data));
  } catch (error) {
    yield put(createDatasetFailure(action.project, error));
  }
}

export function* createDatasetSuccessForward(action) {
  yield put(push(`/${action.project}/resources/datasets/${action.name}`));
}

function* loadProjects(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects list', {}, user);
    
    yield put(loadProjectsSuccess(action.user, data));
  } catch (err) {
    yield put(loadProjectsFailure(err));
  }
}

export default function* createDatasetSaga() {
  yield takeLatest(CREATE_DATASET, createDataset);
  yield takeLatest(CREATE_DATASET_SUCCESS, createDatasetSuccessForward);
  yield takeLatest(LOAD_PROJECTS, loadProjects);
}
