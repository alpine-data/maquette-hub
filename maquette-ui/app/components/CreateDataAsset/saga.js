import _ from 'lodash';
import { takeLatest, call, put, select, takeEvery } from 'redux-saga/effects';
import { push } from 'connected-react-router';

import { makeSelectCurrentUser } from '../../containers/App/selectors';

import { command } from 'utils/request';

import actions from './actions';
import constants from './constants';

export default function createSaga(container, forwardBasePath = '/shop/type') {
    const { fetchSuccess, fetchFailure, createSuccess, createFailure } = actions(container);
    const { FETCH, CREATE } = constants(container);

    function* onFetch(action) {
        try {
            const user = yield select(makeSelectCurrentUser());
            const response = yield call(command, action.command, action.request, user);
            yield put(fetchSuccess(response));
        } catch (error) {
            yield put(fetchFailure(error));
        }
    }

    function* onCreate(action) {
        try {
            window.scrollTo(0, 0);
            const user = yield select(makeSelectCurrentUser());
            const data = yield call(command, action.command, action.request, user);

            yield put(createSuccess(action.request, data));
            yield put(push(`${forwardBasePath}/${action.request.name}`));
        } catch (error) {
            yield put(createFailure(error));
        }
    }

    return function* saga() {
        yield takeLatest(CREATE, onCreate);
        yield takeLatest(FETCH, onFetch);
    }
}
