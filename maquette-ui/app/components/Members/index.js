/**
 *
 * Members
 *
 */
import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { AutoComplete, Button, Icon, SelectPicker, Table } from 'rsuite';

function Members({ roles, members, users, groups, title, onGrant, onRevoke }) {
  const [authType, setAuthType] = useState('user');
  const [authId, setAuthId] = useState('');
  const [authRole, setAuthRole] = useState(_.size(roles) > 0 && roles[0].value || '');

  const newMemberData = [{
    authorization: {
      type: authType,
      name: authId,
    },
    role: authRole
  }]

  return <>
      { title && <h4>{ title }</h4> }
      <Table autoHeight data={ members } rowHeight={ 60 } showHeader={ false }>
        <Table.Column flexGrow={ 13 } verticalAlign="middle">
          <Table.HeaderCell></Table.HeaderCell>
          <Table.Cell>
            {
              row => {
                return <>
                  <span className="mq--sub">{ _.capitalize(row.authorization.type) }</span><br />
                  { row.authorization.name }
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
                  onSelect={ value => onGrant({ authorization: row.authorization, role: value }) } />
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
                    <Button color="red" size="sm" onClick={ () => onRevoke({ authorization: row.authorization }) }><Icon icon="trash-o" /></Button>
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
                  value={ row.authorization.type }
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
                  data={ row.authorization.type == 'user' && users || groups }
                  value={ row.authorization.name }
                  onChange={ name => setAuthId(name) } />
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
                  value={ row.role }
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
                      disabled={ row.authorization.name.length == 0 }
                      size="sm" 
                      onClick={ () => { onGrant(row) } }><Icon icon="plus" /></Button>
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
    authorization: PropTypes.shape({
      type: PropTypes.string.isRequired,
      name: PropTypes.string.isRequired
    }),
    role: PropTypes.string.isRequired
  })),

  roles: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired
  })),

  users: PropTypes.arrayOf(PropTypes.string),
  groups: PropTypes.arrayOf(PropTypes.string),
  title: PropTypes.string,

  onGrant: PropTypes.func,
  onRevoke: PropTypes.func
};

Members.defaultProps = {
  members: [],
  roles: [],
  users: [ 'alice', 'bob', 'clair' ],
  groups: [],
  title: "Manage members",

  onGrant: console.log,
  onRevoke: console.log
}

export default Members;
