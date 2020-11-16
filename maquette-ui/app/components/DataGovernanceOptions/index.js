/**
 *
 * DataGovernanceOptions
 *
 */

import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { produce } from 'immer';
import { VisibilityFormGroup, DataClassificationFormGroup, PersonalInformtionFormGroup } from '../CreateDatasetForm'
import { Button, ButtonToolbar, Form } from 'rsuite';

function DataGovernanceOptions({ visibility, classification, personalInformation, onUpdate = console.log }) {

  const [state, setState] = useState({
    visibility,
    classification, 
    personalInformation});

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;

      if (field == "personalInformation") {
        if (value == "pi" && (state.classification == "public" || state.classification == "internal")) {
          draft.classification = "confidential";
        } else if (value == "spi" && state.classification != "restricted") {
          draft.classification = "restricted";
        }
      }
    }))
  }

  const changed = state.visibility != visibility || state.classification != classification || state.personalInformation != personalInformation;

  return <>
    <h4>Data Governance Options</h4>
    <Form>
      <VisibilityFormGroup value={ state.visibility } onChange={ onChange('visibility') } />
      <hr />
      <DataClassificationFormGroup value={ state.classification } onChange={ onChange('classification') } personalInformation={ state.personalInformation } />
      <hr />
      <PersonalInformtionFormGroup value={ state.personalInformation } onChange={ onChange('personalInformation') } />
      <hr />
      <ButtonToolbar>
        <Button 
          disabled={ !changed }
          appearance="primary" 
          onClick={ () => onUpdate(state.visibility, state.classification, state.personalInformation) }>Save changes</Button>
      </ButtonToolbar>
    </Form>
  </>;
}

DataGovernanceOptions.propTypes = {
  visibility: PropTypes.string.isRequired,
  classification: PropTypes.string.isRequired,
  personalInformation: PropTypes.string.isRequired,

  onUpdate: PropTypes.func
};

export default DataGovernanceOptions;
