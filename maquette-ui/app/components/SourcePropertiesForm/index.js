/**
 *
 * SourcePropertiesForm
 *
 */

import React from 'react';
import PropTypes from 'prop-types';

import { produce } from 'immer'
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, FormControl, FormGroup, HelpBlock, InputPicker, Message, Radio, RadioGroup } from 'rsuite';

import actions from './actions';
import constants from './constants';
import reducer from './reducer';
import saga from './saga';
import selector from './selectors';

export const createActions = actions;
export const createConstants = constants;
export const createReducer = reducer;
export const createSaga = saga;
export const createSelector = selector;

export const initialState = {
  driver: 'postgresql',
  connection: '',
  username: '',
  password: '',
  query: '',
  accessType: 'direct',
  schedule: 'daily'
}

export const validate = (state) => {
  return !(_.isEmpty(state.connection) || _.isEmpty(state.query) || _.isEmpty(state.username) || _.isEmpty(state.password));
}

function SourcePropertiesForm({ container, drivers, state, onChange, reducer, saga, ...props }) {
  const isTesting = _.get(props, `${container}.isTesting`);
  const testResult = _.get(props, `${container}.testResult`) || {};
  const testConnectionDisabled = !validate(state);

  const { testConnection } = actions(container);

  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Data Source Driver</ControlLabel>
        <InputPicker 
          name="driver"
          data={ drivers } 
          style={{ width: '100%' }} 
          onChange={ onChange('driver') } 
          value={ _.get(state, 'properties.driver') } />
        <HelpBlock>Select the database type of your source.</HelpBlock>
      </FormGroup>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Connection String</ControlLabel>
        <FormControl 
          name="connection" 
          value={ _.get(state, 'connection') } 
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
          value={ _.get(state, 'username') } 
          onChange={ onChange('username') } />
      </FormGroup>
    </FlexboxGrid.Item>
      

    <FlexboxGrid.Item colspan={ 11 }>
      <FormGroup>
        <ControlLabel>Password</ControlLabel>
        <FormControl 
          name="password" 
          value={ _.get(state, 'password') } 
          onChange={ onChange('password') }
          type="password" />
      </FormGroup>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <FormGroup>
        <ControlLabel>Query</ControlLabel>
        <FormControl 
          name="connection" 
          value={ _.get(state, 'query') } 
          onChange={ onChange('query') }
          placeholder="SELECT * FROM <DATABASE>.<TABLE_NAME>" />
        <HelpBlock>The query to execute to fetch the data from the database.</HelpBlock>
      </FormGroup>

      <FormGroup>
        <ButtonToolbar>
          <Button 
            disabled={ testConnectionDisabled }
            color="green"
            loading={ isTesting }
            onClick={ () => props.dispatch(testConnection(_.pick(state, 'driver', 'connection', 'query', 'username', 'password'))) }>
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
          <Message type="error" description={ _.get(testResult, 'message') } closable />
        </>
      }
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <hr />

      <FormGroup>
        <ControlLabel>Access Type</ControlLabel>
        <HelpBlock>You can allow direct `passthrough` access to the database. Or you can schedule the access. In the 2nd case, Maquette will regularly fetch data from the database to cache it for consumers which ask for the data.</HelpBlock>
        <RadioGroup name="accessType" value={ state.accessType } onChange={ onChange('accessType') } style={{ paddingTop: "10px" }}>
          <Radio value="direct"><b>Direct</b> <br />Every consumer access is directly passed and processed by the database.</Radio>
          <Radio value="cached"><b>Cached</b><br />Access to the database only happens within a defined schedule. Data will be cached to allow consumers to retrieve data at any time.</Radio>
        </RadioGroup>
      </FormGroup>

      {
        state.accessType === 'cached' && <>
          <FormGroup>
            <ControlLabel>Schedule</ControlLabel>
            <HelpBlock>You can define how often the data is fetched.</HelpBlock>
            <RadioGroup name="accessType" value={ state.schedule } onChange={ onChange('schedule') } style={{ paddingTop: "10px" }}>
              <Radio value="weekly"><b>Weekly</b> <br />Every Sunday at 2.00 am.</Radio>
              <Radio value="daily"><b>Daily</b> <br />Every day at 2.00 am.</Radio>
              <Radio value="half-daily"><b>Midnight &amp; Noon</b> <br />Every day at midnight and moon.</Radio>
              <Radio value="hourly"><b>Every Hour</b><br />Every hour throughout a day.</Radio>
            </RadioGroup>
          </FormGroup>
        </>
      }
    </FlexboxGrid.Item>
  </FlexboxGrid>;
}

SourcePropertiesForm.propTypes = {
  drivers: PropTypes.arrayOf(PropTypes.shape({
    value: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired
  })),

  isTesting: PropTypes.bool.isRequired,
  testResult: PropTypes.shape({
    result: PropTypes.string
  }),

  state: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  onTestConnection: PropTypes.func.isRequired,
};

SourcePropertiesForm.defaultProps = {
  drivers: [
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
  ],

  isTesting: false,
  testResult: {},

  onChange: console.log,
  onTestConnection: console.log
}

export default SourcePropertiesForm;
