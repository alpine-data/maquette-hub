/**
 *
 * StackConfigurationForm
 *
 */
import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import { Button, FlexboxGrid, Grid } from 'rsuite';

import StackCard, { StackIcon } from '../StackCard';
import DynamicForm from '../DynamicForm';
// import styled from 'styled-components';

function SelectStack(props) {
  const select = (stack) => () => {
    props.onChange(_.assign({ stack: stack.name }, stack.configurationForm.defaults));
  }

  return <>
    <h5 className="mq--sub">Select a stack to add</h5>
    <FlexboxGrid justify="space-between">
      {
        _.map(
          props.stacks, 
          s => <FlexboxGrid.Item colspan={ 11 } key={ s.name }>
              <StackCard stack={ s } actionLabel="Select this stack" onClick={ select(s) } />
          </FlexboxGrid.Item>
        )
      }
    </FlexboxGrid>
  </>;
}

function ConfigureStack(props) {
  const stack = _.find(props.stacks, s => s.name == props.value.stack);
  
  return <>
    <FlexboxGrid align="middle">
      <FlexboxGrid.Item>
        <StackIcon stack={ stack } style={{ marginRight: "20px" }} width={ 48 } />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item>
        <h5>{ stack.title }<br /><span className="mq--sub" style={{ fontWeight: "normal" }}>stack configuration</span></h5>
      </FlexboxGrid.Item>
    </FlexboxGrid>
    
    <DynamicForm form={ stack.configurationForm } value={ props.value } onChange={ props.onChange } />

    <Button onClick={ () => props.onChange({}) } appearance="ghost">Remove Stack</Button>
  </>
}

function StackConfigurationForm(props) {
  const selectedStack = _.get(props, 'value.stack') || false;

  return <>
    { selectedStack && <ConfigureStack { ...props } /> || <SelectStack { ...props } /> }
  </>;
}

StackConfigurationForm.defaultProps = {
  onChange: console.log
}

StackConfigurationForm.propTypes = {
  stacks: PropTypes.arrayOf(PropTypes.shape({
    configurationForm: PropTypes.object.isRequired,
    icon: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    summary: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    tags: PropTypes.arrayOf(PropTypes.string).isRequired
  })).isRequired,

  value: PropTypes.shape({
    stack: PropTypes.string,
    configuration: PropTypes.object
  }),

  onChange: PropTypes.func,
  onRemove: PropTypes.func
};

export default StackConfigurationForm;
