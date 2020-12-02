/**
 *
 * CreateDataSourceForm
 *
 */

import React, {useState} from 'react';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import produce from 'immer';
import kebabcase from 'lodash.kebabcase';

import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, InputPicker, HelpBlock, ButtonToolbar, Button, Radio, RadioGroup } from 'rsuite';
import { DataClassificationFormGroup, PersonalInformtionFormGroup, VisibilityFormGroup } from '../CreateDatasetForm';

function AccessTypeForm({ value, onChange }) {
  return <FormGroup>
    <ControlLabel>Access Type</ControlLabel>
    <HelpBlock>You can allow direct `passthrough` access to the database. Or you can schedule the access. In the 2nd case, Maquette will regularly fetch data from the database to cache it for consumers which ask for the data.</HelpBlock>
    <RadioGroup name="schedule" value={ value } onChange={ onChange } style={{ paddingTop: "10px" }}>
      <Radio value="direct"><b>Direct</b> <br />Every consumer access is directly passed and processed by the database.</Radio>
      <Radio value="cached"><b>Cached</b><br />Access to the database only happens within a defined schedule. Data will be cached to allow consumers to retrieve data at any time.</Radio>
    </RadioGroup>
  </FormGroup>;
}

function ScheduleForm({ value, onChange, accessType }) {
  if (accessType == "direct") {
    return <></>;
  } else {
    return <FormGroup>
    <ControlLabel>Schedule</ControlLabel>
    <HelpBlock>You can define how often the data is fetched.</HelpBlock>
    <RadioGroup name="accessType" value={ value } onChange={ onChange } style={{ paddingTop: "10px" }}>
      <Radio value="weekly"><b>Weekly</b> <br />Every Sunday at 2.00 am.</Radio>
      <Radio value="daily"><b>Daily</b> <br />Every day at 2.00 am.</Radio>
      <Radio value="half-daily"><b>Midnight &amp; Noon</b> <br />Every day at midnight and moon.</Radio>
      <Radio value="hourly"><b>Every Hour</b><br />Every hour throughout a day.</Radio>
    </RadioGroup>
  </FormGroup>;
  }
}

function CreateDataSourceForm() {
  const drivers = [
    {
      "value": "postgresql",
      "label": "PostgreSQL"
    },
    {
      "value": "oracle",
      "label": "Oracle 18c"
    },
    {
      "value": "db2",
      "label": "Db2"
    }
  ]

  const [state, setState] = useState({
    title: '',
    name: '',
    summary: '',
    visibility: 'public',
    classification: 'public',
    personalInformation: 'none',
    accessType: 'direct',
    schedule: 'daily',
    driver: 'postgresql',
    query: 'SELECT * FROM <DATABASE>.<TABLE_NAME>'
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

  const isValid = state.title.length > 3 && state.name.length > 3;

  return <Form fluid>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Data Source Title</ControlLabel>
            <FormControl name="title" onChange={ onChange('title') } value={ state.title } />
            <HelpBlock>Select a speaking, memorable title for the dataset.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Data Source Name</ControlLabel>
            <FormControl name="name" value={ state.name } onChange={ onChange('name') } />
            <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Data Source Summary</ControlLabel>
            <FormControl name="summary" onChange={ onChange('summary') } value={ state.summary } />
            <HelpBlock>Describe in a few words which data is contained in your dataset.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Data Source Driver</ControlLabel>
            <InputPicker data={ drivers } style={{ width: "100%" }} onChange={ onChange('driver') } value={ state.driver } />
            <HelpBlock>Describe in a few words which data is contained in your dataset.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>
          

        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Connection String</ControlLabel>
            <FormControl name="connection" value={ state.connection } onChange={ onChange('connection') } />
            <HelpBlock>A JDBC connection string to connect to the database, including username and password.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
          <AccessTypeForm value={ state.accessType } onChange={ onChange('accessType') } />
          <ScheduleForm value={ state.schedule } onChange={ onChange('schedule')} accessType={ state.accessType } />
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
                disabled={ !isValid }
                onClick={ () => onSubmit(state) }>Create data source</Button>
            </ButtonToolbar>
          </FormGroup>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>;
}

CreateDataSourceForm.propTypes = {};

export default CreateDataSourceForm;
