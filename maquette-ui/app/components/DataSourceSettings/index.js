/**
 *
 * DataSourceSettings
 *
 */

import React, { useState } from 'react';
import PropTypes from 'prop-types';
import produce from 'immer';

import { ButtonToolbar, Button, FlexboxGrid, Form, FormGroup, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

import Background from '../../resources/datashop-background.png';
import ResourceSettings from '../ResourceSettings';
import DataGovernanceOptions from '../DataGovernanceOptions';
import Members from '../Members';
import Error from '../Error';
import Container from '../Container';

/*
import { DataSourceSettings as DataSourceSettingsForm } from '../CreateDataSourceForm';
*/
function DataSourceSettingsForm() {
  return <></>;
}

function ConnectionSettings(props) {
  const initialState = {
    driver: _.get(props, 'dataSource.data.source.database.driver'),
    connection: _.get(props, 'dataSource.data.source.database.connection'),
    password: _.get(props, 'dataSource.data.source.database.password'),
    username: _.get(props, 'dataSource.data.source.database.username'),
    query: _.get(props, 'dataSource.data.source.database.query'),
    accessType: _.get(props, 'dataSource.data.source.accessType'),
    schedule: 'daily'
  };

  const [state, setState] = useState(initialState)

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  return <Form fluid>
    <FlexboxGrid justify="space-between">
      <DataSourceSettingsForm 
        state={ state } 
        testing={ _.get(props, 'dataSource.testing') } 
        testResult={ _.get(props, 'dataSource.testResult') || {} }
        onTestConnection={ props.onTestConnection } 
        onChange={ onChange } />

      <FlexboxGrid.Item colspan={ 24 }>
        <FormGroup>
          <ButtonToolbar>
            <Button 
              appearance="primary" 
              type="submit" 
              disabled={ _.isEqual(state, initialState) }
              loading={ _.get(props, 'dataSource.updating') }
              onClick={ () => {
                props.onUpdateDatabaseProperties({
                  driver: state.driver,
                  connection: state.connection,
                  username: state.username,
                  password: state.password,
                  query: state.query,
                  accessType: state.accessType,
                })
              } }>Save changes</Button>
            </ButtonToolbar>
          </FormGroup>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Form>;
}

function DataSourceSettings(props) {
  const source = _.get(props, 'match.params.source');
  const tab = _.get(props, 'match.params.id') || 'options';

  const isOwner = _.get(props, 'dataSource.data.isOwner');

  if (isOwner) {
    return <Container xlg className="" background={ Background }>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={4}>
          <Nav vertical activeKey={ tab } appearance="subtle">
            <Nav.Item eventKey="options" componentClass={ Link } to={ `/shop/sources/${source}/settings` }>Options</Nav.Item>
            <Nav.Item eventKey="source" componentClass={ Link } to={ `/shop/sources/${source}/settings/source` }>Data Source</Nav.Item>
            <Nav.Item eventKey="governance" componentClass={ Link } to={ `/shop/sources/${source}/settings/governance` }>Governance</Nav.Item>
            <Nav.Item eventKey="members" componentClass={ Link } to={ `/shop/sources/${source}/settings/members` }>Members</Nav.Item>
          </Nav>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
          { 
            tab == 'options' && <>
              <ResourceSettings 
                resource="Data Source"
                title={ _.get(props, 'dataSource.data.source.title') }
                name={ _.get(props, 'dataSource.data.source.name') }
                onUpdate={ (title, name) => props.onUpdate({ title, name }) }
              />
            </> 
          }

          { 
            tab == 'source' && <>
              <ConnectionSettings { ...props } />
            </> 
          }

          { 
            tab == 'governance' && <>
              <DataGovernanceOptions 
                visibility={ _.get(props, 'dataSource.data.source.visibility') }
                classification={ _.get(props, 'dataSource.data.source.classification') }
                personalInformation={ _.get(props, 'dataSource.data.source.personalInformation') }
                onUpdate={ (visibility, classification, personalInformation) => props.onUpdate({ visibility, classification, personalInformation }) } />
            </>
          }

          {
            tab == 'members' && <>
              <Members
                title="Manage members"
                members={ _.get(props, 'dataSource.data.source.members') }
                roles={ [ 
                  { value: "consumer", label: "Consumer" },
                  { value: "owner", label: "Owner" }
                ] }
                onGrant={ props.onGrant }
                onRevoke={ props.onRevoke }
                />
            </>
          }
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Container>
  } else {
    return <Error 
      background={ Background }
      message="You are not authorized to view the settings of this asset." />
  }
}

DataSourceSettings.propTypes = {
  onUpdateDatabaseProperties: PropTypes.func,
  onTestConnection: PropTypes.func
};

DataSourceSettings.defaultProps = {
  onUpdateDatabaseProperties: console.log,
  onTestConnection: console.log
}

export default DataSourceSettings;
