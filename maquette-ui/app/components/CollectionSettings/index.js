/**
 *
 * CollectionSettings
 *
 */

import React from 'react';
import PropTypes from 'prop-types';

import { FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

import Background from '../../resources/datashop-background.png';
import Container from '../Container';
import ResourceSettings from '../ResourceSettings';
import DataGovernanceOptions from '../DataGovernanceOptions';
import Members from '../Members';
import Error from '../Error';

function CollectionSettings(props) {
  const collection = _.get(props, 'match.params.collection');
  const tab = _.get(props, 'match.params.id') || 'options'

  const isOwner = _.get(props, 'collection.data.isOwner');

  if (isOwner) {
    return <Container xlg className="mq--main-content" background={ Background }>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={4}>
          <Nav vertical activeKey={ tab } appearance="subtle">
            <Nav.Item eventKey="options" componentClass={ Link } to={ `/shop/collections/${collection}/settings` }>Options</Nav.Item>
            <Nav.Item eventKey="governance" componentClass={ Link } to={ `/shop/collections/${collection}/settings/governance` }>Governance</Nav.Item>
            <Nav.Item eventKey="members" componentClass={ Link } to={ `/shop/collections/${collection}/settings/members` }>Members</Nav.Item>
          </Nav>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
          { 
            tab == 'options' && <>
              <ResourceSettings 
                resource="Collection"
                title={ _.get(props, 'collection.data.collection.title') }
                name={ _.get(props, 'collection.data.collection.name') }
                onUpdate={ (title, name) => props.onUpdate({ title, name }) }
              />
            </> 
          }

          { 
            tab == 'governance' && <>
              <DataGovernanceOptions 
                visibility={ _.get(props, 'collection.data.collection.visibility') }
                classification={ _.get(props, 'collection.data.collection.classification') }
                personalInformation={ _.get(props, 'collection.data.collection.personalInformation') }
                onUpdate={ (visibility, classification, personalInformation) => props.onUpdate({ visibility, classification, personalInformation }) } />
            </>
          }

          {
            tab == 'members' && <>
              <Members
                title="Manage members"
                members={ _.get(props, 'collection.data.collection.members') }
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

CollectionSettings.propTypes = {
  onUpdate: PropTypes.func,
  onGrant: PropTypes.func,
  onRevoke: PropTypes.func
};

CollectionSettings.defaultProps = {
  onUpdate: console.log,
  onGrant: console.log,
  onRevoke: console.log
}

export default CollectionSettings;
