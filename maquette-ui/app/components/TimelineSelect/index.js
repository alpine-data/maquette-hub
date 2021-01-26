/**
 *
 * TimelineSelect
 *
 */

import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { Timeline } from 'rsuite';

import cx from 'classnames';
import { pluralize } from '../../utils/helpers'

const DISPLAY_COUNT_MAX = 6;
const DISPLAY_COUNT_REDUCED = 3;

function TimelineItem({ activeKey, item, onSelect }) {
  const classes = cx("mq--timeline-select--item", {
    "mq--sub": activeKey != item.key,
    "mq--selected": activeKey == item.key
  });

  return <Timeline.Item className={ classes } onClick={ () => onSelect(item.key) }>
    { item.content }
  </Timeline.Item>;
}

function TimelineSelect({ activeKey, className, items, moreLabel, root, onSelect }) {
  const [ collapsed, setCollapsed ] = useState(true);

  const splitItems = () => {
    if (_.size(items) <= DISPLAY_COUNT_MAX) {
      return [ items, [] ];
    } else {
      return [ _.slice(items, 0, DISPLAY_COUNT_REDUCED), _.slice(items, DISPLAY_COUNT_REDUCED)]
    }
  }

  const [ latestItems, remainingItems ] = splitItems();

  return <Timeline className={ cx(className, 'mq--timeline-select') }>
    {
      _.size(latestItems) > 0 && _.map(latestItems, item => <TimelineItem
        key={ item.key }
        activeKey={ activeKey }
        item={ item }
        onSelect={ onSelect } />)
    }

    {
      _.size(remainingItems) > 0 && <Timeline.Item key='_separator' dot={ <Icon icon="bars" size="2x" /> }>
        <b>{ pluralize(_.size(remainingItems), moreLabel) } remaining</b>
        <p>
          <a href="#" onClick={ () => setCollapsed(!collapsed) }>
            { collapsed && <>Show all</> || <>Show less</> }
          </a>
        </p>
      </Timeline.Item>
    }

    {
      _.size(remainingItems) > 0 && _.map(remainingItems, item => (!collapsed || item.force || activeKey == item.key) && <TimelineItem
        activeKey={ activeKey }
        item={ item }
        onSelect={ onSelect } /> || <></>)
    }

    {
      root && <Timeline.Item>{ root }</Timeline.Item>
    }
  </Timeline>;
}

TimelineSelect.propTypes = {
  activeKey: PropTypes.string,

  items: PropTypes.arrayOf(PropTypes.shape({
    key: PropTypes.string,
    content: PropTypes.node,
    force: PropTypes.bool
  })),

  moreLabel: PropTypes.string,
  root: PropTypes.node,

  onSelect: PropTypes.func
};

TimelineSelect.defaultProps = {
  items: [],
  moreLabel: 'item',
  onSelect: console.log
}

export default TimelineSelect;
