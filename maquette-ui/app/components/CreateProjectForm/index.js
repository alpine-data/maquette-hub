/**
 *
 * CreateProjectForm
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
// import styled from 'styled-components';

import produce from 'immer';
import kebabcase from 'lodash.kebabcase';

import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, InputPicker, HelpBlock, ButtonToolbar, Button } from 'rsuite';

class CreateProjectForm extends React.Component {

  constructor(props) {
    super(props);
    this.state = { 
      title: "",
      name: "",
      owner: props.user.id,
      description: ""
    };  

    this.create_onClick = this.create_onClick.bind(this);
    this.owner_onChange = this.owner_onChange.bind(this);
    this.name_onChange = this.name_onChange.bind(this);
    this.title_onChange= this.title_onChange.bind(this);
    this.description_onChange = this.description_onChange.bind(this);
  }

  create_onClick() {
    this.props.onSubmit({ 
      title: this.state.title, 
      name: this.state.name, 
      owner: this.state.owner, 
      description: this.state.description 
    });
  }

  owner_onChange(value) {
    console.log(value);
  }

  description_onChange(value) {
    this.setState(produce(this.state, draft => {
      draft.description = value;
    }));
  }

  name_onChange(value) {
    this.setState(produce(this.state, draft => {
      draft.name = value;
    }));
  }

  title_onChange(value) {
    const id = kebabcase(value.toLowerCase());

    this.setState(produce(this.state, (draft) => { 
      draft.title = value;
      draft.name = id;
    }));
  }

  render() {
    const data = [
      {
        "label": this.props.user.name,
        "value": this.props.user.id,
        "role": "Master"
      }
    ];

    return <Form fluid>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Project Title</ControlLabel>
            <FormControl name="title" onChange={ this.title_onChange } value={ this.state.title } />
            <HelpBlock>Select a speaking, memorable title for your project.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Owner</ControlLabel>
            <InputPicker data={data} name="owner" style={{ width: "100%" }} onChange={ this.owner_onChange } value={ this.state.owner } />
          </FormGroup>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Project Name</ControlLabel>
            <FormControl name="name" value={ this.state.name } onChange={ this.name_onChange } />
            <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
          <FormGroup>
            <ControlLabel>Short Description</ControlLabel>
            <FormControl name="description" onChange={ this.description_onChange } value={ this.state.description } />
          </FormGroup>
          <hr />
          <FormGroup>
            <ButtonToolbar>
              <Button 
                appearance="primary" 
                type="submit" 
                disabled={ !(this.state.name.length > 0 && this.state.title.length > 0) }
                onClick={ this.create_onClick }>Create project</Button>
            </ButtonToolbar>
          </FormGroup>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>;
  }
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
