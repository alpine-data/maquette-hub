/**
 *
 * StreamPropertiesForm
 *
 */

import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import AceEditor from "react-ace";
import "ace-builds/src-noconflict/mode-json";
import "ace-builds/src-noconflict/theme-github";

import { ControlLabel, FlexboxGrid, FormGroup, HelpBlock, InputNumber, InputPicker } from 'rsuite';

export const initialState = {
  retentionDuration: 6,
  retentionUnit: 'hours',
  schema: {
    "type": "record",
    "name": "LongList",
    "aliases": ["LinkedLongs"],
    "fields" : [
      {"name": "value", "type": "long"},
      {"name": "next", "type": ["null", "LongList"]}
    ]
  }
}

export const validate = (state) => {
  return !(_.isEmpty(state.retentionUnit) || _.isEmpty(state.schema));
}

function StreamPropertiesForm({ state, units, onChange }) {
  const [schemaStr, setSchemaStr] = useState('');

  useEffect(() => {
    setSchemaStr(JSON.stringify(state.schema, null, 3));
  }, state.schema)

  return <FlexboxGrid justify="space-between">
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
          value={ schemaStr }
          onChange={ value => setSchemaStr(value) }
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
          onBlur={ () => {
            try {
              const json = JSON.parse(schemaStr);
              onChange('schema')(json);
            } catch (err) {
              onChange('schema')({});
            }
          }}
        />
        <HelpBlock>Optionally define an Avro schema for messages of your stream.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>
  </FlexboxGrid>;
}

StreamPropertiesForm.propTypes = {
  units: PropTypes.array
};

StreamPropertiesForm.defaultProps = {
  units: [
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
}

export default StreamPropertiesForm;
