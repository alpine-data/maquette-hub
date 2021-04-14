import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../../containers/App/selectors';
import { CHANGED_USER } from '../../containers/App/constants';
import { push } from 'connected-react-router';

import { command } from 'utils/request';

import actions from './actions';
import constants from './constants';
import { pluralizeWord } from '../../utils/helpers';

export default function createSaga(container) {
    const { fetchSuccess, fetchFailure, updateSuccess, updateFailed } = actions(container);
    const { FETCH, UPDATE } = constants(container);

    function* onChangedUser() {
        try {
            const action = yield select(state => _.get(state, `${container}.fetchAction`))
            yield put(action);
        } catch (err) {
            yield put(fetchFailure(err));
        }
    }

    function* onFetch(action) {
        try {
            const user = yield select(makeSelectCurrentUser());
            const response = yield call(command, action.command, action.request, user);
            yield put(fetchSuccess(response));
        } catch (error) {
            yield put(fetchFailure(error));
        }
    }

    function* onUpdate(action) {
        try {
            const user = yield select(makeSelectCurrentUser());
            const response = yield call(command, action.command, action.request, user);
            yield put(updateSuccess(response));

            const oldName = _.get(action, `request.name`);
            const newName = _.get(action, `request.metadata.name`) || oldName;
            const fetchAction = yield select(state => _.get(state, `${container}.fetchAction`))

            if (oldName !== newName) {
                _.set(fetchAction, 'request.name', newName);
                yield put(push(`/shop/${pluralizeWord(container)}/${newName}`))
            }

            yield put(fetchAction);
            window.scrollTo(0, 0);
        } catch (err) {
            yield put(updateFailed(err));
        }
    }

    return function* saga() {
        yield takeLatest(CHANGED_USER, onChangedUser);
        yield takeLatest(FETCH, onFetch);
        yield takeLatest(UPDATE, onUpdate);
    }
}
