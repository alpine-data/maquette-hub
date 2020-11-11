import { takeLatest, call, put, select } from 'redux-saga/effects';
import { GET_PROJECTS } from './constants';
import { getProjectsFailed, getProjectsSuccess } from './actions';

import { makeSelectCurrentUser } from '../App/selectors';

import { command } from 'utils/request';

function* getProjects() {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'projects list', {}, user);
    
    yield put(getProjectsSuccess(data));
  } catch (error) {
    yield put(getProjectsFailed(error));
  }
}

export default function* dashboardSaga() {
  yield takeLatest(GET_PROJECTS, getProjects);
}
