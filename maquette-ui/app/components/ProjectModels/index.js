/**
 *
 * ProjectModels
 *
 */

import React from 'react';

import Container from '../Container';
import IFrameDisplay from '../IFrameDisplay';
import ModelOverview from '../ModelOverview';
import ModelSummary from '../ModelSummary';

function MlflowModels({ project }) {
  return <IFrameDisplay 
    src={ `${project.mlflowBaseUrl}/#/models` }
    frameId='mlflow_frame'
    onLoad={ css => {
      css.insertRule('.App-header { display: none }', css.cssRules.length);
      css.insertRule('html, body { overflow: hidden }', css.cssRules.length);
    }} />;
}

function ProjectModels({ view, ...props }) {
  const model = _.get(props, 'match.params.id');

  if (model) {
    return <ModelOverview view={ view } model={ model } { ...props } />
  } else {
    return <Container lg>
      <ModelSummary />
    </Container>;
  }
}

ProjectModels.propTypes = {};

export default ProjectModels;
