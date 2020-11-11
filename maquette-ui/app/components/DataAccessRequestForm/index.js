/**
 *
 * DataAccessRequestForm
 *
 */
import React, { useState } from 'react';
import produce from 'immer';

import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, Input, InputPicker, HelpBlock, ButtonToolbar, Button } from 'rsuite';

function DataAccessRequestForm({ projects = [], onSubmit = console.log, ...props }) {
  const [state, setState] = useState({
    origin: projects.length > 0 ? projects[0].name : '',
    reason: ""
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value
    }));
  }

  const projectsData = _.map(projects, p => { return {
    label: p.title,
    value: p.name,
    role: "Master"
  }});

  const isValid = state.origin.length > 0 && state.reason.length > 0;

  return <Form fluid>
    <FormGroup>
      <ControlLabel>Target dataset</ControlLabel>
      <HelpBlock>Select a speaking, memorable title for the dataset.</HelpBlock>

      <InputPicker data={ projectsData } name="target" style={{ width: "100%" }} onChange={ onChange('target') } value={ state.origin } />
    </FormGroup>

    <FormGroup>
      <ControlLabel>Reason</ControlLabel>
      <Input name="reason" componentClass="textarea" rows={ 5 } value={ state.reason } onChange={ onChange('reason') } />
      <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
    </FormGroup>

    <hr />

    <FormGroup>
      <ButtonToolbar>
        <Button 
          appearance="primary" 
          type="submit" 
          disabled={ !isValid }
          onClick={ () => onSubmit(state) }>Submit request</Button>
      </ButtonToolbar>
    </FormGroup>
  </Form>;
}

DataAccessRequestForm.propTypes = {};

export default DataAccessRequestForm;
