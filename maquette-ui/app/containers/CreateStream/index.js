/**
 *
 * CreateStream
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectCreateStream from './selectors';

import CreateDataAsset, { createSaga, createReducer } from '../../components/CreateDataAsset';
import StreamPropertiesForm, { initialState, validate } from '../../components/StreamPropertiesForm';

const container = 'createStream';
const assetType = 'stream';
const saga = createSaga(container, '/shop/streams');
const reducer = createReducer(container, assetType);

export function CreateStream(props) {
  return <CreateDataAsset
    assetType="stream"
    container="createStream" 
    description="A stream publishes data as a stream of events/ updates. Existing data is retained for a specified duration. A consumer can `listen` to the data."
    reducer={ reducer }
    saga={ saga }

    componentClass={ StreamPropertiesForm }
    initialState={ initialState }
    validate={ validate }

    { ...props } />
}

CreateStream.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createStream: makeSelectCreateStream(),
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

export default compose(withConnect)(CreateStream);
