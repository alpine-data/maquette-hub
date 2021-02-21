/**
 *
 * ModelSettings
 *
 */

import React from 'react';
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup } from 'rsuite';
import Members from '../Members';

import { useFormState } from '../../utils/hooks';

function ModelSettings({ model, onGrant = console.log, onRevoke = console.log, onUpdate = console.log }) {
  const initialState = _.pick(model, 'title', 'description');
  const [state, , onChangeValue] = useFormState(initialState);
  const changed = !_.isEqual(state, initialState);

  return <>
    <h5>Model Settings</h5>

    <Form fluid>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 12 }>
          <FormGroup>
            <ControlLabel>Model Title</ControlLabel>
            <FormControl name="title" value={ state.title } onChange={ onChangeValue('title') } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 12 } />

        <FlexboxGrid.Item colspan={ 12 }>
          <FormGroup>
            <ControlLabel>Model Name</ControlLabel>
            <FormControl name="name" disabled={ true } value={ model.name } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 12 } />

        <FlexboxGrid.Item colspan={ 14 }>
          <FormGroup>
            <ControlLabel>Description</ControlLabel>
            <FormControl 
              name="description" 
              componentClass="textarea" 
              rows={ 5 }
              value={ state.description } 
              onChange={ onChangeValue('description') } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <ButtonToolbar>
            <Button
              disabled={ !changed }
              onClick={ () => onUpdate(state) }
              appearance="primary"
              type="submit">Save changes</Button>
          </ButtonToolbar>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>

    <hr />

    <h5>Model Key Contacts</h5>
    <Members
      title={ "" }
      members={ model.members }
      roles={ [
        { value: "owner", label: "Owner" },
        { value: "sme", label: "Subject Matter Expert" },
        { value: "reviewer", label: "Reviewer" },
        { value: "ds", label: "Data Scientist" }
      ] }
      readOnly={ false }
      onGrant={ onGrant }
      onRevoke={ onRevoke } />
  </>;
}

ModelSettings.propTypes = {};

export default ModelSettings;
