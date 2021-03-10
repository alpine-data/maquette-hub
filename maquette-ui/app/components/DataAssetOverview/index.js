/**
 *
 * DataAssetOverview
 *
 */

import _ from 'lodash';
import React, { useMemo } from 'react';
import styled from 'styled-components';

import { FlexboxGrid, Tooltip, Whisper } from 'rsuite';

import Container from '../Container';
import DataAssetProjects from '../DataAssetProjects';
import DataAssetProperties from '../DataAssetProperties';
import UserCard from '../UserCard';
import CodeExamples from '../CodeExamples';
import DependencyGraph from '../DependencyGraph';

const Review = styled.div`
  display: inline-block;
  line-height: 2em;
  background-color: #ff0000;
`;

function transformDependencies(view, container) {
  const assetId = view[container].id;

  var nodes = _.map(_.get(view, 'dependencies.nodes') || [], node => {
    const type = _.get(node, 'properties.nodeType');
    const id = _.get(node, 'properties.id');
    const primary = _.isEqual(type, 'data-asset') && _.isEqual(assetId, id)

    let typeLabel = type;
    if (_.isEqual(type, 'data-asset')) {
      typeLabel = view[container].type;
    }

    return {
      id: `node-${node.id}`,
      type,
      data: {
        type,
        label: typeLabel,
        title: _.get(node, 'properties.properties.title') || _.get(node, 'properties.properties.name'),
        primary,
        link: '/foo/bar/todo'
      }
    }
  });

  var relationships = _.map(_.get(view, 'dependencies.relationships') || [], rel => {
    return {
      id: `rel-${rel.id}`,
      source: `node-${rel.startNode}`,
      target: `node-${rel.endNode}`,
      type: 'smoothstep',
      animated: false,
      arrowHeadType: 'arrow',
      style: { stroke: '#333', 'stroke-width': 2 }
    }
  })

  return _.concat(nodes, relationships);
}

function DataAssetOverview({ view, container, codeExamples }) {
  var dependencies = useMemo(() => transformDependencies(view, container), [view.dependencies]);

  return <Container>
    <h5>Properties</h5>
    <DataAssetProperties resource={ view[container] } />

    <h5>Key Contacts</h5>
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 12 }>
        { 
          _.map(view.owners, owner => <UserCard key={ owner.id } user={ owner } role='Data Owner' />)
        }
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 12 }>
        { 
          _.map(view.stewards, owner => <UserCard key={ owner.id } user={ owner } role='Data Steward' />)
        }
      </FlexboxGrid.Item>
    </FlexboxGrid>

    <>
      <hr />
      <h5>Related data assets</h5>
      <DependencyGraph graph={ dependencies } />
    </>

    <>
      <hr />
      <h5>Projects using this { container }</h5>
      <DataAssetProjects asset={ view[container] } />
    </>

    {
      !_.isEmpty(codeExamples) && <>
        <hr />
        <h5>Code Examples</h5>
        <CodeExamples samples={ codeExamples } asset={ view[container].name } />
      </>
    }
  </Container>;
}

DataAssetOverview.propTypes = {};

export default DataAssetOverview;
