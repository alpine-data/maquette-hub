/**
 *
 * CreateProjectForm
 *
 */
import _ from 'lodash';
import React, { useEffect } from 'react';
import PropTypes from 'prop-types';

import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, InputPicker, HelpBlock, ButtonToolbar, Button } from 'rsuite';

import { useFormState } from '../../utils/hooks';

function CreateProjectForm(props) {
  const [ state,, onChange ] = useFormState({
    title: '',
    name: '',
    owner: _.get(props, 'user.id'),
    summary: ''
  })

  useEffect(() => {
    onChange('name')(_.kebabCase(_.lowerCase(state.title)));
  }, [ state.title ])

  return <Form fluid>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Project Title</ControlLabel>
            <FormControl name="title" onChange={ onChange('title') } value={ state.title } />
            <HelpBlock>Select a speaking, memorable title for your project.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Owner</ControlLabel>
            <FormControl disabled name="owner" onChange={ onChange('owner') } value={ _.get(props, 'user.name') || state.owner } />
          </FormGroup>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Project Name</ControlLabel>
            <FormControl name="name" value={ state.name } onChange={ onChange('name') } />
            <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
          <FormGroup>
            <ControlLabel>Project Summary</ControlLabel>
            <FormControl name="summary" onChange={ onChange('summary') } value={ state.summary } />
          </FormGroup>
          <hr />
          <FormGroup>
            <ButtonToolbar>
              <Button 
                appearance="primary" 
                type="submit" 
                disabled={ !state.name.length > 0 && state.title.length > 0 }
                onClick={ () => {
                  props.onSubmit(state);
                } }>Create project</Button>
            </ButtonToolbar>
          </FormGroup>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>;
}

CreateProjectForm.propTypes = {
  user: PropTypes.object.isRequired,
  onSubmit: PropTypes.func.isRequired
};

CreateProjectForm.defaultProps = {
  user: {
    id: 'alice',
    name: 'Alice'
  },

  onSubmit: (data) => console.log(data)
}

export default CreateProjectForm;
