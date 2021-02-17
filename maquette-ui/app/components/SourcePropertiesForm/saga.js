import _ from 'lodash';
import { takeLatest, call, put, select } from 'redux-saga/effects';

import { makeSelectCurrentUser } from '../../containers/App/selectors';

import { command } from 'utils/request';

import actions from './actions';
import constants from './constants';

export default function createSaga(container) {
    const { testConnectionSuccess } = actions(container);
    const { TEST_CONNECTION } = constants(container);

    function* onTestConnection(action) {
        try {
          const user = yield select(makeSelectCurrentUser());
          const data = yield call(command, 'sources test', _.omit(action, 'type'), user);
      
          yield put(testConnectionSuccess(data));
        } catch (error) {
          yield put(testConnectionSuccess({ "result": "failed", "message": "Could not connect to server to test connection." }));
        }
    }

    return function* saga() {
        yield takeLatest(TEST_CONNECTION, onTestConnection);
    }
}
