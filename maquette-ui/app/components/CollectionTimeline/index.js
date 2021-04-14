/**
 *
 * CollectionTimeline
 *
 */

import React, { version } from 'react';
import PropTypes from 'prop-types';

import TimelineSelect from '../TimelineSelect'

import { pluralize, timeAgo } from '../../utils/helpers';

function CollectionTimeline({ activeTag, collection, onSelect }) {
  const items = []

  if (collection.customDetails.files.files > 0) {
    items.push({
      key: 'main',
      content: <>
        <b>main</b>
        <p><b>{ collection.customDetails.files.lastModified.added.by }</b> updated collection { timeAgo(collection.customDetails.files.lastModified.added.at) }.</p>
      </>
    })
  }

  _.forEach(collection.customDetails.tags, tag => {
    items.push({
      key: tag.name,
      content: <>
        <b>{ tag.name }</b>
        <p className="mq--p-leading">{ tag.message }</p>
        <p><b>{ tag.content.lastModified.added.by }</b> tagged { pluralize(tag.content.files, 'files') } ({ tag.content.size.humanized })</p>
      </>
    })
  })

  return <TimelineSelect 
    activeKey={ activeTag } 
    moreLabel='tag' 
    items={ items } 
    root={ <>
      <b className="mq--sub">{ new Date(collection.properties.created.at).toLocaleString() }</b>
      <p><b>{ collection.properties.created.by } </b> created the collection</p>
    </>}
    onSelect={ onSelect } />;
}

CollectionTimeline.propTypes = {
  activeTag: PropTypes.string,
  collection: PropTypes.object,
  onSelect: PropTypes.func
};

CollectionTimeline.defaultProps = {
  activeTag: '_latest_',
  collection: {},
  onSelect: console.log
}

export default CollectionTimeline;
