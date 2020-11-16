/**
 *
 * ResourceSettings
 *
 */

import React, { useState } from 'react';
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup } from 'rsuite';
import PropTypes from 'prop-types';
import { produce } from 'immer';

function ResourceSettings({ resource = "Project", title, name, onUpdate }) {
  const [state, setState] = useState({
    title: title,
    name: name,
    delete_title: ''
  });

  const onChanged = (field) => (value) => {
    return setState(produce(state, draft => {
      draft[field] = value
    }));
  }

  const changed = state.title != title || state.name != name;

  return <>
    <h4>Settings</h4>
    <Form fluid>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={11}>
          <FormGroup>
            <ControlLabel>{ resource } Title</ControlLabel>
            <FormControl name="title" value={ state.title } onChange={ onChanged('title') } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 2 } />

        <FlexboxGrid.Item colspan={11}>
          <FormGroup>
            <ControlLabel>{ resource } Name</ControlLabel>
            <FormControl name="name" value={ state.name } onChange={ onChanged('name') } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <ButtonToolbar>
            <Button 
              disabled={ !changed }
              onClick={ () => onUpdate(state.title, state.name) }
              appearance="primary" 
              type="submit">Update { resource }</Button>
          </ButtonToolbar>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>
    <hr />

    <h4>Delete { resource }</h4>
    <Form fluid>
      <FormGroup>
        <ControlLabel>Type { resource } Name</ControlLabel>
        <FormControl name="repeat-name" value={ state.delete_title } onChange={ onChanged('delete_title') } />
      </FormGroup>

      <ButtonToolbar>
        <Button 
          appearance="primary" 
          color="red"
          disabled={ state.delete_title != name }
          type="submit">Delete { resource }</Button>
      </ButtonToolbar>
    </Form>
  </>;
}

ResourceSettings.propTypes = {
  resource: PropTypes.string,
  name: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,

  onUpdate: PropTypes.func
};

export default ResourceSettings;
