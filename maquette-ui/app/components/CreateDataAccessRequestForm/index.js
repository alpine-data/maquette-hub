/**
 *
 * CreateDataAccessRequestForm
 *
 */
import React, { useState } from 'react';
import produce from 'immer';

import { ControlLabel, FlexboxGrid, Form, FormGroup, Input, InputPicker, HelpBlock, ButtonToolbar, Button, Message } from 'rsuite';

function CreateDataAccessRequestForm(props) {
  const [state, setState] = useState({
    asset: props.asset,
    project: props.projects[0].value,
    reason: ""
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value
    }));
  }

  const isValid = state.project.length > 0 && state.reason.length > 0;

  const isSubmitting = _.get(props, 'createDataAccessRequest.submitting') || false;
  const isOwner = _.get(props, 'createDataAccessRequest.data.isOwner') || false;
  const requiresExplicitApproval = _.get(props, 'createDataAccessRequest.data.requiresExplicitApproval') || false;
  const submitText = (isOwner && 'Grant access for project') || (!requiresExplicitApproval && 'Subscribe project') || 'Submit request'
  const error = _.get(props, 'createDataAccessRequest.submitError');

  return <Form fluid>
    <FlexboxGrid justify="space-between">
      {
        error && <>
          <FlexboxGrid.Item colspan={24}>
          <Message
            type="error"
            title="Something went wrong ..."
            description={
              <p>{ error }</p>
            } />
          </FlexboxGrid.Item>
        </>
      }      
      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>Target asset</ControlLabel>
          <Input disabled={ true } value={ `${props.assetType}s/${props.asset}` } />
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>Project</ControlLabel>
          <InputPicker 
            data={ props.projects } 
            name="project" 
            style={{ width: "100%" }} 
            onChange={ onChange('project') } 
            value={ state.project } />
          <HelpBlock>
            Select the project for which should be able to access the data.
          </HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <FormGroup>
          <ControlLabel>Justification</ControlLabel>
          <Input 
            name="reason" 
            componentClass="textarea" 
            rows={ 5 } 
            value={ state.reason } onChange={ onChange('reason') } />
          <HelpBlock>Describe for which purpose you are going to use the data and how you want to work with the data.</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <ButtonToolbar>
          <Button 
            appearance="primary" 
            type="submit" 
            disabled={ !isValid }
            loading={ isSubmitting }
            onClick={ () => props.onSubmit(state) }>{ submitText }</Button>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Form>;
}

CreateDataAccessRequestForm.propTypes = {};

CreateDataAccessRequestForm.defaultProps = {
  projects: [],
  onSubmit: console.log
}

export default CreateDataAccessRequestForm;
