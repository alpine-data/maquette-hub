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

import { Icon, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, InputPicker, HelpBlock, ButtonToolbar, Button, Radio, RadioGroup } from 'rsuite';

class CreateProjectForm extends React.Component {

  constructor(props) {
    super(props);

    this.state = { 
      title: "",
      name: "",
      project: "props.user.id",
      description: "",
      owner: props.user.id
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
        "label": "foo-bar",
        "value": "foo-bar",
        "role": "Master"
      }
    ];

    return <Form fluid>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Dataset Title</ControlLabel>
            <FormControl name="title" onChange={ this.title_onChange } value={ this.state.title } />
            <HelpBlock>Select a speaking, memorable title for the dataset.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Project</ControlLabel>
            <InputPicker data={data} name="project" style={{ width: "100%" }} onChange={ this.project_onChange } value={ this.state.project } />
          </FormGroup>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Dataset Name</ControlLabel>
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
            <ControlLabel>Visibility</ControlLabel>
            <HelpBlock>The dataset metadata might be visible to allow other users to find the dataset. If the dataset is visible, the data is still not accessible for everyone.</HelpBlock>
            <RadioGroup name="privacy" style={{ paddingTop: "10px" }}>
              <Radio value="public"><b>Public</b> <br />The dataset and its metadata can be seen by everyone. The data is not public.</Radio>
              <Radio value="private"><b>Private</b><br />The dataset is entirely hidden from other users.</Radio>
            </RadioGroup>
          </FormGroup>

          <hr />

          <FormGroup>
            <ControlLabel>Data Classification</ControlLabel>
            <HelpBlock>Classify the contained data.</HelpBlock>

            <RadioGroup name="classification" style={{ paddingTop: "10px" }}>
              <Radio value="public"><b>Public</b> <br />The data is public information, it can be used by everyone, everywere for any purpose.</Radio>
              <Radio value="internal"><b>Internal</b><br />The dataset may contain company internal information. If disclosed, the impact to business is minimal.</Radio>
              <Radio value="confidential"><b>Confidential</b><br />The dataset may contain business critical internal information. If disclosed, business or brand could be negatively effected.</Radio>
              <Radio value="restricted"><b>Restricted</b><br />The dataset may contain highly sensitive information. If disclosed, it will have significant financial or legal impact.</Radio>
            </RadioGroup>
          </FormGroup>

          <hr />

          <FormGroup>
            <ControlLabel>Personal Information</ControlLabel>
            <HelpBlock>Specify whether the data may contain data points which could identify a natural person.</HelpBlock>

            <RadioGroup name="personal" style={{ paddingTop: "10px" }}>
              <Radio value="none"><b>None</b> <br />The data definetely does not contain any personal information.</Radio>
              <Radio value="pi"><b>Personal Information</b><br />The dataset may contain personal information.</Radio>
              <Radio value="spi"><b>Sensitive Personal Information</b><br />The dataset may contain sensitive personal information according to GDPR.</Radio>
            </RadioGroup>
          </FormGroup>

          <hr />

          <FormGroup>
            <ButtonToolbar>
              <Button 
                appearance="primary" 
                type="submit" 
                disabled={ !(this.state.name.length > 0 && this.state.title.length > 0) }
                onClick={ this.create_onClick }>Create dataset</Button>
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
