/**
 *
 * ModelOverview
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { Breadcrumb, FlexboxGrid } from 'rsuite';

import { timeAgo, formatDate } from '../../utils/helpers';

import Container from '../Container';
import ModelQuestionnaire from '../ModelQuestionnaire';
import ModelSettings from '../ModelSettings';
import ModernSummary, { TextMetric, TrendMetric } from '../ModernSummary';
import UserCard from '../UserCard';
import VerticalTabs from '../VerticalTabs';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

const roleLabels = {
  "owner": "Owner",
  "sme": "Subject Matter Expert",
  "reviewer": "Reviewer",
  "ds": "Data Scientist"
}

function Overview({ model, view, onUpdateModel }) {
  return <FlexboxGrid justify="space-between">
    <FlexboxGrid.Item colspan={ 16 }>
      <h5>
        Description
      </h5>
      <p className="mq--p-leading">
        { model.description || <>No description yet.</> }
      </p>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 7 }>
      <h5>
        Key Contacts
      </h5>

      
      {
        _.map(model.members, member => <UserCard 
          key={ `${member.role}/${member.authorization.name}` }
          role={ roleLabels[member.role] }
          user={ member.authorization.name }
          users={ view.users }
        />)
      }
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <h5>
        Versions
      </h5>

      {
        _.map(model.versions, version => <React.Fragment key={ version.version }>
          <ModernSummary
            title={ `Version ${version.version}` }
            tags={ [ version.description || 'No description' ] }
            appearance="ghost"
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
            link={ `/${view.project.name}/models/${model.name}/versions/${version.version}` } />   
        </React.Fragment>)
      }
    </FlexboxGrid.Item>
  </FlexboxGrid>
}

function ModelOverview({ view, model, onGrantModelRole, onRevokeModelRole, onUpdateModel, ...props }) {  
  const stages = _.map(
    [
      _.get(_.find(model.versions, { stage: 'None' }), 'version') || '-',
      _.get(_.find(model.versions, { stage: 'Staging' }), 'version') || '-',
      _.get(_.find(model.versions, { stage: 'Production' }), 'version') || '-'
    ], version => {
      if (version !== '-') {
        return `Version ${version}`;
      } else {
        return version;
      }
    });

  return <Container xlg>
    <Breadcrumb>
      <Breadcrumb.Item componentClass={ Link } to={ `/${view.project.name}/models` } >Models</Breadcrumb.Item>
      <Breadcrumb.Item active>{ model.title }</Breadcrumb.Item>
    </Breadcrumb>

    <div style={{ marginBottom: '30px' }}>
      <ModernSummary
        title={ model.title }
        tags={ model.flavors || model.flavours }
        metrics={[
          <TrendMetric
            value={ _.size(model.warnings) }
            label='Warnings'
            text='+3 last 7 days'
            trend='up'
            sentiment='negative' />,
          <TrendMetric
            value={ 12 }
            label='Consumers'
            text='No changes last 7 days' />,
          <TextMetric
            label="Development / Staging / Production"
            value={ _.join(stages, ' / ') } />,
        ]} />
    </div>

    <VerticalTabs
      active={ _.get(props, 'match.params.id2') || 'overview' }
      tabs={[
        {
          label: "Overview",
          link: `/${view.project.name}/models/${model.name}`,
          key: "overview",
          visible: true,
          component: () => <Overview 
            view={ view } 
            model={ model }
            onUpdateModel={ onUpdateModel } />
        },
        {
          label: "Dashboard",
          link: `/${view.project.name}/models/${model.name}/dashboard`,
          key: "dashboard",
          visible: true,
          component: () => <>Huhuh</>
        },
        {
          label: "Relationships",
          link: `/${view.project.name}/models/${model.name}/relationships`,
          key: "relationships",
          visible: true,
          component: () => <>Huhuh</>
        },
        {
          label: "Settings",
          link: `/${view.project.name}/models/${model.name}/settings`,
          key: "settings",
          visible: true,
          component: () => <ModelSettings 
            view={ view } 
            model={ model }
            onGrant={ value => onGrantModelRole(_.assign({ model: model.name }, value)) }
            onRevoke={ value => onRevokeModelRole(_.assign({ model: model.name }, value )) }
            onUpdate={ state => onUpdateModel('projects models update', _.assign({ model: model.name }, state)) } />
        },
        {
          label: "Questionnaire",
          link: `/${view.project.name}/models/${model.name}/questionnaire`,
          key: "questionnaire",
          visible: true,
          component: () => <ModelQuestionnaire view={ view } model={ model } />
        }
      ]} />
  </Container>;
}

ModelOverview.propTypes = {};

ModelOverview.defaultProps = {
  onUpdateModel: console.log
}

export default ModelOverview;
