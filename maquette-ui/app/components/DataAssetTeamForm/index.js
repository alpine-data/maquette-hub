/**
 *
 * DataAssetTeamForm
 *
 */

import _ from 'lodash';
import React from 'react';
import { ControlLabel, FlexboxGrid, FormGroup, HelpBlock } from 'rsuite';
import UserInputPicker from '../UserInputPicker';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

export function transformProfilesToUsers({ users }) {
  return _.map(users, profile => {
    return {
      "label": profile.name,
      "value": profile.id,
      "user": profile
    }
  });
} 

export function initialState(data) {
  return _.pick(data, 'owner', 'steward');
}

export function validate(state) {
  return !_.isEmpty(state.owner) && !_.isEmpty(state.steward);
}

function DataAssetTeamForm({ state, users, onChange }) {
  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 24 }>
      <FormGroup>
        <ControlLabel>Responsibilities</ControlLabel>
        <HelpBlock>Every data asset must define at least one responsible data owner and one data steward. You may later add or change responsible team members.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>
    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Data Owner</ControlLabel>
        <UserInputPicker name="owner" value={ state.owner } onChange={ onChange('owner') } users={ users } />
        <HelpBlock>The data owner is accountable for the data quality and the proper way the data is used.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>
    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Data Steward</ControlLabel>
        <UserInputPicker name="steward" value={ state.steward } onChange={ onChange('steward') } users={ users } />
        <HelpBlock>The data steward is responsible for the quality of the data asset and acts as a subject matter expert for it.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>
  </FlexboxGrid>;
}

DataAssetTeamForm.propTypes = {};

DataAssetTeamForm.defaultProps = {
  users: [
    {
      "label": "Alice im Wunderland",
      "value": "alice",
      "user": {
        "avatar": "https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50",
        "name": "Alice im Wunderland",
        "title": "Senior Data Scientist"
      }
    },
    {
      "label": "Bert Bertelsmann",
      "value": "bert",
      "user": {
        "avatar": "https://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50",
        "name": "Bert Bertelsmann",
        "title": "Partner GRC"
      }
    }
  ]
}

export default DataAssetTeamForm;
