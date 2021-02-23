/**
 *
 * ProjectModels
 *
 */

import React from 'react';
import { Route, Switch } from 'react-router-dom';

import Container from '../Container';
import ModelOverview from '../ModelOverview';
import ModelQuestionnaire from '../ModelQuestionnaire';
import ModelSummary from '../ModelSummary';
import ModelVersion from '../ModelVersion';

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

  return <Switch>
    <Route 
      exact
      path="/:project/models/:model/versions/:version"
      render={ routerProps => {
        const model = _.find(view.models, { name: modelName });
        const version = _.find(model.versions, { version: _.get(routerProps, 'match.params.version') })

        return <ModelVersion view={ view } model={ model } version={ version } onUpdateModel={ onUpdateModel } { ...props } />
      } } />

    <Route 
      path="/:project/models/:model/versions/:version/:tab"
      render={ routerProps => {
        const model = _.find(view.models, { name: modelName });
        const version = _.find(model.versions, { version: _.get(routerProps, 'match.params.version') })
        const tab = _.get(routerProps, 'match.params.tab');

        return <ModelVersion view={ view } model={ model } version={ version } tab={ tab } { ...props } />
      } } />

    <Route 
      path="/:project/models/:model" 
      render={ () => {
        const model = _.find(view.models, { name: modelName });
        return <ModelOverview model={ model } view={ view } onUpdateModel={ onUpdateModel } { ...props } />
      } } />

    <Route
      render={ () => <Models view={ view } { ...props } /> } />
  </Switch>;
}

ProjectModels.propTypes = {};

ProjectModels.defaultProps = {
  onUpdateModel: console.log
}

export default ProjectModels;
