/**
 *
 * ProjectSettings
 *
 */

import React from 'react';
import PropTypes from 'prop-types';

import Container from '../Container';
import Members from '../Members';
import ResourceSettings from '../ResourceSettings';
import VerticalTabs from '../VerticalTabs';
import ProjectToolchainSettings from '../ProjectToolchainSettings';

function ProjectSettings(props) {
  const project = _.get(props, 'match.params.project');
  const isAdmin = _.get(props, 'project.data.isAdmin');

  return <Container lg>
    <VerticalTabs
      active={ _.get(props, 'match.params.id') || 'properties' }
      tabs={[
        {
          order: 0,
          key: 'properties',
          label: 'Properties',
          link: `/${project}/settings`,
          visible: isAdmin,
          component: () => <ResourceSettings 
            resource="Project"
            title={ _.get(props, 'project.data.project.title') }
            name={ _.get(props, 'project.data.project.name') }
            onUpdate={ (title, name) => props.onUpdate({ title, name }) } />
        },
        {
          order: 10,
          key: 'members',
          label: 'Members',
          link: `/${project}/settings/members`,
          visible: isAdmin,
          component: () => <Members
            title="Manage members"
            members={ _.get(props, 'project.data.project.members') }
            roles={ [ 
              { value: "member", label: "Member" },
              { value: "admin", label: "Admin" }
            ] }
            readOnly={ !isAdmin }
            onGrant={ props.onGrant }
            onRevoke={ props.onRevoke } />
        },
        {
          order: 20,
          key: 'toolchain',
          label: 'Toolchain',
          link: `/${project}/settings/toolchain`,
          visible: isAdmin,
          component: () => <ProjectToolchainSettings view={ _.get(props, 'project.data') } { ...props } />
        }
      ]} />
    </Container>;
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
