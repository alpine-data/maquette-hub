/**
 *
 * ProjectToolchainSettings
 *
 */

import React from 'react';

import Deployment from '../Deployment';

function ProjectToolchainSettings({ view, ...props }) {
  return <>
    <h4>Project Toolchain Settings</h4>
    <p>
      The project toolchain provides common tool stacks which can be used by all members of the project.
    </p>

    <hr />

    <Deployment 
      title="MLflow" 
      icon="tensorflow"
      subtitle="Experiment Tracking &amp; Model Management" 
      deployment={ view.project.infrastructure }
      properties={{ foo: 'BAR', some_secret: 'hello world!' }} />
  </>;
}

ProjectToolchainSettings.propTypes = {};

export default ProjectToolchainSettings;
