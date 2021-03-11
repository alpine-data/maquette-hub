/**
 *
 * ModelVersion
 *
 */

import React, { useState } from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { Breadcrumb, Button, ButtonGroup, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, Icon, Message, Radio, RadioGroup, Table, Timeline } from 'rsuite';
import { Survey } from 'survey-react';

import { timeAgo, formatDate, formatTime } from '../../utils/helpers';
import { useFormState } from '../../utils/hooks'

import Container from '../Container';
import ModernSummary, { TextMetric } from '../ModernSummary';
import VerticalTabs from '../VerticalTabs';
import EditableParagraph from '../EditableParagraph';
import _ from 'lodash';

const ActionButton = styled(Button)`
  width: 100%;
  max-width: 220px;
  margin-bottom: 10px !important;
  
  &.rs-btn-ghost {
    display: block;
    background: rgba(255, 255, 255, 0.6);
  }

  &.rs-btn-ghost:hover {
    background: rgba(255, 255, 255, 0.9);
  }
`;

function ModelTimeline({ view, version }) {
  return <Timeline className="mq--dataset-versions">
    {
      _.map(version.events, (event, idx) => {
        return <React.Fragment key={ `event-${idx}` }>
          {
            event.event === 'approved' && <>
              <Timeline.Item>
                <b className="mq--sub">{ formatTime(event.created.at) }</b>
                <p><b>{ view.users[event.created.by].name }</b> approved model version</p>
              </Timeline.Item>
            </>
          }

          {
            event.event === 'questionnaire-filled' && <>
              <Timeline.Item>
                <b className="mq--sub">{ formatTime(event.created.at) }</b>
                <p><b>{ view.users[event.created.by].name }</b> answered questionnaire</p>
              </Timeline.Item>
            </>
          }

          {
            event.event === 'rejected' && <>
              <Timeline.Item>
                <b className="mq--sub">{ formatTime(event.created.at) }</b>
                <p><b>{ view.users[event.created.by].name }</b> rejected the model version with the following reason:</p>
                <p>{ event.reason }</p> 
              </Timeline.Item>
            </>
          }

          {
            event.event === 'review-requested' && <>
              <Timeline.Item>
                <b className="mq--sub">{ formatTime(event.created.at) }</b>
                <p><b>{ view.users[event.created.by].name }</b> requested to review the model</p>
              </Timeline.Item>
            </>
          }

          {
            event.event === 'registered' && <>
              <Timeline.Item>
                <b className="mq--sub">{ formatTime(event.created.at) }</b>
                <p><b>{ view.users[event.created.by].name }</b> registered model version</p>
              </Timeline.Item>
            </>
          }
        </React.Fragment>
      })
    }
  </Timeline>
}

function CheckResults({ results }) {
  return <Table autoHeight data={ results }>
    <Table.Column resizable width={ 80 }>
      <Table.HeaderCell>Type</Table.HeaderCell>
      <Table.Cell style={{ textAlign: 'center' }}>
        {
          row => {
            var icons = {
              'exception': {
                icon: 'exclamation-circle',
                color: '#8f1300'
              },
              'warning': {
                icon: 'warning',
                color: '#8f6200'
              },
              'ok': {
                icon: 'check-circle',
                color: '#156100'
              }
            }

            return <span style={{ color: icons[row.type].color }}><Icon icon={ icons[row.type].icon } /></span>;
          }
        }
      </Table.Cell>
    </Table.Column>
    <Table.Column resizable width={ 500 }>
      <Table.HeaderCell>Message</Table.HeaderCell>
      <Table.Cell>
        {
          row => {
            return <>{ row.message }</>
          }
        }
      </Table.Cell>
    </Table.Column>
  </Table>;
}

function ModelAction({ model, view, version, action, onUpdateModel }) {
  switch (action.type) {
    case 'approve':
      if (model.permissions.canApproveModel) {
        return <ActionButton 
          appearance='ghost'  
          componentClass={ Link }
          to={`/${view.project.name}/models/${model.name}/versions/${version.version}/review`}>Review Model</ActionButton>
      } else {
        return <></>
      }
    
    case 'fill-questionnaire':
      if (model.permissions.canFillQuestionnaire) {
        return <ActionButton 
          appearance='ghost'  
          componentClass={ Link }
          to={`/${view.project.name}/models/${model.name}/versions/${version.version}/questionnaire`}>Fill Model Questionnaire</ActionButton>
      } else {
        return <></>;
      }
  
    case 'review-questionnaire':
      if (model.permissions.canFillQuestionnaire || model.permissions.canApproveModel) {
        return <ActionButton 
          appearance='ghost' 
          componentClass={ Link }
          to={`/${view.project.name}/models/${model.name}/versions/${version.version}/questionnaire`}>Review Questionnaire</ActionButton>
      } else {
        return <></>
      }

    case 'request-review':
      if (model.permissions.canFillQuestionnaire) {
        return <ActionButton
          appearance='ghost'
          onClick={ () => {
            onUpdateModel('projects models request-review', {
              project: view.project.name,
              model: model.name,
              version: version.version
            });
          } }>Request Review</ActionButton>;
      } else {
        return <></>;
      }
  
    case 'archive':
      if (model.permissions.canPromote) {
        return <ActionButton 
          appearance='ghost' 
          onClick={ () => {
            onUpdateModel('projects models promote', {
              project: view.project.name,
              model: model.name,
              version: version.version,
              stage: 'Archived'
            });
          } }>Archive Version</ActionButton>
      } else {
        return <></>
      }

    case 'restore':
      if (model.permissions.canPromote) {
        return <ActionButton 
          appearance='ghost' 
          onClick={ () => {
            onUpdateModel('projects models promote', {
              project: view.project.name,
              model: model.name,
              version: version.version,
              stage: 'None'
            });
          } }>Restore Version</ActionButton>
        } else {
          return <></>
        }
    
    case 'promote':
      if (model.permissions.canPromote) {
        return <ActionButton 
          appearance='ghost' 
          onClick={ () => {
            onUpdateModel('projects models promote', {
              project: view.project.name,
              model: model.name,
              version: version.version,
              stage: action.to
            });
          } }>Promote to { action.to }</ActionButton>
      } else {
        return <></>
      }
    
    default:
      return <></>;
  }
}

function Overview({ view, model, version, onUpdateModel }) {
  console.log(model);

  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 16 }>
      <h5>
        Description
      </h5>

      <EditableParagraph 
        label="Edit description"
        value={ version.description } 
        placeholder="No description yet."
        className="mq--p-leading"
        onChange={ value => onUpdateModel('projects models update-version', {
          project: view.project.name,
          model: model.name,
          version: version.version,
          description: value
        })
      } />

      <hr />

      <h5>Code Analysis<span className="mq--sub">, { version.codeQualitySummary }</span></h5>
      <CheckResults results={ version.codeQualityChecks } />

      <hr />

      <h5>Dependency Analysis<span className="mq--sub">, { version.dataDependencySummary }</span></h5>
      <CheckResults results={ version.dataDependencyChecks } />

      <hr />
      <h5>Model History</h5>
      <ModelTimeline view={ view } model={ model } version={ version } />
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
  const [ redo, setRedo ] = useState(false);
  const questions = _.get(version, 'questionnaire.questions');
  const answered = _.get(version, 'questionnaire.answers.answered') || {};
  let answers = _.get(version, 'questionnaire.answers.responses');
  let prefilled = _.get(view, 'latestQuestionnaireResponses') || {};

  if (redo) {
    prefilled = answers;
    answers = undefined;
  }

  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 16 }>
      {
        _.isEmpty(answers) && model.permissions.canFillQuestionnaire && <>
          <Survey data={ prefilled } json={ questions } onComplete={ (sender) => {
            setRedo(false);
            onUpdateModel('projects models answer-questionnaire', {
              project: view.project.name,
              model: model.name,
              version: version.version,
              answers: sender.data
            })
          } } />
        </> 
      }
      {
        _.isEmpty(answers) && !model.permissions.canFillQuestionnaire && <>
          <Message 
            type="info"
            description={ <>Questionnaire not filled yet.</> } />
        </>
      }
      { !_.isEmpty(answers) && <>
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
        !_.isEmpty(answers) && model.permissions.canFillQuestionnaire && version.state != 'approved' && <>
          <ActionButton appearance="ghost" onClick={ () => setRedo(true) }>Redo Questionnaire</ActionButton>
        </>
      }
    </FlexboxGrid.Item>
  </FlexboxGrid>
}

function ReviewModel({ view, model, version, onUpdateModel }) {
  console.log(version);
  const initialState = {
    decision: 'approve',
    reason: ''
  }

  const [state, , onChange] = useFormState(initialState);
  const isValid = !(state.decision === 'reject' && _.isEmpty(state.reason));
  const rejected = _.get(_.first(_.filter(version.events, e => e.event === 'rejected')), 'created');

  return <div>
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 11 }>
        <h5>Model Review</h5>

        {
          version.state === 'rejected' && <>
            <Message 
              type="error"
              description={ <>The model has been rejected by { view.users[rejected.by].name } at <b>{formatTime(rejected.at)}</b>.</> } />
          </>
        }

        { 
          version.state === 'review-requested' && version.state !== 'approved' && <>
            <Form fluid>
              <FormGroup>
                <ControlLabel>Response</ControlLabel>
                <RadioGroup value={ state.decision } onChange={ onChange('decision') }>
                  <Radio value="approve">Approve</Radio>
                  <Radio value="reject">Reject</Radio>
                </RadioGroup>
              </FormGroup>

              <FormGroup>
                <ControlLabel>Reason</ControlLabel>
                <FormControl componentClass="textarea" rows={ 5 } value={ state.reason } onChange={ onChange('reason') } />
              </FormGroup>

              <ButtonGroup>
                <Button 
                  disabled={ !isValid }
                  appearance="primary"
                  onClick={ () => {
                    if (state.reason === 'reject') {
                      onUpdateModel('projects models approve', { 
                        project: view.project.name,
                        model: model.name,
                        version: version.version
                      });
                    } else {
                      onUpdateModel('projects models reject', { 
                        project: view.project.name,
                        model: model.name,
                        version: version.version,
                        reason: state.reason
                      });
                    }
                  } }>

                  Submit Review
                </Button>
              </ButtonGroup>
            </Form>
          </> || <>
            <Message 
              type="info"
              description={ <>Review has not been requested yet.</> } />
          </>
        }
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </div>
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
          value={ version.codeQualitySummary } />,
        <TextMetric
          label="Dependencies"
          value={ version.dataDependencySummary } />,
        <TextMetric
          label="Governance"
          value={ version.dataGovernanceSummary } />
      ] }
      />

      <VerticalTabs
        active= { tab || 'overview' }
        tabs={[
          {
            label: 'Overview',
            link: `/${view.project.name}/models/${model.name}/versions/${version.version}`,
            key: 'overview',
            visible: true,
            component: () => <Overview view={ view } model={ model } version={ version } onUpdateModel={ onUpdateModel } />
          },
          {
            label: 'Questionnaire',
            link: `/${view.project.name}/models/${model.name}/versions/${version.version}/questionnaire`,
            key: 'questionnaire',
            visible: true,
            component: () => <Questionnaire view={ view } model={ model } version={ version } onUpdateModel={ onUpdateModel } />
          },
          {
            label: 'Review',
            link: `/${view.project.name}/models/${model.name}/versions/${version.version}/review`,
            key: 'review',
            visible: model.permissions.canApproveModel,
            component: () => <ReviewModel view={ view } model={ model } version={ version } onUpdateModel={ onUpdateModel } />
          }
        ]} />
  </Container>;
}

ModelVersion.propTypes = {};

export default ModelVersion;
