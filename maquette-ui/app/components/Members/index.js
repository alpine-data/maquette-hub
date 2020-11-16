/**
 *
 * Members
 *
 */
import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { AutoComplete, Button, Icon, SelectPicker, Table } from 'rsuite';
// import styled from 'styled-components';

function Members({ roles, members, users = [], groups = [], onRoleChanged = console.log, onMemberAdded = console.log, onMemberRemoved=console.log }) {
  const [authType, setAuthType] = useState('user');
  const [authId, setAuthId] = useState('');
  const [authRole, setAuthRole] = useState(roles[0].value);

  const newMemberData = [{
    type: authType,
    id: authId,
    role: authRole
  }]

  return <>
      <h4>Manage members</h4>
      <Table autoHeight data={ members } rowHeight={ 60 } showHeader={ false }>
        <Table.Column flexGrow={ 13 } verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            {
              row => {
                return <>
                  <span className="mq--sub">{ _.capitalize(row.type) }</span><br />
                  { row.name || row.id }
                </>
              }
            }
          </Table.Cell>
        </Table.Column>

        <Table.Column flexGrow={ 4 } verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            { 
              row => {
                return <SelectPicker 
                  block 
                  cleanable={ false } 
                  data={ roles } 
                  value={ row["role"] }
                  onSelect={ value => onRoleChanged(row.type, row.id, value) } />
              }
            }
          </Table.Cell>
        </Table.Column>

        <Table.Column flexGrow={ 2 } align="right" verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            { 
              row => {
                return <>
                    <Button color="red" size="sm" onClick={ () => onMemberRemoved(row.type, row.id) }><Icon icon="trash-o" /></Button>
                  </>;
              }
            }
          </Table.Cell>
        </Table.Column>
      </Table>

      <Table autoHeight data={ newMemberData } rowHeight={ 60 } showHeader={ false }>
        <Table.Column flexGrow={ 4 } verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            {
              row => {
                return <SelectPicker 
                  block
                  cleanable={ false }
                  data={ [
                    {
                      label: "User",
                      value: "user"
                    },
                    {
                      label: "LDAP Role",
                      value: "role"
                    }
                  ] }
                  value={ row['type'] }
                  onSelect={ type => setAuthType(type) } />
              }
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column flexGrow={ 9 } verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            {
              row => {
                return <AutoComplete 
                  data={ row['type'] == 'user' && users || groups }
                  value={ row['id'] }
                  onChange={ id => setAuthId(id) } />
              }
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column flexGrow={ 4 } verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            { 
              row => {
                return <SelectPicker 
                  block 
                  cleanable={ false } 
                  data={ roles } 
                  value={ row["role"] }
                  onSelect={ role => setAuthRole(role) } />
              }
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column flexGrow={ 2 } align="right" verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            { 
              row => {
                return <>
                    <Button 
                      color="green" 
                      disabled={ row.id.length == 0 }
                      size="sm" 
                      onClick={ () => {
                        setAuthId('');
                        onMemberAdded(row.type, row.id, row.role);
                      } }><Icon icon="plus" /></Button>
                  </>;
              }
            }
          </Table.Cell>
        </Table.Column>
      </Table>
    </>;
}

Members.propTypes = {
  members: PropTypes.arrayOf(PropTypes.shape({
    id: PropTypes.string.isRequired,
    type: PropTypes.string.isRequired,
    name: PropTypes.string,
    role: PropTypes.string.isRequired
  })),

  roles: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired
  })),

  users: PropTypes.arrayOf(PropTypes.string),
  groups: PropTypes.arrayOf(PropTypes.string),

  onRoleChanged: PropTypes.func,
  onMemberRemoved: PropTypes.func,
  onMemberAdded: PropTypes.func
};

export default Members;
