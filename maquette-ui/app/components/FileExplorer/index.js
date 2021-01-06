/**
 *
 * FileExplorer
 *
 */
import _ from 'lodash';
import React from 'react';

import { formatDistance } from 'date-fns';

import { FlexboxGrid, Icon, Table } from 'rsuite';
import { Link } from 'react-router-dom';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

function FileExplorer({ directory, header, treeBaseUrl = '/', blobBaseUrl = '/' }) {
  var data = _
    .chain(directory.children)
    .map((value, key) => _.assign(value, { name: key }))
    .sortBy('type', 'name')
    .value();
  
  if (_.isEmpty(data)) {
    return <div className="mq--file-explorer">
      <div className="mq--file-explorer--header">
        <b>Empty directory</b>
      </div>
    </div>
  } else {
    return <>
      <div className="mq--file-explorer">
        <div className="mq--file-explorer--header">
          { header }
        </div>
        <Table autoHeight data={ data } showHeader={ false }>
          <Table.Column flexGrow={ 4 }>
            <Table.HeaderCell>Name</Table.HeaderCell>
            <Table.Cell>
              {
                row => {
                  if (row.type == 'directory') {
                    return <>
                      <Icon icon="folder-o" style={{ marginRight: "10px" }} />
                      <Link to={ treeBaseUrl + '/' + row.name }>{ row.name }</Link>
                    </>
                  } else {
                    return <>
                      <Icon icon="file-o" style={{ marginRight: "10px" }} />
                      <Link to={ blobBaseUrl + '/' + row.name }>{ row.name }</Link>
                    </>
                  }
                }
              }
            </Table.Cell>
          </Table.Column>

          <Table.Column flexGrow={ 3 }>
            <Table.HeaderCell>Message</Table.HeaderCell>
            <Table.Cell>
              {
                row => <span className="mq--sub">{ row.message }</span>
              }
            </Table.Cell>
          </Table.Column>

          <Table.Column flexGrow={ 2 } align="right">
            <Table.HeaderCell>Added</Table.HeaderCell>
            <Table.Cell>
              {
                row => {
                  const at = _.get(row, 'added.at');
                  return at && <span className="mq--sub">
                    { formatDistance(new Date(at), new Date()) }
                  </span>
                }
              }
            </Table.Cell>
          </Table.Column>
        </Table>
      </div>
    </>;
  }
}

FileExplorer.propTypes = {};

export default FileExplorer;
