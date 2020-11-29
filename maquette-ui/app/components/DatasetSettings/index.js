/**
 *
 * DatasetSettings
 *
 */

import React from 'react';
import Container from '../Container';
import PropTypes from 'prop-types';

import Background from '../../resources/datashop-background.png';
import { FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';
import ResourceSettings from '../ResourceSettings';
import DataGovernanceOptions from '../DataGovernanceOptions';
import Members from '../Members';
import Error from '../Error';

function DatasetSettings(props) {
  const dataset = _.get(props, 'match.params.dataset') || 'dataset';
  const tab = _.get(props, 'match.params.id') || 'options'

  const isOwner = _.get(props, 'dataset.data.isOwner');

  if (isOwner) {
    return <Container xlg className="mq--main-content" background={ Background }>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={4}>
          <Nav vertical activeKey={ tab } appearance="subtle">
            <Nav.Item eventKey="options" componentClass={ Link } to={ `/shop/datasets/${dataset}/settings` }>Options</Nav.Item>
            <Nav.Item eventKey="governance" componentClass={ Link } to={ `/shop/datasets/${dataset}/settings/governance` }>Governance</Nav.Item>
            <Nav.Item eventKey="members" componentClass={ Link } to={ `/shop/datasets/${dataset}/settings/members` }>Members</Nav.Item>
          </Nav>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
          { 
            tab == 'options' && <>
              <ResourceSettings 
                resource="Dataset"
                title={ _.get(props, 'dataset.data.dataset.title') }
                name={ _.get(props, 'dataset.data.dataset.name') }
                onUpdate={ (title, name) => props.onUpdate({ title, name }) }
              />
            </> 
          }

          { 
            tab == 'governance' && <>
              <DataGovernanceOptions 
                visibility={ _.get(props, 'dataset.data.dataset.visibility') }
                classification={ _.get(props, 'dataset.data.dataset.classification') }
                personalInformation={ _.get(props, 'dataset.data.dataset.personalInformation') }
                onUpdate={ (visibility, classification, personalInformation) => props.onUpdate({ visibility, classification, personalInformation }) } />
            </>
          }

          {
            tab == 'members' && <>
              <Members
                title="Manage members"
                members={ _.get(props, 'dataset.data.dataset.members') }
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

DatasetSettings.propTypes = {
  onUpdate: PropTypes.func,
  onGrant: PropTypes.func,
  onRevoke: PropTypes.func
};

DatasetSettings.defaultProps = {
  onUpdate: console.log,
  onGrant: console.log,
  onRevoke: console.log
}

export default DatasetSettings;
