/**
 *
 * StreamSettings
 *
 */
import _ from 'lodash';
import React, { useState } from 'react';
import produce from 'immer';

import { Button, ButtonToolbar, FlexboxGrid, Form, FormGroup, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

import Background from '../../resources/datashop-background.png';
import Container from '../Container';
import ResourceSettings from '../ResourceSettings';
import DataGovernanceOptions from '../DataGovernanceOptions';
import Members from '../Members';
import Error from '../Error';

import { StreamSettingsForm } from '../CreateStreamForm'

function StreamConfiguration(props) {
  const schemaJson = _.get(props, 'stream.data.stream.schema');
  const initialState = {
    retentionDuration: _.get(props, 'stream.data.stream.retention.retention'),
    retentionUnit: _.get(props, 'stream.data.stream.retention.unit'),
    schema: schemaJson && JSON.stringify(schemaJson) || ''
  };

  const [state, setState] = useState(initialState)

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  return <Form fluid>
    <FlexboxGrid justify="space-between">
      <StreamSettingsForm
        state={ state }
        onChange={ onChange }
      />

      <FlexboxGrid.Item colspan={ 24 }>
        <FormGroup>
          <ButtonToolbar>
          <Button 
              appearance="primary" 
              type="submit" 
              disabled={ _.isEqual(state, initialState) }
              loading={ _.get(props, 'dataSource.updating') }
              onClick={ () => {
                const value = _.assign({}, _.omit(state, 'retentionDuration', 'retentionUnit'), { 
                  retention: {
                    unit: state.retentionUnit,
                    retention: state.retentionDuration * 1
                  },
                  schema: !_.isEmpty(state.schema) && JSON.parse(state.schema) || null
                });

                props.onUpdate(value)
              } }>Save changes</Button>
          </ButtonToolbar>
        </FormGroup>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Form>
}

function StreamSettings(props) {
  const stream = _.get(props, 'match.params.stream');
  const tab = _.get(props, 'match.params.id') || 'options'

  const isOwner = _.get(props, 'stream.data.isOwner');
  
  if (isOwner) {
    return <Container xlg className="mq--main-content" background={ Background }>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={4}>
          <Nav vertical activeKey={ tab } appearance="subtle">
            <Nav.Item eventKey="options" componentClass={ Link } to={ `/shop/streams/${stream}/settings` }>Options</Nav.Item>
            <Nav.Item eventKey="stream" componentClass={ Link } to={ `/shop/streams/${stream}/settings/stream` }>Stream</Nav.Item>
            <Nav.Item eventKey="governance" componentClass={ Link } to={ `/shop/streams/${stream}/settings/governance` }>Governance</Nav.Item>
            <Nav.Item eventKey="members" componentClass={ Link } to={ `/shop/streams/${stream}/settings/members` }>Members</Nav.Item>
          </Nav>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
          { 
            tab == 'options' && <>
              <ResourceSettings 
                resource="Stream"
                title={ _.get(props, 'stream.data.stream.title') }
                name={ _.get(props, 'stream.data.stream.name') }
                onUpdate={ (title, name) => props.onUpdate({ title, name }) }
              />
            </> 
          }

          { 
            tab == 'stream' && <>
              <StreamConfiguration { ...props } />
            </> 
          }

          { 
            tab == 'governance' && <>
              <DataGovernanceOptions 
                visibility={ _.get(props, 'stream.data.stream.visibility') }
                classification={ _.get(props, 'stream.data.stream.classification') }
                personalInformation={ _.get(props, 'stream.data.stream.personalInformation') }
                onUpdate={ (visibility, classification, personalInformation) => props.onUpdate({ visibility, classification, personalInformation }) } />
            </>
          }

          {
            tab == 'members' && <>
              <Members
                title="Manage members"
                members={ _.get(props, 'stream.data.stream.members') }
                roles={ [ 
                  { value: "consumer", label: "Consumer" },
                  { value: "producer", label: "Producer" },
                  { value: "member", label: "Member" },
                  { value: "owner", label: "Owner" }
                ] }
                onGrant={ props.onGrant }
                onRevoke={ props.onRevoke }
                />
            </>
          }
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Container>;
  } else {
    return <Error 
      background={ Background }
      message="You are not authorized to view the settings of this asset." />
  }  
}

StreamSettings.propTypes = {

};

export default StreamSettings;
