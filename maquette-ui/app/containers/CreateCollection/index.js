/**
 *
 * CreateCollection
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectCreateCollection from './selectors';

import CreateDataAsset, { createSaga, createReducer } from '../../components/CreateDataAsset';

const container = 'createCollection';
const assetType = 'collection';
const saga = createSaga(container, '/shop/collections');
const reducer = createReducer(container, assetType);

export function CreateCollection(props) {
  return <CreateDataAsset
    assetType="collection"
    container="createCollection" 
    description="A collection stores versioned file sets. Each set contains a immutable set of files for further processing."
    createCommand="collections create"
    fetchCommand="views data-asset create"
    fetchRequest={ {} }
    reducer={ reducer }
    saga={ saga }
    { ...props } />
}

CreateCollection.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createCollection: makeSelectCreateCollection(),
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

export default compose(withConnect)(CreateCollection);
