/**
 *
 * Stream
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectStream from './selectors';

import StreamOverview from 'components/StreamOverview';
import DataAsset, { createSaga, createReducer } from '../../components/DataAsset';
import StreamPropertiesForm, { validate as streamValidate, initialState as streamInitialState } from '../../components/StreamPropertiesForm';
import codeExamples from './samples'

const container = 'stream';
const saga = createSaga(container);
const reducer = createReducer(container);

export function Stream(props) {
  const canConsume = _.get(props, 'stream.view.permissions.canConsume');
  
  return <DataAsset 
    container="stream"
    additionalTabs={
      [
        {
          order: 15,
          label: 'Data',
          key: 'data',
          link: '/data',
          visible: true,
          component: () => <>
                <StreamOverview { ...props } />
            </>
        },
        {
          order: 16,
          label: 'Browse Data',
          key: 'browse',
          link: '/browse',
          visible: canConsume,
          component: () => <>TODO</>
        }
      ]
    }
    settingsComponentClass={ StreamPropertiesForm }
    settingsComponentInitialState={ streamInitialState }
    settingsComponentValidate={ streamValidate }
    codeExamples={ codeExamples }
    reducer={ reducer }
    saga={ saga } 
    { ...props } />
}

Stream.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  stream: makeSelectStream(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(Stream);
