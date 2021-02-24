/**
 *
 * ProjectApplications
 *
 */

import React, { useState, useEffect } from 'react';
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, Icon, IconButton, Tooltip, Whisper } from 'rsuite';

import copy from 'copy-to-clipboard';
import { formatDate } from '../../utils/helpers';
import { useFormState } from '../../utils/hooks';

import Container from '../Container';
import ModernSummary, { TextMetric } from '../ModernSummary';



// import PropTypes from 'prop-types';
// import styled from 'styled-components';

function CreateApplication({ view, onUpdate }) {
  const [creating, setCreating] = useState(false);

  const initialState = {
    name: "",
    description: "",
    gitRepository: ""
  };

  const [state, setState, onChanged, onChangedValues] = useFormState(initialState);

  useEffect(() => {
    if (creating) {
      setCreating(false);
      setState(initialState);
    }
  }, [_.size(view.applications)])

  if (creating) {
    return <div style={{ marginBottom: '20px' }}>
      <Form fluid>
        <FlexboxGrid justify="space-between">
          <FlexboxGrid.Item colspan={ 11 }>
            <FormGroup>
              <ControlLabel>Application name</ControlLabel>
              <FormControl name="name" value={ state.name } onChange={ onChanged('name') } />
            </FormGroup>
          </FlexboxGrid.Item>

          <FlexboxGrid.Item colspan={ 11 }>
            <FormGroup>
              <ControlLabel>Description</ControlLabel>
              <FormControl name="description" value={ state.description } onChange={ onChanged('description') } />
            </FormGroup>
          </FlexboxGrid.Item>

          <FlexboxGrid.Item colspan={ 24 }>
            <ButtonToolbar>
              <Button
                onClick={ () => onUpdate('projects applications create', state) }
                color="green"
                type="submit">
                  Add application
              </Button>

              <Button
                onClick={ () => {
                  setCreating(false);
                  setState(initialState);
                } }>
                  Withdraw
              </Button>
            </ButtonToolbar>
          </FlexboxGrid.Item>
        </FlexboxGrid>
      </Form>
    </div>
  } else {
    return <>
    <div style={{ textAlign: 'right', marginBottom: '20px' }}>
      <Button color="green" onClick={ () => setCreating(true) }>Add application</Button>
    </div>
  </>
  }
}

function ListApplications({ view, onUpdate }) {
  if (_.isEmpty(view.applications)) {
    return <div style={{ textAlign: 'center' }}>
      This project has no applications yet. Applications manage access tokens (technical users) for accessing project resources, such as data assets or models.
    </div>
  } else {
    return <>
      {
        _.map(view.applications, app => <>
          <ModernSummary 
            key={ app.name }
            title={ app.name }
            tags={ [ app.description ] }
            metrics={ [
              <TextMetric 
                label="Id"
                value={ app.id } />,
              <TextMetric 
                label="Owner"
                value={ view.users[app.created.by].name } />,
              <TextMetric
                label="Updated"
                value={ formatDate(app.created.at) } />
            ] }
            metricColspan={ 4 }
            actionsColspan={ 3 }
            actions={
              <ButtonToolbar>
                <Whisper trigger="hover" speaker={ <Tooltip>Copy secret app token.</Tooltip> }>
                  <IconButton 
                    onClick={ () => copy(app.secret) }
                    icon={ <Icon icon="copy-o" /> } 
                    color="blue" />
                </Whisper>
                <IconButton 
                  icon={ <Icon icon="trash-o" />  } 
                  color="red"
                  onClick={ () => onUpdate('projects applications remove', { name: app.name }) } />
              </ButtonToolbar>
            } />
        </>)
      }
    </>
  }
}

function ProjectApplications({ view, onUpdate, ...props }) {
  return <Container lg>
    <CreateApplication view={ view } onUpdate={ onUpdate }  />
    <ListApplications view={ view } onUpdate={ onUpdate } />
  </Container>;
}

ProjectApplications.propTypes = {};

export default ProjectApplications;
