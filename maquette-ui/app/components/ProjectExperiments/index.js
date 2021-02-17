/**
 *
 * ProjectExperiments
 *
 */

import React from 'react';
import IFrameDisplay from '../IFrameDisplay';

function ProjectExperiments({ project }) {
  return <IFrameDisplay 
    src={ `${project.mlflowBaseUrl}/#/` }
    frameId='mlflow_frame'
    onLoad={ css => {
      css.insertRule('.App-header { display: none }', css.cssRules.length);
      css.insertRule('html, body { overflow: hidden }', css.cssRules.length);
    }} />;
}

ProjectExperiments.propTypes = {};

export default ProjectExperiments;
