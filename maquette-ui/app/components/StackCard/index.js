/**
 *
 * StackCard
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
// import styled from 'styled-components';

import Summary from '../Summary';

import ElasticsearchLogo from './logos/elasticsearch.svg';
import Neo4J from './logos/neo4j.svg';
import PythonGPU from './logos/python-gpu.svg';
import PostgreSQLLogo from './logos/postgresql.svg';
import PythonLogo from './logos/python.svg';
import RStudio from './logos/r-studio.svg';
import TensorflowGPU from './logos/tensorflow-gpu.svg';
import Tensorflow from './logos/tensorflow.svg';

import { Button, FlexboxGrid } from 'rsuite';
import { Link } from 'react-router-dom';

const logos = {
  "elasticsearch": ElasticsearchLogo,
  "neo4j": Neo4J,
  "python": PythonLogo,
  "python-gpu": PythonGPU,
  "postgresql": PostgreSQLLogo,
  "r-studio": RStudio,
  "tensorflow-gpu": TensorflowGPU,
  "tensorflow": Tensorflow
}

export const StackIcon = ({ stack, ...props }) => {
  return <img src={ logos[stack.icon] } alt={ stack.title } width={ 64 } { ...props } />
}

function StackCard({ actionLabel = 'Setup this stack', stack, linkTo, onClick = console.log }) {
  return <Summary.Summaries>
      <Summary>
        <Summary.Header category="Stack" icon="stack-overflow">{ stack.title }</Summary.Header>
        <Summary.Body>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={18}>
              { stack.summary }
            </FlexboxGrid.Item>
            <FlexboxGrid.Item colspan={ 6 } align="right">
              <StackIcon stack={ stack } />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={24}>
              {
                actionLabel && <>
                  <br /><br />
                  {
                    linkTo && <>
                      <Button appearance="ghost" componentClass={ Link } to={ linkTo(stack.name) }>{ actionLabel }</Button>
                    </> || <>
                      <Button appearance="ghost" onClick={ () => onClick(stack.name) }>{ actionLabel }</Button>
                    </>
                  }
                </>
              }
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Summary.Body>
      </Summary>
    </Summary.Summaries>;
}

StackCard.propTypes = {
  stack: PropTypes.shape({
    icon: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    summary: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    tags: PropTypes.arrayOf(PropTypes.string).isRequired
  }).isRequired,

  actionLabel: PropTypes.any,

  onClick: PropTypes.func,
  linkTo: PropTypes.func
};

export default StackCard;
