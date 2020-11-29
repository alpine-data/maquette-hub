/**
 *
 * ProjectSettings
 *
 */

import React from 'react';
import PropTypes from 'prop-types';

import Error from '../Error';

import Background from '../../resources/projects-background.png';
import Container from '../Container';
import { FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';
import ResourceSettings from '../ResourceSettings';
import Members from '../Members';

function ProjectSettings(props) {
  const project = _.get(props, 'match.params.project');
  const tab = _.get(props, 'match.params.id') || 'options'

  const isMember = _.get(props, 'project.data.isMember');
  const isAdmin = _.get(props, 'project.data.isAdmin');

  if (isMember) {    
    return <Container xlg className="mq--main-content" background={ Background }>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={4}>
          <Nav vertical activeKey={ tab } appearance="subtle">
            <Nav.Item eventKey="options" componentClass={ Link } to={ `/${project}/settings` }>Options</Nav.Item>
            <Nav.Item eventKey="members" componentClass={ Link } to={ `/${project}/settings/members` }>Members</Nav.Item>
          </Nav>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
          { 
            tab == 'options' && <>
              <ResourceSettings 
                resource="Project"
                title={ _.get(props, 'project.data.project.title') }
                name={ _.get(props, 'project.data.project.name') }
                onUpdate={ (title, name) => props.onUpdate({ title, name }) }
              />
            </> 
          }

          {
            tab == 'members' && <>
              <Members
                title="Manage members"
                members={ _.get(props, 'project.data.project.members') }
                roles={ [ 
                  { value: "member", label: "Member" },
                  { value: "admin", label: "Admin" }
                ] }
                readOnly={ !isAdmin }
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
      message="You are not authorized to view the settings of this project." />
  }
}

ProjectSettings.propTypes = {
  onUpdate: PropTypes.func,
  onGrant: PropTypes.func,
  onRevoke: PropTypes.func
}

ProjectSettings.defaultProps = {
  onUpdate: console.log,
  onGrant: console.log,
  onRevoke: console.log
}

export default ProjectSettings;
