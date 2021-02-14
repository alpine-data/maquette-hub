/**
 *
 * CreateCollectionForm
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';

import { useFormState } from '../../utils/hooks';

import { Button, ButtonToolbar, FlexboxGrid, Form, FormGroup } from 'rsuite';
import DataAssetPropertiesForm from '../DataAssetPropertiesForm';
import DataAssetTeamForm from '../DataAssetTeamForm';

function CreateCollectionForm(props) {
  const [state, , onChange, onChangeValues] = useFormState({
    title: '',
    name: '',
    summary: '',
    visibility: 'public',
    classification: 'public',
    personalInformation: 'none',
    zone: 'raw'
  });

  const createdDisabled = _.size(state.title) < 3 || _.size(state.name) < 3;

  return <Form fluid>
    <h5>Data Asset Properties</h5>
    <DataAssetPropertiesForm assetName='Collection' state={ state } onChange={ onChangeValues }  />

    <h5>Data Asset Roles</h5>
    <DataAssetTeamForm state={ state } onChange={ onChange } />

    <FlexboxGrid justify="space-between">

      <FlexboxGrid.Item>
        <hr />
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <FormGroup>
          <ButtonToolbar>
            <Button 
              appearance="primary" 
              type="submit" 
              disabled={ createdDisabled }
              loading={ _.get(props, 'createCollection.creating') }
              onClick={ () => props.onCreateCollection(state) }>
                
                Create collection
            </Button>
          </ButtonToolbar>
        </FormGroup>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Form>;
}

CreateCollectionForm.propTypes = {
  onCreateCollection: PropTypes.func
};

CreateCollectionForm.defaultProps = {
  onCreateCollection: console.log
}

export default CreateCollectionForm;
