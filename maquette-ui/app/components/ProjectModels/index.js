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

function Models({ view, ...props }) {
  return <Container lg>
      {
        _.map(view.models, model => <React.Fragment key={ model.name }>
          <ModelSummary
            title={ model.title }
            tags={ [ model.name ] }
            link={ `/${view.project.name}/models/${model.name}` } 
            warnings={ _.size(model.warnings) }
            owner={{ id: model.updated.by, name: 'Egon Olsen' }}
            updated={ model.updated.at } />
        </React.Fragment>)
      }
    </Container>;
}

function ProjectModels({ view, onUpdateModel, ...props }) {
  const modelName = _.get(props, 'match.params.id');

  if (modelName) {
    const model = _.find(view.models, { name: modelName });
    return <ModelOverview view={ view } model={ model } onUpdateModel={ onUpdateModel } { ...props } />
  } else {
    return <Models view={ view } />
  }
}

ProjectModels.propTypes = {};

ProjectModels.defaultProps = {
  onUpdateModel: console.log
}

export default ProjectModels;
