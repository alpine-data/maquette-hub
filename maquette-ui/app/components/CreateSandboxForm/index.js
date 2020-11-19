/**
 *
 * CreateSandboxForm
 *
 */
import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { produce } from 'immer';

import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock, InputPicker } from 'rsuite';
import StackConfigurationForm from '../StackConfigurationForm';
// import styled from 'styled-components';

function CreateSandboxForm(props) {
  const projects = _.map(_.get(props, 'projects') || [], p => {
    return {
      label: p.title,
      value: p.name
    }
  });

  const [state, setState] = useState({
    name: '',
    project: props.project || projects[0].value,
    stacks: [ ]
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  const stacks_onChange = (idx) => (value) => {
    setState(produce(state, draft => {
      if (!_.isEmpty(value)) {
        draft.stacks.splice(idx, 1);
        draft.stacks.splice(idx, 0, value);
      } else {
        draft.stacks.splice(idx, 1);
      }
    }));
  }

  const addNewStack = !_.find(state.stacks, s => !s.stack)
  const selectedStacks = _.map(state.stacks, s => s.stack);
  const availableStacks = _.filter(props.stacks, s => !_.includes(selectedStacks, s.name))

  return <>
    <Form fluid>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Project</ControlLabel>
            <InputPicker data={ projects } style={{ width: "100%" }} onChange={ onChange('project') } value={ state.project } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Sandbox Name</ControlLabel>
            <FormControl onChange={ onChange('name') } value={ state.name } />
            <HelpBlock>Select a speaking title for your sandbox environment.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>
    <hr />
    <>
      {
        _.map(state.stacks, (s, idx) => <React.Fragment key={s}>
          <StackConfigurationForm
            key={ `stack-${idx}` }
            value={ s }
            stacks={ props.stacks }
            onChange={ stacks_onChange(idx) } />
          <hr />
        </React.Fragment>)
      }
    </>
    {
      addNewStack && !_.isEmpty(availableStacks) && <>
        <StackConfigurationForm 
          key={ `new-${_.size(state.stacks)}` }
          value={ {} } 
          stacks={ availableStacks } 
          onChange={ stacks_onChange(_.size(state.stacks)) } />
        <hr />
      </>
    }

    <ButtonToolbar>
      <Button 
        appearance="primary" 
        disabled={ _.isEmpty(state.stacks) || _.isEmpty(state.name) }
        onClick={ () => props.onSubmit(state) }>Create Sandbox</Button>
    </ButtonToolbar>
  </>;
}

CreateSandboxForm.propTypes = {
  project: PropTypes.object.isRequired,
  projects: PropTypes.array,

  onSubmit: PropTypes.func
};

CreateSandboxForm.defaultProps = {
  onSubmit: console.log
}

export default CreateSandboxForm;
