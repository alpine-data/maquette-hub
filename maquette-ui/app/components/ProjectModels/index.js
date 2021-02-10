/**
 *
 * ProjectModels
 *
 */

import React from 'react';
import IFrameDisplay from '../IFrameDisplay';


function ProjectModels({ project }) {
  return <IFrameDisplay 
    src={ `${project.mlflowBaseUrl}/#/models` }
    frameId='mlflow_frame'
    onLoad={ css => {
      css.insertRule('.App-header { display: none }', css.cssRules.length);
      css.insertRule('html, body { overflow: hidden }', css.cssRules.length);
    }} />;
}

ProjectModels.propTypes = {};

export default ProjectModels;
