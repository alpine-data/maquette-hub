/**
 *
 * ModelVersion
 *
 */

import React from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { Breadcrumb, Button, FlexboxGrid, Message } from 'rsuite';
import { Survey } from 'survey-react';

import { timeAgo, formatDate, formatTime } from '../../utils/helpers';

import Container from '../Container';
import ModernSummary, { TextMetric } from '../ModernSummary';
import VerticalTabs from '../VerticalTabs';

const ActionButton = styled(Button)`
  width: 100%;
  max-width: 220px;
  margin-bottom: 10px !important;
`;

function ModelAction({ model, view, version, action, onUpdateModel }) {
  switch (action.type) {
    case 'approve':
      return <ActionButton
        appearance='ghost' 
        onSelect={ () => {
          onUpdateModel('projects models approve', { 
            project: view.project.name,
            model: model.name,
            version: version.version
          });
        } }>Approve Model</ActionButton>
    
    case 'fill-questionnaire':
      return <ActionButton 
        appearance='ghost'  
        componentClass={ Link }
        to={`/${view.project.name}/models/${model.name}/versions/${version.version}/questionnaire`}>Fill Model Questionnaire</ActionButton>
  
    case 'review-questionnaire':
      return <ActionButton 
        appearance='ghost' 
        componentClass={ Link }
        to={`/${view.project.name}/models/${model.name}/versions/${version.version}/questionnaire`}>Review Questionnaire</ActionButton>
  
    case 'archive':
      return <ActionButton 
        appearance='ghost' 
        onSelect={ () => {
          onUpdateModel('projects models promote', {
            project: view.project.name,
            model: model.name,
            version: version.version,
            stage: 'Archived'
          });
        } }>Archive Version</ActionButton>

    case 'restore':
      return <ActionButton 
        appearance='ghost' 
        onSelect={ () => {
          onUpdateModel('projects models promote', {
            project: view.project.name,
            model: model.name,
            version: version.version,
            stage: 'None'
          });
        } }>Restore Version</ActionButton>
    
    case 'promote':
      return <ActionButton 
        appearance='ghost' 
        onSelect={ () => {
          onUpdateModel('projects models promote', {
            project: view.project.name,
            model: model.name,
            version: version.version,
            stage: action.to
          });
        } }>Promote to { action.to }</ActionButton>
    
    default:
      return <></>;
  }
}

function Overview({ view, model, version, onUpdateModel }) {
  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 16 }>
      <h5>
        Description
      </h5>
      <p className="mq--p-leading">
        { version.description || 'No description yet.' }
      </p>

      <h6>Source Code</h6>
      <ul>
        <li><a href="#">Source Repository</a></li>
        <li><a href="#">Browse Commit</a></li>
      </ul>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 7 } style={{
      borderLeft: '1px solid #ccc',
      padding: '0 20px'
    }}>
      <h5>
        Actions
      </h5>

      {
        _.map(version.actions, (action, idx) => <ModelAction 
          key={ `${action.type}/${idx}` } 
          action={ action } 
          model={ model } 
          view={ view }
          version={ version }
          onUpdateModel={ onUpdateModel } />)
      }
    </FlexboxGrid.Item>
  </FlexboxGrid>
}

function Questionnaire({ view,  model, version, onUpdateModel }) {
  const questions = _.get(version, 'questionnaire.questions');
  const answers = _.get(version, 'questionnaire.answers.responses');
  const prefilled = _.get(view, 'latestQuestionnaireResponses') || {};

  const answered = _.get(version, 'questionnaire.answers.answered') || {};

  console.log(version);

  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 16 }>
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
          <Message 

            type="info"
            description={ <>Questionnaire submitted by <b>{view.users[answered.by].name}</b> at <b>{formatTime(answered.at)}</b>.</> } />
          <Survey data={ answers } json={ questions } mode="display" />
        </>
      }
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 7 } style={{
      borderLeft: '1px solid #ccc',
      padding: '0 20px'
    }}>
      {

        !_.isEmpty(answers) && <>
          <ActionButton appearance="ghost">Redo Questionnaire</ActionButton>
        </>
      }
    </FlexboxGrid.Item>
  </FlexboxGrid>
}

function ModelVersion({ view, model, version, tab, onUpdateModel }) {
  return <Container xlg>
    <Breadcrumb>
      <Breadcrumb.Item componentClass={ Link } to={ `/${view.project.name}/models` }>Models</Breadcrumb.Item>
      <Breadcrumb.Item componentClass={ Link } to={ `/${view.project.name}/models/${model.name}` } >{ model.title }</Breadcrumb.Item>
      <Breadcrumb.Item active>Version { version.version }</Breadcrumb.Item>
    </Breadcrumb>

    <ModernSummary
      title={ `Version ${version.version}` }
      tags={ version.flavours }
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

      <VerticalTabs
        active= { tab || 'summary' }
        tabs={[
          {
            label: 'Summary',
            link: `/${view.project.name}/models/${model.name}/versions/${version.version}`,
            key: 'summary',
            visible: true,
            component: () => <Overview view={ view } model={ model } version={ version } onUpdateModel={ onUpdateModel } />
          },
          {
            label: 'Questionnaire',
            link: `/${view.project.name}/models/${model.name}/versions/${version.version}/questionnaire`,
            key: 'questionnaire',
            visible: true,
            component: () => <Questionnaire view={ view } model={ model } version={ version } onUpdateModel={ onUpdateModel } />
          }
        ]} />
  </Container>;
}

ModelVersion.propTypes = {};

export default ModelVersion;
