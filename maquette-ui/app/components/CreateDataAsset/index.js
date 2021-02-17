/**
 *
 * CreateDataAsset
 *
 */

import _ from 'lodash';
import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import { useFormState } from 'utils/hooks';

import Container from '../Container';
import ViewContainer from '../ViewContainer';

import actions from './actions';
import constants from './constants';
import reducer from './reducer';
import saga from './saga';
import { Button, ButtonToolbar, Form } from 'rsuite';
import DataAssetPropertiesForm, { initialState as initialDataAssetProperties, validate as validateDataAssetProperties } from '../DataAssetPropertiesForm';
import DataAssetTeamForm, { initialState as initialStateDataAssetTeam, validate as validateDataAssetTeam, transformProfilesToUsers } from '../DataAssetTeamForm';

export const createActions = actions;
export const createConstants = constants;
export const createReducer = reducer;
export const createSaga = saga;

function CreateDataAsset({ assetType, componentClass, container, description, createCommand, fetchCommand, fetchRequest, initialState, validate, reducer, saga, ...props }) {
  useInjectReducer({ key: container, reducer });
  useInjectSaga({ key: container, saga });

  const { create, fetch } = actions(container);
  const [initialized, setInitialized] = useState(false);
  const [stateInitialized, setStateInitialized] = useState(false);
  const [state, , onChange, onChangeValues] = useFormState(_.assign({}, initialDataAssetProperties, initialState));
  const data = _.get(props, `${container}.data`);
  const Component = componentClass;

  useEffect(() => {
    if (!initialized) {
      props.dispatch(fetch(fetchCommand, fetchRequest));
      setInitialized(true);
    }
  }, []);

  useEffect(() => {
    if (!_.isEmpty(data)) {
      onChangeValues(initialStateDataAssetTeam(data));
      setStateInitialized(true);
    }
  }, [ data ]);

  const isCreateDisabled = !validateDataAssetProperties(state) || !validateDataAssetTeam(state) || !validate(state);

  return <ViewContainer
    background="data"
    titles={ [ { label: `Create new ${assetType}` } ] }
    loading={ _.get(props, `${container}.loading`) }
    error={ _.get(props, `${container}.error`) }
    content={ () => {
      return <>
        { 
          stateInitialized && <>
              <Container md>
                <p className="mq--p-leading">
                  { description }
                </p>

                <hr />

                <Form fluid>
                  <DataAssetPropertiesForm assetType={ assetType } state={ state } onChangeValues={ onChangeValues } />
                  <hr />
                  <DataAssetTeamForm 
                    users={ transformProfilesToUsers(data) }
                    state={ state }
                    onChange={ onChange } />

                  {
                    componentClass && <>
                      <hr />
                      <Component onChange={ onChange } onChangeValues={ onChangeValues } state={ state } />
                    </>
                  }

                  <ButtonToolbar>
                    <Button
                      appearance="primary"
                      disabled={ isCreateDisabled }
                      onClick={ () => {
                        props.dispatch(create(createCommand, state))
                      } }>

                      Create { assetType }
                    </Button>
                  </ButtonToolbar>
                </Form>
              </Container>
            </>
          }
      </>
     } }>
  </ViewContainer>;
}

CreateDataAsset.propTypes = {
  assetType: PropTypes.string.isRequired,
  componentClass: PropTypes.elementType,
  container: PropTypes.string.isRequired,
  description: PropTypes.node.isRequired,
  fetchCommand: PropTypes.string.isRequired,
  fetchRequest: PropTypes.object.isRequired,

  initialState: PropTypes.object.isRequired,
  validate: PropTypes.func.isRequired
};

CreateDataAsset.defaultProps = {
  fetchRequest: {},
  initialState: {},
  validate: (state) => true
}

export default CreateDataAsset;
