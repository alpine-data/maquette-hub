/**
 *
 * Sandboxes
 *
 */
import _ from 'lodash';
import React from 'react';
import { Link } from 'react-router-dom';
import { Button, FlexboxGrid, Table } from 'rsuite';

import Container from '../Container';
import StackCard from '../StackCard';

import Background from '../../resources/sandboxes-background.png';

function Sandboxes(props) {
  const project = _.get(props, 'project.data.project.name');
  const stacks = _.get(props, 'project.data.project.stacks');
  const sandboxes = _.get(props, 'project.data.project.sandboxes');

  return <Container lg className="mq--main-content" background={ Background }>
    <h4>Create a new Sandbox</h4>
    <p className="mq--p-leading">
      You may add multiple stacks to a single sandbox. Select a stack below to start the setup of a new sandbox.
    </p>
    <FlexboxGrid justify="space-between">
      { 
        _.map(stacks, s => <React.Fragment key={ s.name }>
          <FlexboxGrid.Item colspan={ 11 }>
            <StackCard 
              stack={ s }
              linkTo={ stack => `/new/sandbox?project=${project}&stack=${stack}` } />
          </FlexboxGrid.Item>
        </React.Fragment>)
      }
    </FlexboxGrid>

    { 
      !_.isEmpty(sandboxes) && <>
        <hr />
        <h4>Existing Sandboxes</h4>
        <Table autoHeight data={ sandboxes } rowHeight={ 60 }>
            <Table.Column flexGrow={ 2 } verticalAlign="middle">
              <Table.HeaderCell>Sandbox</Table.HeaderCell>
              <Table.Cell dataKey="name" />
            </Table.Column>

            <Table.Column flexGrow={ 1 } verticalAlign="middle">
              <Table.HeaderCell>Owner</Table.HeaderCell>
              <Table.Cell>
                {
                  row => row.created.by
                }
              </Table.Cell>
            </Table.Column>

            <Table.Column flexGrow={ 2 } verticalAlign="middle">
              <Table.HeaderCell>Created</Table.HeaderCell>
              <Table.Cell>
                {
                  row => new Date(row.created.at).toLocaleString()
                }
              </Table.Cell>
            </Table.Column>

            <Table.Column flexGrow={ 2 } verticalAlign="middle">
              <Table.HeaderCell>Stacks</Table.HeaderCell>
              <Table.Cell>
                {
                  row => _.join(_.map(row.stacks, s => s.configuration.stack), ', ')
                }
              </Table.Cell>
            </Table.Column>

            <Table.Column flexGrow={ 2 } align="right">
              <Table.HeaderCell></Table.HeaderCell>
              <Table.Cell>
                {
                  row => {
                    return <Button 
                      size="sm" 
                      appearance="ghost" 
                      componentClass={ Link } 
                      to={ `/${project}/sandboxes/${row.name}` }>View Details</Button>
                  }
                }
              </Table.Cell>
            </Table.Column>
        </Table>
      </>
    }
  </Container>;
}

Sandboxes.propTypes = {};

export default Sandboxes;
