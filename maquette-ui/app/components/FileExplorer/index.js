/**
 *
 * FileExplorer
 *
 */
import _ from 'lodash';
import React from 'react';

import { Button, FlexboxGrid, Icon, Table } from 'rsuite';
import { Link } from 'react-router-dom';
import { timeAgo } from '../../utils/helpers'

function FilePreview({ directory, selectedFile, blobBaseUrl }) {
  console.log(directory);
  const file = directory.children[selectedFile];

  return <div className="mq--file-explorer"  style={{ marginTop: "20px" }}>
    <div className="mq--file-explorer--header">
      <b>{ selectedFile }</b>
    </div>
    <FlexboxGrid style={{ margin: "20px 10px"}}>
      <FlexboxGrid.Item colspan={ 6 }>
        <b>Type: </b> { file.fileType }
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 6 }>
        <b>Uploaded by: </b> { file.added.by }
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 6 }>
        <b>Uploaded: </b> { timeAgo(file.added.at) }
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 6 }>
        <b>Size: </b> { file.size.humanized }
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 24 }>
        <hr />
        <div style={{ textAlign: "center" }}>
          <p className="mq--p-leading">No Preview available for this file type.</p>
          <Button 
            href={ `${blobBaseUrl}/${file.name}` } 
            target="_blank"
            appearance="ghost" 
            color="green">Download</Button>
        </div>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </div>;
}

function FileExplorer({ directory, header, selectedFile, treeBaseUrl = '/', blobBaseUrl = '/' }) {
  console.log(directory);
  
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
                      <Link to={ treeBaseUrl + '?file=' + row.name }>{ row.name }</Link>
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

                  if (at) {
                    return <span className="mq--sub">
                      { timeAgo(_.get(row, 'added.at')) } ago
                    </span>;
                  } else {
                    return <></>;
                  }
                  
                }
              }
            </Table.Cell>
          </Table.Column>
        </Table>
      </div>

      { 
        selectedFile && <FilePreview directory={ directory } selectedFile={ selectedFile } blobBaseUrl={ blobBaseUrl } />
      }
    </>;
  }
}

FileExplorer.propTypes = {};

export default FileExplorer;
