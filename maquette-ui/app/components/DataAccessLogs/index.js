/**
 *
 * DataAccessLogs
 *
 */
import _ from 'lodash';
import React, { useMemo, useState } from 'react';

import Container from '../Container';

import { Checkbox, CheckboxGroup, Icon, Table } from 'rsuite';
import { formatTime } from '../../utils/helpers';

function DataAccessLogs(props) {
  const [filter, setFilter] = useState(['read', 'write', 'administration']);
  const logs = useMemo(() => {
    let result = props.logs;
    result = _.filter(result, l => _.indexOf(filter, l.action.category) >= 0);
    result = _.take(result, 100);
    result = _.sortBy(result, l => new Date(l.logged))
    result = _.reverse(result);
    return result;
  }, [ props.logs, filter ]);

  return <Container lg className="mq--main-content">
      <div style={{ textAlign: 'right' }}>
        <CheckboxGroup inline value={ filter } onChange={ v => setFilter(v) }>
          <Icon icon="filter" />
          <Checkbox value="view">View</Checkbox>
          <Checkbox value="read">Read</Checkbox>
          <Checkbox value="write">Write</Checkbox>
          <Checkbox value="administration">Administration</Checkbox>
        </CheckboxGroup>
      </div>

      <Table 
        data={ logs }
        autoHeight
        onRowClick={data => {
          console.log(data);
        }}>
        <Table.Column
          width={ 200 }
          resizable>
          <Table.HeaderCell>Logged</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>{ formatTime(row.logged) }</>
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column
          width={ 80 }
          resizable>
          <Table.HeaderCell>Type</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>{ _.get(row, 'action.category') || '-' }</>
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column
          width={ 120 }
          resizable>
          <Table.HeaderCell>Application</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>{ _.get(row, 'application.name') || 'n/a' }</>
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column
          width={ 120 }
          resizable>
          <Table.HeaderCell>Project</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>{ _.get(row, 'project.name') || 'n/a' }</>
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column
          width={ 250 }
          resizable>
          <Table.HeaderCell>User</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>
                { 
                  row.user && <>
                    <>{ _.get(row, 'user.name') } ({ _.get(row, 'user.id') })</>
                  </> || <>
                    <>n/a</>
                  </>
                }
              </>
            }
          </Table.Cell>
        </Table.Column>
        <Table.Column
          width={ 500 }
          resizable>
          <Table.HeaderCell>Action</Table.HeaderCell>
          <Table.Cell>
            {
              row => <>{ _.get(row, 'action.message') || 'n/a' }</>
            }
          </Table.Cell>
        </Table.Column>
      </Table>
    </Container>;
}

DataAccessLogs.propTypes = {};

export default DataAccessLogs;
