/**
 *
 * DynamicForm
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock, InputPicker } from 'rsuite';


const components = {
  "input": ({ control, ...props }) => {
    return <FormControl { ...props } />
  },
  "input-picker": ({ control, ...props }) => {
    return <InputPicker data={ control.items } cleanable={ false } style={{ width: "100%" }} { ...props } />
  }
}

function Control({ control, value, onChange }) {
  const Component = components[control.control.type] || FormControl;
  const controlValue = _.get(value, control.control.name) || '';

  const control_onChange = (controlValue) => {
    onChange(_.assign(value, { [control.control.name]: controlValue }));
  }

  return <FormGroup>
    <ControlLabel>{ control.label }</ControlLabel>
    <Component control={ control.control } value={ controlValue } onChange={ control_onChange } />
    { control.helpText && <HelpBlock>{ control.helpText }</HelpBlock> }
  </FormGroup>;
}

function Row({ row, value, onChange }) {
  const spaces = _.size(row.controls) - 1;
  const colspan = Math.floor((24 - spaces) / _.size(row.controls)); 

  return <>
    <FlexboxGrid justify="space-between">
    {
      _.map(row.controls, control => <FlexboxGrid.Item colspan={ colspan } key={ control.label } >
          <Control control={ control } value={ value } onChange={ onChange } />
        </FlexboxGrid.Item>)
    } 
    </FlexboxGrid>
    { row.separatorAfter && <hr /> }
  </>;
}

function DynamicForm({ form, value = {}, onChange = console.log }) {
  return <Form fluid>
    { form.helpText && <p className="mq--p-leading">{ form.helpText }</p> || <><br /></> }
    {
      _.map(form.fields, (row, idx) => <Row row={ row } key={ `row-${idx}` } value={ value } onChange={ onChange } />)
    }
  </Form>;
}

DynamicForm.propTypes = {
  form: PropTypes.object.isRequired,
  value: PropTypes.object.isRequired,

  onChange: PropTypes.func
};

export default DynamicForm;
