import { takeLatest, select, put, call } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../App/selectors';

import { 
  init as initAction,
  failed,
  fetched,

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
  GRANT_ACCESS,
  REVOKE_ACCESS,
  UPDATE_DATA_ACCESS_REQUEST,
  UPDATE_DATASET } from './constants';

import {
  CHANGE_USER
} from '../App/constants';

import { command } from 'utils/request';

import { push } from 'connected-react-router';

export function* onChangeUser() {
  try {
    const action = yield select(state => _.get(state, 'dataset.initialParams'));
    yield put(action);
  } catch (err) {
    console.log(err);
  }
}

export function* onInit(action) {
  try {
    const user = yield select(makeSelectCurrentUser());
    const data = yield call(command, 'views dataset', _.omit(action, 'type'), user);

    yield put(fetched('data', data));
  } catch (err) {
    yield put(failed('data', err));
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
  yield takeLatest(CHANGE_USER, onChangeUser);
  yield takeLatest(INIT, onInit);

  yield takeLatest(GRANT_ACCESS, grantAccess);
  yield takeLatest(REVOKE_ACCESS, revokeAccess);
  yield takeLatest(UPDATE_DATA_ACCESS_REQUEST, updateDataAccessRequest);
  yield takeLatest(UPDATE_DATASET, updateDataset);
}
