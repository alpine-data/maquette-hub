/**
 *
 * Deployment
 *
 */

import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';

import copy from 'copy-to-clipboard';

import { Button, ButtonToolbar, FlexboxGrid, Icon, Table, Tooltip, Whisper } from 'rsuite';
import { StackIcon } from '../StackCard';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';

SyntaxHighlighter.registerLanguage('json', json);

function Status({ status }) {
  if (status == "STARTED") {
    return <Button color="green" size="sm">Started</Button>;
  } else if (status == "STOPPED") {
    return <Button color="red" size="sm">Stopped</Button>
  }
}

function Deployment({ actionButtons, deployment, icon, title, subtitle, properties }) {
  const [showDeploymentDetails, setShowDeploymetDetails] = useState(false);

  console.log(properties);
  console.log(_.map(properties, (value, key) => { return { value, key } }));

  return <FlexboxGrid>
    <FlexboxGrid.Item colspan={ 24 }>
      <FlexboxGrid align="middle">
        <FlexboxGrid.Item>
          <StackIcon stack={ { icon } } style={{ marginRight: "20px" }} width={ 48 } />
        </FlexboxGrid.Item>
        <FlexboxGrid.Item>
          <h5 style={{ marginBottom: "10px" }}>
            { title }
            { 
              subtitle && <>
                <br /><span className="mq--sub" style={{ fontWeight: "normal" }}>{ subtitle }</span>
              </>
            }            
          </h5>

          <ButtonToolbar>
            <Status status={ _.get(deployment, 'status') } />
            { actionButtons }
          </ButtonToolbar>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </FlexboxGrid.Item>

    { 
      !_.isEmpty(properties) && <>
        <FlexboxGrid.Item colspan={ 24 } style={{ marginTop: '20px' }}>
          <Table data={ _.map(properties, (value, key) => { return { value, key } }) } autoHeight rowHeight={ 60 }>
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
        </FlexboxGrid.Item>
      </>
    }      

    <FlexboxGrid.Item colspan={ 24 } style={{ marginTop: '20px' }}>
    {
      showDeploymentDetails && <>
        <Button appearance="link" onClick={ () => setShowDeploymetDetails(false) }>Hide Deployment Details</Button>
        <SyntaxHighlighter showLineNumbers language="json" style={docco}>
          { 
            JSON.stringify(_.get(deployment, 'properties') || '', null, 2) 
          }
        </SyntaxHighlighter>
      </> || <Button appearance="link" onClick={ () => setShowDeploymetDetails(true) }>Show Deployment Details</Button>
    }
    </FlexboxGrid.Item>
  </FlexboxGrid>;
}

Deployment.propTypes = {
  actionButtons: PropTypes.arrayOf(PropTypes.node),
  icon: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
  subtitle: PropTypes.string,
  deployment: PropTypes.object.isRequired,
  properties: PropTypes.object.isRequired
};

Deployment.defaultProps = {
  actionButtons: [],
  icon: 'python',
  title: 'No title',
  deployment: {},
  properties: {}
}

export default Deployment;
