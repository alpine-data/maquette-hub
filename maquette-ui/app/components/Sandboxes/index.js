/**
 *
 * Sandboxes
 *
 */
import _ from 'lodash';
import React from 'react';
import { FlexboxGrid } from 'rsuite';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import Container from '../Container';
import StackCard from '../StackCard';


import Background from './background.png';

function Sandboxes(props) {
  const stacks = _.get(props, 'project.stacks');
  return <div className="mq--page-background" style={{ backgroundImage: `url(${Background})` }}>
      <Container md className="mq--main-content">
        <h4>Create a new Sandbox</h4>
        <p className="mq--p-leading">
          You may add multiple stacks to a single sandbox. Select a stack below to start setup of a new sandbox.
        </p>
        <FlexboxGrid justify="space-between">
          { 
            _.map(stacks, s => <React.Fragment key={ s }>
              <FlexboxGrid.Item colspan={ 11 }>
                <StackCard stack={ s } />
              </FlexboxGrid.Item>
            </React.Fragment>)
          }
        </FlexboxGrid>

        <hr />
        <h4>Existing Sandboxes</h4>
      </Container>
    </div>;
}

Sandboxes.propTypes = {};

export default Sandboxes;
