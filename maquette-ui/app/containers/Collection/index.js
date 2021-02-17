/**
 *
 * Collection
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectCollection from './selectors';

import CollectionOverview from 'components/CollectionOverview';
import DataAsset, { createSaga, createReducer } from '../../components/DataAsset';

const container = 'collection';
const saga = createSaga(container);
const reducer = createReducer(container);

export function Collection(props) {
  const canConsume = _.get(props, 'collection.view.permissions.canConsume');
  
  return <DataAsset 
    container="collection"
    additionalTabs={
      [
        {
          order: 15,
          label: 'Files',
          key: 'data',
          link: '/data',
          visible: canConsume,
          component: () => <CollectionOverview { ...props } />
        }
      ]
    }
    reducer={ reducer }
    saga={ saga } 
    { ...props } />
}

Collection.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  collection: makeSelectCollection(),
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

export default compose(withConnect)(Collection);
