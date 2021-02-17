/**
 *
 * CreateSource
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectCreateSource from './selectors';

import CreateDataAsset, { createSaga, createReducer } from '../../components/CreateDataAsset';
import SourcePropertiesForm, { initialState } from '../../components/SourcePropertiesForm';

const container = 'createSource';
const assetType = 'source';
const saga = createSaga(container, '/shop/sources');
const reducer = createReducer(container, assetType);


export function CreateSource(props) {
  return <CreateDataAsset
    assetType="source"
    container="createSource" 
    description="A data source allows direct or indirect access to data of a database. The consumer does not need to have the actual database credentials, nor the database driver. Access is possible by using the SDK client only."
    createCommand="sources create"
    fetchCommand="views data-asset create"
    fetchRequest={ {} }
    reducer={ reducer }
    saga={ saga }

    componentClass={ SourcePropertiesForm }
    initialState={ initialState }
    { ...props } />
}

CreateSource.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createSource: makeSelectCreateSource(),
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

export default compose(withConnect)(CreateSource);
