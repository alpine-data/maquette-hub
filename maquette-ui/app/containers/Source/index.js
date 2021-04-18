/**
 *
 * Source
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectSource from './selectors';

import SourceOverview from 'components/SourceOverview';
import DataAsset, { createActions, createSaga, createReducer } from '../../components/DataAsset';
import codeExamples from './samples'
import SourcePropertiesForm, { validate as sourceValidate, initialState as sourceInitialState, createSaga as createSourceSaga, createReducer as createSourceReducer, createSelector as createSourceSelector } from '../../components/SourcePropertiesForm';

const container = 'source';
const propertiesContainer = 'sourceProperties';

const saga = createSaga(container);
const reducer = createReducer(container);
const { update } = createActions(container);

const propertiesSaga = createSourceSaga(propertiesContainer);
const propertiesReducer = createSourceReducer(propertiesContainer);
const makeSelectSourceProperties = createSourceSelector(propertiesContainer);

export function Source(props) {
  useInjectReducer({ key: propertiesContainer, reducer: propertiesReducer });
  useInjectSaga({ key: propertiesContainer, saga: propertiesSaga });

  const canConsume = _.get(props, 'source.view.permissions.canConsume');
  
  return <DataAsset 
    container="source"
    additionalTabs={
      [
        {
          order: 15,
          label: 'Data',
          key: 'data',
          link: '/data',
          visible: true,
          component: () => <>
                <SourceOverview 
                  onAnalyze={ () => update('sources analyze', { name: _.get(props, 'source.view.asset.properties.metadata.name') }) }
                  { ...props } />
            </>
        }
      ]
    }
    
    settingsComponentClass={ SourcePropertiesForm }
    settingsComponentInitialState={ sourceInitialState }
    settingsComponentValidate={ sourceValidate }
    settingsComponentAdditionalProps={{
      container: propertiesContainer,
      reducer: propertiesReducer,
      saga: propertiesSaga
    }}

    codeExamples={ codeExamples }
    reducer={ reducer }
    saga={ saga } 
    { ...props } />
}

Source.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  source: makeSelectSource(),
  [propertiesContainer]: makeSelectSourceProperties()
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

export default compose(withConnect)(Source);
