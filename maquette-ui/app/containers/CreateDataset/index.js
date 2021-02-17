/**
 *
 * CreateDataset
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectCreateDataset from './selectors';

import CreateDataAsset, { createSaga, createReducer } from '../../components/CreateDataAsset';

const container = 'createDataset';
const assetType = 'dataset';
const saga = createSaga(container, '/shop/datasets');
const reducer = createReducer(container, assetType);

export function CreateDataset(props) {
  return <CreateDataAsset
    assetType="dataset"
    container="createDataset" 
    description="A dataset contains structured data records with a consistent schema, just like a data frame. A single data set may contain multiple versions of the dataset with a varying schema."
    createCommand="datasets create"
    fetchCommand="views data-asset create"
    fetchRequest={ {} }
    reducer={ reducer }
    saga={ saga }
    { ...props } />
}

CreateDataset.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createDataset: makeSelectCreateDataset(),
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

export default compose(withConnect)(CreateDataset);
