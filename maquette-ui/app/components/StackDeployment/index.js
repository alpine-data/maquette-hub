/**
 *
 * StackDeployment
 *
 */
import _ from 'lodash';
import React, { useState } from 'react';
import { Button, ButtonToolbar, FlexboxGrid, Icon, Table, Tooltip, Whisper } from 'rsuite';
import copy from 'copy-to-clipboard';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';

import { StackIcon } from '../StackCard';

SyntaxHighlighter.registerLanguage('json', json);

function Status({ status }) {
  if (status == "STARTED") {
    return <Button color="green" size="sm">Started</Button>;
  } else if (status == "STOPPED") {
    return <Button color="red" size="sm">Stopped</Button>
  }
}

function StackDeployment({ deployedStack, stacks }) {
  const stack = _.find(stacks, s => s.name == deployedStack.configuration.stack)
  const [showDeploymentDetails, setShowDeploymetDetails] = useState(false);

  const properties = _.map(deployedStack.parameters.parameters, (value, key) =>  { return { key, value } });

  return <>
    <FlexboxGrid align="middle">
      <FlexboxGrid.Item>
        <StackIcon stack={ stack } style={{ marginRight: "20px" }} width={ 48 } />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item>
        <h5 style={{ marginBottom: "10px" }}>{ stack.title }<br /><span className="mq--sub" style={{ fontWeight: "normal" }}>deployment properties</span></h5>

        <ButtonToolbar>
          <Status status={ deployedStack.deployment.status } />
          <Button appearance="ghost" size="sm" target="_blank" href={ deployedStack.parameters.entrypoint }>{ deployedStack.parameters.entrypointLabel }</Button>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </FlexboxGrid>

    <br /><br />

    <Table data={ properties } autoHeight rowHeight={ 60 }>
      <Table.Column flexGrow={1} verticalAlign="middle">
        <Table.HeaderCell>Property</Table.HeaderCell>
        <Table.Cell dataKey="key" />
      </Table.Column>
      <Table.Column flexGrow={1} verticalAlign="middle">
        <Table.HeaderCell>Value</Table.HeaderCell>
        <Table.Cell>
          { 
            row => {
              const v = _.toLower(row.key);

              if (v.indexOf('password') > 0 || v.indexOf('secret') > 0 || v.indexOf('pwd') > 0) {
                return <b style={{ fontSize: "2em" }}>&middot;&middot;&middot;&middot;&middot;</b>
              } else {
                return <>{ row.value }</>
              }
            }
          }
        </Table.Cell>
      </Table.Column>
      <Table.Column align="right" verticalAlign="middle">
        <Table.HeaderCell></Table.HeaderCell>
        <Table.Cell>
          { 
            row => {
              return <Whisper trigger="click" speaker={ <Tooltip>Copied!</Tooltip> }>
                  <Button size="sm" appearance="ghost" onClick={ () => copy(row.value) }>
                    <Icon icon="copy" />
                  </Button>
              </Whisper>;
            }
          }
        </Table.Cell>
      </Table.Column>
    </Table>

    <br />

    {
      showDeploymentDetails && <>
        <Button appearance="link" onClick={ () => setShowDeploymetDetails(false) }>Hide Deployment Details</Button>
        <SyntaxHighlighter showLineNumbers language="json" style={docco}>
          { 
            JSON.stringify(_.get(deployedStack, 'deployment.properties') || '', null, 2) 
          }
        </SyntaxHighlighter>
      </> || <Button appearance="link" onClick={ () => setShowDeploymetDetails(true) }>Show Deployment Details</Button>
    }

    <hr />
  </>;
}

StackDeployment.propTypes = {};

export default StackDeployment;
