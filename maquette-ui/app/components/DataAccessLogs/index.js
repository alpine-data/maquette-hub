/**
 *
 * DataAccessLogs
 *
 */
import _ from 'lodash';
import React from 'react';

import Container from '../Container';

import Background from '../../resources/datashop-background.png';
import { Icon, Timeline } from 'rsuite';

import { formatTime } from '../../utils/helpers';

function LogEntry({ log }) {
  console.log(log);
  return <Timeline.Item dot={ <Icon icon="download" size="2x" /> } className="mq--timeline-select--item">
    <p>
      <span className="mq--sub">{ formatTime(log.accessed.at) }</span><br />
      <b>{ log.accessed.by }</b>{ log.project && <> via project <b>{ log.project.title } ({ log.project.name })</b>:</> } { log.message }
    </p>
  </Timeline.Item>
}

function DataAccessLogs(props) {
  const logs = _.reverse(props.logs);
  return <Container lg background={ Background } className="mq--main-content">
      <h4>Access Logs</h4>

      <Timeline className="mq--timeline-select">
        { _.map(logs, log => <LogEntry key={ log } log={ log } />) }

        <Timeline.Item>
          <p>
          <span className="mq--sub">{ formatTime(props.asset.created.at) }</span><br />
            <b>{ props.asset.created.by }</b> created asset
          </p>
        </Timeline.Item>
      </Timeline>
    </Container>;
}

DataAccessLogs.propTypes = {};

export default DataAccessLogs;
