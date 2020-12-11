/**
 *
 * CreateStreamForm
 *
 */

import React, { useState } from 'react';
import PropTypes from 'prop-types';

import AceEditor from "react-ace";

import "ace-builds/src-noconflict/mode-json";
import "ace-builds/src-noconflict/theme-github";

import produce from 'immer';
import kebabcase from 'lodash.kebabcase';
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock, InputNumber, InputPicker } from 'rsuite';
import { DataClassificationFormGroup, PersonalInformtionFormGroup, VisibilityFormGroup } from '../CreateDatasetForm';

export function StreamSettingsForm({ state, onChange }) {
  const units = [
    {
      "value": "seconds",
      "label": "Seconds"
    },
    {
      "value": "minutes",
      "label": "Minutes"
    },
    {
      "value": "hours",
      "label": "Hours"
    },
    {
      "value": "days",
      "label": "Days"
    }
  ]

  return <>
    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Stream Retention <span className="mq--sub">(duration)</span></ControlLabel>
        <InputNumber name="retentionDuration" style={{ width: "100%" }} onChange={ onChange('retentionDuration') } value={ state.retentionDuration } />
        <HelpBlock>Select the duration how long messages should be retained in the stream.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Stream Retention <span className="mq--sub">(unit)</span></ControlLabel>
        <InputPicker data={ units } style={{ width: "100%" }} name="retentionUnit" onChange={ onChange('retentionUnit') } value={ state.retentionUnit } />
        <HelpBlock>Select the database type of your source.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <FormGroup>
        <ControlLabel>Schema <span className="mq--sub">(optional)</span></ControlLabel>
        <AceEditor
          mode="json"
          theme="github"
          value={ state.schema }
          onChange={ onChange('schema') }
          name="schema"
          editorProps={{ $blockScrolling: false }}
          height="200px"
          width="100%"
          placeholder={ JSON.stringify({
            "type": "record",
            "name": "LongList",
            "aliases": ["LinkedLongs"],
            "fields" : [
              {"name": "value", "type": "long"},
              {"name": "next", "type": ["null", "LongList"]}
            ]
          }, null, 3) }
        />
        <HelpBlock>Optionally define an Avro schema for messages of your stream.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>
  </>;
}

function CreateStreamForm(props) {
  const [state, setState] = useState({
    title: '',
    name: '',
    summary: '',
    visibility: 'public',
    classification: 'public',
    personalInformation: 'none',

    retentionDuration: '6',
    retentionUnit: 'hours'
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;

      if (field == 'personalInformation') {
        if (value == 'pi' && (state.classification == 'public' || state.classification == 'internal')) {
          draft.classification = 'confidential';
        } else if (value == 'spi' && state.classification != 'restricted') {
          draft.classification = 'restricted';
        }
      }

      if (field == 'title') {
        const id = kebabcase(value.toLowerCase());
        draft.title = value;
        draft.name = id;
      }
    }));
  }

  const createdDisabled = _.size(state.title) < 3 || _.size(state.name) < 3 || _.isEmpty(state.retentionDuration)

  return <Form fluid>
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>Stream Title</ControlLabel>
          <FormControl name="title" onChange={ onChange('title') } value={ state.title } />
          <HelpBlock>Select a speaking, memorable title for the stream.</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>Stream Name</ControlLabel>
          <FormControl name="name" value={ state.name } onChange={ onChange('name') } />
          <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <FormGroup>
          <ControlLabel>Stream Summary</ControlLabel>
          <FormControl name="summary" onChange={ onChange('summary') } value={ state.summary } />
          <HelpBlock>Describe in a few words which data is contained in your stream.</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <hr />
      </FlexboxGrid.Item>

      <StreamSettingsForm
        state={ state }
        onChange={ onChange } />

      <FlexboxGrid.Item colspan={ 24 }>
        <hr />   
        <VisibilityFormGroup value={ state.visibility } onChange={ onChange('visibility') } />
        <hr />
        <DataClassificationFormGroup value={ state.classification } onChange={ onChange('classification') } personalInformation={ state.personalInformation } />
        <hr />
        <PersonalInformtionFormGroup value={ state.personalInformation } onChange={ onChange('personalInformation') } />
        <hr />

        <FormGroup>
          <ButtonToolbar>
            <Button 
              appearance="primary" 
              type="submit" 
              disabled={ createdDisabled }
              loading={ _.get(props, 'createStream.creating') }
              onClick={ () => {
                const value = _.assign({}, _.omit(state, 'retentionDuration', 'retentionUnit'), { 
                  retention: {
                    unit: state.retentionUnit,
                    retention: state.retentionDuration * 1
                  },
                  schema: !_.isEmpty(state.schema) && JSON.parse(state.schema) || null
                });

                props.onCreateStream(value)
              } }>Create stream</Button>
          </ButtonToolbar>
        </FormGroup>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Form>;
}

CreateStreamForm.propTypes = {
  onCreateStream: PropTypes.func,
};

CreateStreamForm.defaultProps = {
  onCreateStream: console.log
}

export default CreateStreamForm;
