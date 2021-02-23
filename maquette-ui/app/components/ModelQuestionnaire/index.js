/**
 *
 * ModelQuestionnaire
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { Breadcrumb } from 'rsuite';

import { timeAgo, formatDate } from '../../utils/helpers';

import { Survey } from 'survey-react';
import Container from '../Container';
import ModernSummary, { TextMetric } from '../ModernSummary';

function ModelQuestionnaire({ model, version, view, onUpdateModel }) {  
  const questions = _.get(version, 'questionnaire.questions');
  const answers = _.get(version, 'questionnaire.answers.responses');
  const prefilled = _.get(view, 'latestQuestionnaireResponses') || {};

  console.log(answers, prefilled);

  return <Container xlg>
    <Breadcrumb>
      <Breadcrumb.Item componentClass={ Link } to={ `/${view.project.name}/models` }>Models</Breadcrumb.Item>
      <Breadcrumb.Item componentClass={ Link } to={ `/${view.project.name}/models/${model.name}` } >{ model.title }</Breadcrumb.Item>
      <Breadcrumb.Item active>Version { version.version }</Breadcrumb.Item>
      <Breadcrumb.Item active>Questionnaire</Breadcrumb.Item>
    </Breadcrumb>

    <ModernSummary
      title={ `Version ${version.version}` }
      tags={ [ version.description || 'No description' ] }
      metricColspan={ 3 }
      actionsColspan={ 1 }
      metrics={ [
        <TextMetric 
          label="Stage"
          value={ version.stage } />,
        <TextMetric
          label="Registered" 
          value={ formatDate(version.registered.at ) } />,
        <TextMetric
          label="Updated" 
          value={ timeAgo(version.updated.at) } />,
        <TextMetric
          label="Code Quality" 
          value="No issues" />,
        <TextMetric
          label="Governance"
          value="Action required" />,
        <TextMetric
          label="Monitoring"
          value="No issues" />
      ] }
      />

      <Container lg>
        {
          _.isEmpty(answers) && <>
            <Survey data={ prefilled } json={ questions } onComplete={ (sender) => {
              onUpdateModel('projects models answer-questionnaire', {
                project: view.project.name,
                model: model.name,
                version: version.version,
                answers: sender.data
              })
            } } />
          </> || <>
            <Survey data={ answers } json={ questions } mode="display" />
          </>
        }
        
      </Container>
  </Container>
}

ModelQuestionnaire.propTypes = {};

export default ModelQuestionnaire;
