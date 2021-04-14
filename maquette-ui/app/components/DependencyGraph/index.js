/**
 *
 * DependencyGraph
 *
 */

import _ from 'lodash';
import React, { useEffect, useMemo, useState } from 'react';
import cx from 'classnames';
import ReactFlow, { Background, Controls, Handle, isNode } from 'react-flow-renderer'
import styled from 'styled-components';
import dagre from 'dagre';

import { Icon, IconButton } from 'rsuite';
import { TextMetric } from '../ModernSummary';

const GraphWrapper = styled.div`
  position: relative;
  border: 1px solid #cccccc;
  height: 400px;
  background: #fff;
`;

const FullScreenWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  z-index: 9999;
`

const FullSizeToggleWrapper = styled.div`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 10px;
  text-align: right;
  z-index: 5;
`

const DataSourceNode = ({ data }) => {
  const classNames = cx({
    'mq--graph--node': true,
    'mq--graph--node-primary': data.primary,
    [`mq--graph--node--${data.type}`]: true,
  })

  return <div className={ classNames }>
    <Handle type="target" position="left" style={{ borderRadius: 0 }} />
    <TextMetric
      label={ _.capitalize(data.label) }
      value={ <>{ data.title } <a href={  data.link }><Icon icon="link" style={{ marginLeft: '10px' }} /></a></> }
    />
    <Handle
      type="source"
      position="right"
      style={{ borderRadius: 0 }}
    />
  </div>
}

const getLayoutedElements = (elements, direction = 'LR') => {
  const dagreGraph = new dagre.graphlib.Graph();
  dagreGraph.setDefaultEdgeLabel(() => ({}));

  const isHorizontal = direction === 'LR';
  dagreGraph.setGraph({ rankdir: direction });
  elements.forEach((el) => {
    if (isNode(el)) {
      dagreGraph.setNode(el.id, { width: 150, height: 50 });
    } else {
      dagreGraph.setEdge(el.source, el.target);
    }
  });
  dagre.layout(dagreGraph);
  return elements.map((el) => {
    if (isNode(el)) {
      const nodeWithPosition = dagreGraph.node(el.id);
      el.targetPosition = isHorizontal ? 'left' : 'top';
      el.sourcePosition = isHorizontal ? 'right' : 'bottom';
      // unfortunately we need this little hack to pass a slighltiy different position
      // in order to notify react flow about the change
      el.position = {
        x: nodeWithPosition.x + Math.random() / 1000,
        y: nodeWithPosition.y,
      };
      el.type = 'custom';
    }
    return el;
  });
};


function DependencyGraph({ graph }) {
  const [fullScreen, setFullScreen] = useState(false);
  const [instance, setInstance] = useState(undefined);
  const elements = useMemo(() => getLayoutedElements(graph), [graph]);

  const Wrapper = fullScreen && FullScreenWrapper || GraphWrapper;

  useEffect(() => {
    if (instance) {
      instance.fitView();
    }
  }, [fullScreen]);

  return   <Wrapper>
    <ReactFlow 
      arrowHeadColor="#333"
      onLoad={
        (reactFlowInstance) => {
          reactFlowInstance.fitView();
          setInstance(reactFlowInstance);
        }
      }
      elements={elements}
      nodeTypes={{ default: DataSourceNode, custom: DataSourceNode, input: DataSourceNode, output: DataSourceNode }}>
        
        <Background />
        <Controls />
    </ReactFlow>

    <FullSizeToggleWrapper>
      <IconButton 
        onClick={ () => setFullScreen(!fullScreen) }
        icon={ <Icon icon="expand" /> } 
      />
    </FullSizeToggleWrapper>
  </Wrapper>;
}

DependencyGraph.propTypes = {};

export default DependencyGraph;
