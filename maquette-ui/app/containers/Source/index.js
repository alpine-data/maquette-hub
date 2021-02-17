/**
 *
 * Source
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectSource from './selectors';

import SourceOverview from 'components/SourceOverview';
import DataAsset, { createSaga, createReducer } from '../../components/DataAsset';
import codeExamples from './samples'
import SourcePropertiesForm, { validate as sourceValidate, initialState as sourceInitialState, createSaga as createSourceSaga, createReducer as createSourceReducer, createSelector as createSourceSelector } from '../../components/SourcePropertiesForm';

const container = 'source';
const propertiesContainer = 'sourceProperties';

const saga = createSaga(container);
const reducer = createReducer(container);

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
                <SourceOverview { ...props } />
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
