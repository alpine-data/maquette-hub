/**
 *
 * StackCard
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
// import styled from 'styled-components';

import Summary from '../Summary';

import PostgreSQLLogo from './logos/postgresql.svg';
import PythonLogo from './logos/python.svg';
import { Button, FlexboxGrid } from 'rsuite';

const logos = {
  "python": PythonLogo,
  "postgresql": PostgreSQLLogo
}

function StackCard({ stack, onClick=console.log }) {
  return <Summary.Summaries>
      <Summary>
        <Summary.Header category="Stack" icon="stack-overflow">{ stack.title }</Summary.Header>
        <Summary.Body>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={18}>
              { stack.summary }
            </FlexboxGrid.Item>
            <FlexboxGrid.Item colspan={ 6 }>
              <img src={ logos[stack.icon] } width="64" /> 
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={24}>
              <br /><br />
              <Button appearance="ghost" onClick={ () => onClick(stack.name) }>Setup this Stack</Button>
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

  onClick: PropTypes.func
};

export default StackCard;
