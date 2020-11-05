/**
 *
 * TestComponent
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectTestComponent from './selectors';
import reducer from './reducer';
import saga from './saga';

import { clickAction } from './actions';

export function TestComponent({ onClick, testComponent }) {
  useInjectReducer({ key: 'testComponent', reducer });
  useInjectSaga({ key: 'testComponent', saga });

  return <div><button onClick={ onClick }>Hello World { testComponent.foo }</button></div>;
}

TestComponent.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  testComponent: makeSelectTestComponent(),
});

function mapDispatchToProps(dispatch) {
  return {
    onClick: () => dispatch(clickAction("Hello World!")),
    dispatch,
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(TestComponent);
