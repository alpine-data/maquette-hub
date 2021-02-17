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

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectCreateSource from './selectors';

import CreateDataAsset, { createSaga as createDataAssetSaga, createReducer as createDataAssetReducer } from '../../components/CreateDataAsset';
import SourcePropertiesForm, { validate, initialState, createSaga as createSourceSaga, createReducer as createSourceReducer, createSelector as createSourceSelector } from '../../components/SourcePropertiesForm';

const container = 'createSource';
const assetType = 'source';
const propertiesContainer = 'createSourceProperties';

const dataAssetSaga = createDataAssetSaga(container, '/shop/sources');
const dataAssetReducer = createDataAssetReducer(container, assetType);

const propertiesSaga = createSourceSaga(propertiesContainer);
const propertiesReducer = createSourceReducer(propertiesContainer);
const makeSelectSource = createSourceSelector(propertiesContainer);

export function CreateSource(props) {
  useInjectReducer({ key: propertiesContainer, reducer: propertiesReducer });
  useInjectSaga({ key: propertiesContainer, saga: propertiesSaga });

  return <CreateDataAsset
    assetType="source"
    container="createSource" 
    description="A data source allows direct or indirect access to data of a database. The consumer does not need to have the actual database credentials, nor the database driver. Access is possible by using the SDK client only."
    createCommand="sources create"
    fetchCommand="views data-asset create"
    fetchRequest={ {} }
    reducer={ dataAssetReducer }
    saga={ dataAssetSaga }

    componentClass={ SourcePropertiesForm }
    componentAdditionalProps={{
      container: propertiesContainer,
      reducer: propertiesReducer,
      saga: propertiesSaga
    }}

    initialState={ initialState }
    validate={ validate }
    { ...props } />
}

CreateSource.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createSource: makeSelectCreateSource(),
  [propertiesContainer]: makeSelectSource()
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
