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
import { Link } from 'react-router-dom';

const logos = {
  "python": PythonLogo,
  "postgresql": PostgreSQLLogo
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
