/**
 *
 * ModelOverview
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { Breadcrumb, FlexboxGrid, Table } from 'rsuite';

import { timeAgo } from '../../utils/helpers';

import Container from '../Container';
import ModernSummary, { TextMetric, TrendMetric } from '../ModernSummary';
import UserCard from '../UserCard';
import VerticalTabs from '../VerticalTabs';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

function Overview({ model, view }) {
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

      
      <UserCard
        role="Owner"
        user={{ id: 'alice', name: 'Gustav Gustavson' }} />

      <UserCard
        role="Subject Matter Expert"
        user={{ id: 'alice', name: 'Gustav Gustavson' }} />

      <UserCard
        role="Reviewer"
        user={{ id: 'alice', name: 'Gustav Gustavson' }} />
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 24 }>
      <h5>
        Versions
      </h5>

      <Table autoHeight data={ model.versions }>
        <Table.Column flexGrow={ 1 }>
          <Table.HeaderCell>Version</Table.HeaderCell>
          <Table.Cell dataKey="version" />
        </Table.Column>

        <Table.Column flexGrow={ 3 }>
          <Table.HeaderCell>Description</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>
                {
                  !_.isEmpty(row.description) && <>
                    { row.description }<>huhu</>
                  </> || <>
                    <span className="mq--sub">No description</span>
                  </>
                }
              </>
            }
          </Table.Cell>
        </Table.Column>

        <Table.Column flexGrow={ 2 }>
          <Table.HeaderCell>Registered</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>{ timeAgo(row.registered.at) } ago by { row.registered.by }</>
            }
          </Table.Cell>
        </Table.Column>

        <Table.Column flexGrow={ 1 }>
          <Table.HeaderCell>Stage</Table.HeaderCell>
          <Table.Cell dataKey="stage" />
        </Table.Column>

        <Table.Column flexGrow={ 1 }>
          <Table.HeaderCell>Actions</Table.HeaderCell>
          <Table.Cell>
            { row => <>Hier</> }
          </Table.Cell>
        </Table.Column>
      </Table>
    </FlexboxGrid.Item>
  </FlexboxGrid>
}

function ModelOverview({ view, model, ...props }) {
  console.log(model);
  
  return <Container xlg>
    <Breadcrumb>
      <Breadcrumb.Item componentClass={ Link } to={ `/${view.project.name}/models` } >Models</Breadcrumb.Item>
      <Breadcrumb.Item active>{ model.name }</Breadcrumb.Item>
    </Breadcrumb>

    <div style={{ marginBottom: '30px' }}>
      <ModernSummary
        title={ model.name }
        tags={ model.flavors || model.flavours }
        metrics={[
          <TrendMetric
            value={ model.warnings }
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
            value="Version 23 / Version 10 / Version 10" />,
        ]} />
    </div>

    <VerticalTabs
      active={ _.get(props, 'match.params.id2') || 'overview' }
      tabs={[
        {
          label: "Overview",
          link: `/${view.project.name}/models/${model}`,
          key: "overview",
          visible: true,
          component: () => <Overview view={ view } model={ model } />
        },
        {
          label: "Dashboard",
          link: `/${view.project.name}/models/${model}/dashboard`,
          key: "dashboard",
          visible: true,
          component: () => <>Huhuh</>
        },
        {
          label: "Relationships",
          link: `/${view.project.name}/models/${model}/relationships`,
          key: "relationships",
          visible: true,
          component: () => <>Huhuh</>
        },
        {
          label: "Settings",
          link: `/${view.project.name}/models/${model}/settings`,
          key: "settings",
          visible: true,
          component: () => <>Huhuh</>
        }
      ]} />
  </Container>;
}

ModelOverview.propTypes = {};

export default ModelOverview;
