/**
 *
 * CreateProjectForm
 *
 */

import React, {useState} from 'react';
import PropTypes from 'prop-types';
// import styled from 'styled-components';

import produce from 'immer';
import kebabcase from 'lodash.kebabcase';

import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, InputPicker, HelpBlock, ButtonToolbar, Button, Radio, RadioGroup } from 'rsuite';

export function VisibilityFormGroup({ value, onChange }) {
  return <FormGroup>
    <ControlLabel>Visibility</ControlLabel>
    <HelpBlock>The dataset metadata might be visible to allow other users to find the dataset. If the dataset is visible, the data is still not accessible for everyone.</HelpBlock>
    <RadioGroup name="visibility" value={ value } onChange={ onChange } style={{ paddingTop: "10px" }}>
      <Radio value="public"><b>Public</b> <br />The dataset and its metadata can be seen by everyone. The data is not public.</Radio>
      <Radio value="private"><b>Private</b><br />The dataset is entirely hidden from other users.</Radio>
    </RadioGroup>
  </FormGroup>;
}

export function DataClassificationFormGroup({ value, onChange, personalInformation }) {
  return <FormGroup>
    <ControlLabel>Data Classification</ControlLabel>
    <HelpBlock>Classify the contained data.</HelpBlock>

    <RadioGroup name="classification" value={ value } onChange={ onChange } style={{ paddingTop: "10px" }}>
      <Radio value="public" disabled={ personalInformation != "none" }><b>Public</b> <br />The data is public information, it can be used by everyone, everywere for any purpose.</Radio>
      <Radio value="internal" disabled={ personalInformation != "none" }><b>Internal</b><br />The dataset may contain company internal information. If disclosed, the impact to business is minimal.</Radio>
      <Radio value="confidential" disabled={ personalInformation == "spi" }><b>Confidential</b><br />The dataset may contain business critical internal information. If disclosed, business or brand could be negatively effected.</Radio>
      <Radio value="restricted"><b>Restricted</b><br />The dataset may contain highly sensitive information. If disclosed, it will have significant financial or legal impact.</Radio>
    </RadioGroup>
  </FormGroup>;
}

export function PersonalInformtionFormGroup({ value, onChange }) {
  return <FormGroup>
    <ControlLabel>Personal Information</ControlLabel>
    <HelpBlock>Specify whether the data may contain data points which could identify a natural person.</HelpBlock>

    <RadioGroup name="personalInformation" value={ value } onChange={ onChange } style={{ paddingTop: "10px" }}>
      <Radio value="none"><b>None</b> <br />The data definetely does not contain any personal information.</Radio>
      <Radio value="pi"><b>Personal Information</b><br />The dataset may contain personal information.</Radio>
      <Radio value="spi"><b>Sensitive Personal Information</b><br />The dataset may contain sensitive personal information according to GDPR.</Radio>
    </RadioGroup>
  </FormGroup>;
}

function CreateDatasetForm({ creating = false, onSubmit = console.log }) {
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

  const isValid = state.title.length > 3 && state.name.length > 3;

  return <Form fluid>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Dataset Title</ControlLabel>
            <FormControl name="title" onChange={ onChange('title') } value={ state.title } />
            <HelpBlock>Select a speaking, memorable title for the dataset.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Dataset Name</ControlLabel>
            <FormControl name="name" value={ state.name } onChange={ onChange('name') } />
            <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Dataset Summary</ControlLabel>
            <FormControl name="summary" onChange={ onChange('summary') } value={ state.summary } />
            <HelpBlock>Describe in a few words which data is contained in your dataset.</HelpBlock>
          </FormGroup>
          
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
                disabled={ !isValid }
                loading={ creating }
                onClick={ () => onSubmit(state) }>Create dataset</Button>
            </ButtonToolbar>
          </FormGroup>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>;
}

CreateDatasetForm.propTypes = {
  onSubmit: PropTypes.func.isRequired
};

CreateDatasetForm.defaultProps = {
  onSubmit: (data) => console.log(data)
}

export default CreateDatasetForm;
