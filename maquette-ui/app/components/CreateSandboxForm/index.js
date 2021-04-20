/**
 *
 * CreateSandboxForm
 *
 */
import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import { produce } from 'immer';

import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock, Input, Radio, RadioGroup, SelectPicker} from 'rsuite';
import StackConfigurationForm from '../StackConfigurationForm';

import { useFormState } from '../../utils/hooks';

function CreateSandboxForm(props) {  
  const repositories = _.map(_.get(props, 'gitRepositories'), repo => {
    return { label: repo, value: repo }
  });

  const volumes = _.map(_.get(props, 'volumes'), volume => {
    return { label: volume.name, value: volume.id }
  });

  const [state, setState, onChange, onChangeValues] = useFormState({
    name: _.get(props, 'randomName') || '',
    project: _.get(props, 'project.name') || '',
    stacks: [ ]
  });

  const [volume,, onVolumeChange] = useFormState({
    type: 'plain',
    name: _.get(props, 'randomName') + '_volume',
    repository: _.size(repositories) > 0 ? repositories[0].value : '',
    id: _.size(volumes) > 0 ? repositories[0].id : ''
  })

  const stacks_onChange = (idx) => (value) => {
    setState(produce(state, draft => {
      if (!_.isEmpty(value)) {
        draft.stacks.splice(idx, 1);
        draft.stacks.splice(idx, 0, value);
      } else {
        draft.stacks.splice(idx, 1);
      }
    }));
  }

  const addNewStack = !_.find(state.stacks, s => !s.stack)
  const selectedStacks = _.map(state.stacks, s => s.stack);
  const availableStacks = _.filter(props.stacks, s => !_.includes(selectedStacks, s.name))

  const validate = () => {
    let result = true;

    if (_.isEmpty(state.name)) result = false;
    if (_.isEmpty(state.project)) result = false;
    if (_.isEmpty(state.stacks)) result = false;

    if (volume.type === 'plain') {
      if (_.isEmpty(volume.name)) result = false;
    } else if (volume.type === 'git') {
      if (_.isEmpty(volume.name)) result = false;
      if (_.isEmpty(volume.repository)) result = false;
    } else if (volume.type === 'existing') {
      if (_.isEmpty(volume.id)) result = false;
    }

    return result;
  }

  return <>
    <Form fluid>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Project</ControlLabel>
            <Input disabled={ true } value={ _.get(props, 'project.name') } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Sandbox Name</ControlLabel>
            <FormControl onChange={ onChange('name') } value={ state.name } />
            <HelpBlock>Select a speaking title for your sandbox environment.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Volume</ControlLabel>
            <HelpBlock>The volume is mounted into the stacks containers so that you can access and share data and project files.</HelpBlock>

            <RadioGroup name="volume_type" value={ _.get(volume, 'type') } onChange={ onVolumeChange('type') } style={{ paddingTop: "10px" }}>
              <Radio value="plain"><b>Plain</b> Setup a new plain volume.</Radio>
              <Radio value="git"><b>Git Repository</b> Initialize volume with data from a git repository.</Radio>
              <Radio value="existing"><b>Existing</b> Reuse an existing volume.</Radio>
            </RadioGroup>
          </FormGroup>
        </FlexboxGrid.Item>

        {
          _.get(volume, 'type') == 'plain' && <>
            <FlexboxGrid.Item colspan={ 11 }>
              <FormGroup>
                <ControlLabel>Volume Name</ControlLabel>
                <FormControl onChange={ onVolumeChange('name') } value={ _.get(volume, 'name') } />
                <HelpBlock>Select a speaking name for the volume.</HelpBlock>
              </FormGroup>
            </FlexboxGrid.Item>
          </>
        }

        {
          _.get(volume, 'type') == 'git' && <>
            <FlexboxGrid.Item colspan={ 11 }>
              <FormGroup>
                <ControlLabel>Volume Name</ControlLabel>
                <FormControl onChange={ onVolumeChange('name') } value={ _.get(volume, 'name') } />
                <HelpBlock>Select a speaking name for the volume.</HelpBlock>
              </FormGroup>
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 11 }>
              <FormGroup>
                <ControlLabel>Git Repository</ControlLabel>
                <SelectPicker 
                  data={ repositories } 
                  onChange={ onVolumeChange('repository') } 
                  value={ _.get(volume, 'repository') }
                  style={{ width: '100%' }} />
                <HelpBlock>Select from your accessible Git repositories.</HelpBlock>
              </FormGroup>
            </FlexboxGrid.Item>
          </>
        }

        {
          _.get(volume, 'type') == 'existing' && <>
            <FlexboxGrid.Item colspan={ 11 }>
              <FormGroup>
                <ControlLabel>Select volume</ControlLabel>
                <SelectPicker 
                  data={ volumes } 
                  onChange={ onVolumeChange('id') } 
                  value={ _.get(volume, 'id') }
                  style={{ width: '100%' }} />
                <HelpBlock>Select an existing volume.</HelpBlock>
              </FormGroup>
            </FlexboxGrid.Item>
          </>
        }
      </FlexboxGrid>
    </Form>
    <hr />
    <>
      {
        _.map(state.stacks, (s, idx) => <React.Fragment key={s}>
          <StackConfigurationForm
            key={ `stack-${idx}` }
            value={ s }
            stacks={ props.stacks }
            onChange={ stacks_onChange(idx) } />
          <hr />
        </React.Fragment>)
      }
    </>
    {
      addNewStack && !_.isEmpty(availableStacks) && <>
        <StackConfigurationForm 
          key={ `new-${_.size(state.stacks)}` }
          value={ {} } 
          stacks={ availableStacks } 
          onChange={ stacks_onChange(_.size(state.stacks)) } />
        <hr />
      </>
    }

    <ButtonToolbar>
      <Button 
        appearance="primary" 
        disabled={ !validate() }
        onClick={ () => {
          let v = { type: volume.type };

          if (volume.type === 'plain') {
            v = _.assign(v, _.pick(volume, 'name'));
          } else if (volume.type === 'git') {
            v = _.assign(v, _.pick(volume, 'name', 'repository'));
          } else if (volume.type === 'existing') {
            v = _.assign(v, _.pick(volume, 'id'));
          }

          const request = _.assign({}, state, { volume: v })
          props.onSubmit(request) 
        } }>Create Sandbox</Button>
    </ButtonToolbar>
  </>;
}

CreateSandboxForm.propTypes = {
  onSubmit: PropTypes.func
};

CreateSandboxForm.defaultProps = {
  onSubmit: console.log
}

export default CreateSandboxForm;
