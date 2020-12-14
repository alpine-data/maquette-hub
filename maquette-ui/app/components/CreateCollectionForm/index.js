/**
 *
 * CreateCollectionForm
 *
 */

import React, { useState } from 'react';
import PropTypes from 'prop-types';

import produce from 'immer';
import kebabcase from 'lodash.kebabcase';
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock, InputNumber, InputPicker } from 'rsuite';
import { DataClassificationFormGroup, PersonalInformtionFormGroup, VisibilityFormGroup } from '../CreateDatasetForm';

function CreateCollectionForm(props) {
  const [state, setState] = useState({
    title: '',
    name: '',
    summary: '',
    visibility: 'public',
    classification: 'public',
    personalInformation: 'none'
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;

      if (field == 'personalInformation') {
        if (value == 'pi' && (state.classification == 'public' || state.classification == 'internal')) {
          draft.classification = 'confidential';
        } else if (value == 'spi' && state.classification != 'restricted') {
          draft.classification = 'restricted';
        }
      }

      if (field == 'title') {
        const id = kebabcase(value.toLowerCase());
        draft.title = value;
        draft.name = id;
      }
    }));
  }

  const createdDisabled = _.size(state.title) < 3 || _.size(state.name) < 3;

  return <Form fluid>
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>Collection Title</ControlLabel>
          <FormControl name="title" onChange={ onChange('title') } value={ state.title } />
          <HelpBlock>Select a speaking, memorable title for the collection.</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>Collection Name</ControlLabel>
          <FormControl name="name" value={ state.name } onChange={ onChange('name') } />
          <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <FormGroup>
          <ControlLabel>Collection Summary</ControlLabel>
          <FormControl name="summary" onChange={ onChange('summary') } value={ state.summary } />
          <HelpBlock>Describe in a few words which data is contained in your collection.</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <hr />   
        <VisibilityFormGroup value={ state.visibility } onChange={ onChange('visibility') } />
        <hr />
        <DataClassificationFormGroup value={ state.classification } onChange={ onChange('classification') } personalInformation={ state.personalInformation } />
        <hr />
        <PersonalInformtionFormGroup value={ state.personalInformation } onChange={ onChange('personalInformation') } />
        <hr />

        <FormGroup>
          <ButtonToolbar>
            <Button 
              appearance="primary" 
              type="submit" 
              disabled={ createdDisabled }
              loading={ _.get(props, 'createCollection.creating') }
              onClick={ () => props.onCreateCollection(state) }
            >Create collection</Button>
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
