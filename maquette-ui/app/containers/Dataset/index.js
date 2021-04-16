/**
 *
 * Dataset
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import makeSelectDataset from './selectors';

import DatasetOverview from 'components/DatasetOverview';
import DataAsset, { createActions, createSaga, createReducer } from '../../components/DataAsset';
import codeExamples from './samples'
import _ from 'lodash';

const container = 'dataset';
const saga = createSaga(container);
const reducer = createReducer(container);
const { update } = createActions(container);

export function Dataset(props) {
  const canConsume = _.get(props, 'dataset.view.permissions.canConsume');
  
  return <DataAsset 
    container="dataset"
    additionalTabs={
      [
        {
          order: 15,
          label: 'Data',
          key: 'data',
          link: '/data',
          visible: true,
          component: () => <>
              <DatasetOverview { ...props } onAnalyze={ version => update('datasets versions analyze', { name: _.get(props, 'dataset.view.asset.properties.metadata.name'), version: version }) } />
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
    codeExamples={ codeExamples }
    reducer={ reducer }
    saga={ saga } 
    { ...props } />
}

Dataset.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  dataset: makeSelectDataset(),
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

export default compose(withConnect)(Dataset);
