/*
 *
 * TestComponent reducer
 *
 */
import produce from 'immer';
import { DEFAULT_ACTION, CLICK_ACTION } from './constants';

export const initialState = {};

/* eslint-disable default-case, no-param-reassign */
const testComponentReducer = (state = initialState, action) =>
  produce(state, (draft) => {
    switch (action.type) {
      case DEFAULT_ACTION:
        break;

      case CLICK_ACTION:
        draft.foo = action.foo;
    }
  });

export default testComponentReducer;
