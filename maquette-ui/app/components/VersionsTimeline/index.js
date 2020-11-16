/**
 *
 * VersionsTimeline
 *
 */

import _ from 'lodash';
import React, { useState, version } from 'react';
import { Icon, Timeline } from 'rsuite';
import PropTypes from 'prop-types';
import cx from 'classnames';

const DISPLAY_COUNT_MAX = 6;
const DISPLAY_COUNT_REDUCED = 3;

function Version({ version, activeVersion, onSelectVersion = console.log }) {
  const classes = cx("mq--dataset-version", {
    "mq--sub": activeVersion != version.version,
    "mq--selected": activeVersion == version.version
  })

  return <Timeline.Item className="mq--dataset-version" className={ classes } onClick={ () => onSelectVersion(version.version) }>
      <b>v{version.version}</b> 
      <p className="mq--p-leading">{ version.message }</p>
      <p><b>{ version.created.by }</b> published version with { version.records } records &middot; { new Date(version.created.at).toLocaleString() }</p>
    </Timeline.Item>;
}

function VersionsTimeline({ dataset, versions, activeVersion, onSelectVersion }) {
  const [collapsed, setCollapsed] = useState(true);
  
  const splitVersions = () => {
    if (_.size(versions) <= DISPLAY_COUNT_MAX) {
      return [ versions, [] ];
    } else {
      return [ _.slice(versions, 0, DISPLAY_COUNT_REDUCED), _.slice(versions, DISPLAY_COUNT_REDUCED)]
    }
  }

  const [latestVersions, remainingVersions] = splitVersions();

  return <Timeline className="mq--dataset-versions">
    { 
      _.size(latestVersions) > 0 && _.map(latestVersions, version => <Version 
        activeVersion={ activeVersion } 
        key={ version.id } 
        version={ version } 
        onSelectVersion={ onSelectVersion } />) 
    }

    { 
      _.size(remainingVersions) > 0 && <Timeline.Item dot={ <Icon icon="bars" size="2x" /> }>
        <b>{ _.size(remainingVersions) } more versions</b>
        <p>
          <a href="#" onClick={ () => setCollapsed(!collapsed) }>
            { collapsed && <>Show all versions</> || <>Show less versions</> }
          </a>
        </p>
      </Timeline.Item>
    }

    { 
      !collapsed && _.size(remainingVersions) > 0 && _.map(remainingVersions, version => <Version 
        activeVersion={ activeVersion } 
        key={ version.id } 
        version={ version } 
        onSelectVersion={ onSelectVersion } />)
    }
    
    <Timeline.Item>
      <b className="mq--sub">{ new Date(_.get(dataset, 'created.at')).toLocaleString() }</b>
      <p><b>{ _.get(dataset, "created.by") } </b> created the dataset</p>
    </Timeline.Item>
  </Timeline>;
}

VersionsTimeline.propTypes = {
  activeVersion: PropTypes.string,
  dataset: PropTypes.object,
  versions: PropTypes.arrayOf(PropTypes.object),

  onSelectVersion: PropTypes.func
};

export default VersionsTimeline;
