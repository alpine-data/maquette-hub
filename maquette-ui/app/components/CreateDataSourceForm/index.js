/**
 *
 * CreateDataSourceForm
 *
 */

import React, {useState} from 'react';
import PropTypes from 'prop-types';

import produce from 'immer';
import kebabcase from 'lodash.kebabcase';

import { ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, InputPicker, HelpBlock, ButtonToolbar, Button, Radio, RadioGroup, Message } from 'rsuite';
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

export function DataSourceSettings({ state, testing, testResult, onChange, onTestConnection }) {
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

  const testConnectionDisabled = _.isEmpty(state.connection) || _.isEmpty(state.query) || _.isEmpty(state.username) || _.isEmpty(state.password);

  return <>
    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Data Source Driver</ControlLabel>
        <InputPicker data={ drivers } style={{ width: "100%" }} onChange={ onChange('driver') } value={ state.driver } />
        <HelpBlock>Select the database type of your source.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>
      

    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Connection String</ControlLabel>
        <FormControl 
          name="connection" 
          value={ state.connection } 
          onChange={ onChange('connection') }
          placeholder="//host:port/database" />
        <HelpBlock>A JDBC connection string to connect to the database, including username and password.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Username</ControlLabel>
        <FormControl 
          name="username" 
          value={ state.username } 
          onChange={ onChange('username') } />
      </FormGroup>
    </FlexboxGrid.Item>
      

    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Password</ControlLabel>
        <FormControl 
          name="password" 
          value={ state.password } 
          onChange={ onChange('password') }
          type="password" />
      </FormGroup>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <FormGroup>
        <ControlLabel>Query</ControlLabel>
        <FormControl 
          name="connection" 
          value={ state.query } 
          onChange={ onChange('query') }
          placeholder="SELECT * FROM <DATABASE>.<TABLE_NAME>" />
        <HelpBlock>The query to execute to fetch the data from the database.</HelpBlock>
      </FormGroup>
      <FormGroup>
        <ButtonToolbar>
          <Button 
            disabled={ testConnectionDisabled }
            color="green"
            loading={ testing }
            onClick={ () => {
              onTestConnection({
                driver: state.driver,
                connection: state.connection,
                username: state.username,
                password: state.password,
                query: state.query
              })
            }}>
              Test connection and query.
          </Button>
        </ButtonToolbar>
      </FormGroup>

      {
        testResult.result == 'success' && <>
          <Message type="success" description="Connection has been tested successfully" closable />
        </>
      }

      {
        testResult.result == 'failed' && <>
          <Message type="error" description={ testResult.message } closable />
        </>
      }
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <hr />
      <AccessTypeForm value={ state.accessType } onChange={ onChange('accessType') } />
      <ScheduleForm value={ state.schedule } onChange={ onChange('schedule')} accessType={ state.accessType } />
    </FlexboxGrid.Item>
  </>
}

function CreateDataSourceForm(props) {
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
    connection: '',
    query: '',
    username: '',
    password: '',
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

  const testConnectionDisabled = _.isEmpty(state.connection) || _.isEmpty(state.query) || _.isEmpty(state.username) || _.isEmpty(state.password);
  const createdDisabled = _.size(state.title) < 3 || _.size(state.name) < 3 || testConnectionDisabled;

  return <Form fluid>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>Data Source Title</ControlLabel>
            <FormControl name="title" onChange={ onChange('title') } value={ state.title } />
            <HelpBlock>Select a speaking, memorable title for the data source.</HelpBlock>
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
            <HelpBlock>Describe in a few words which data is contained in your data source.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
        </FlexboxGrid.Item>

        <DataSourceSettings 
          state={ state } 
          testing={ _.get(props, 'createDataSource.testing') } 
          testResult={ _.get(props, 'createDataSource.testResult') }
          onTestConnection={ props.onTestConnection } 
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
                loading={ _.get(props, 'createDataSource.creating') }
                onClick={ () => {
                  props.onCreateDataSource({
                    title: state.title,
                    name: state.name,
                    summary: state.summary,
                    properties: {
                      driver: state.driver,
                      connection: state.connection,
                      username: state.username,
                      password: state.password,
                      query: state.query
                    },
                    accessType: state.accessType,
                    visibility: state.visibility,
                    classification: state.classification,
                    personalInformation: state.personalInformation
                  })
                } }>Create data source</Button>
            </ButtonToolbar>
          </FormGroup>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>;
}


CreateDataSourceForm.propTypes = {
  createDataSource: PropTypes.shape({
    creating: PropTypes.bool,
    testing: PropTypes.bool,
    testResult: PropTypes.oneOfType([
      PropTypes.bool, 
      PropTypes.shape({
        result: PropTypes.string,
        message: PropTypes.string
      }) 
    ])
  }),

  onCreateDataSource: PropTypes.func,
  onTestConnection: PropTypes.func,
};

CreateDataSourceForm.defaultProps = {
  testing: false,
  testResult: false,

  onCreateDataSource: console.log,
  onTestConnection: console.log
}

export default CreateDataSourceForm;
