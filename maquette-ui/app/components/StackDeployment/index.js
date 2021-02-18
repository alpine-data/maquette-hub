/**
 *
 * StackDeployment
 *
 */
import _ from 'lodash';
import React from 'react';
import { Button } from 'rsuite';;
import Deployment from '../Deployment';

function StackDeployment({ deployedStack, stacks }) {
  const stack = _.find(stacks, s => s.name == deployedStack.configuration.stack)

  return <Deployment
    title={ stack.title }
    icon={ stack.icon }
    subtitle="Deployment details"
    deployment={ deployedStack.deployment }
    properties={ deployedStack.parameters.parameters }
    actionButtons={ <>
      <Button appearance="ghost" size="sm" target="_blank" href={ deployedStack.parameters.entrypoint }>{ deployedStack.parameters.entrypointLabel }</Button>
    </> }  />;
}

StackDeployment.propTypes = {};

export default StackDeployment;
