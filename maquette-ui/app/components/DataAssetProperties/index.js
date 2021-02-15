/**
 *
 * DataAssetProperties
 *
 */

import React from 'react';
import { FlexboxGrid, Icon, Tag, Tooltip, Whisper } from 'rsuite';
import PropTypes from 'prop-types';

const visibilityAlt = {
  "private": "The dataset is entirely hidden from other users.",
  "public": "The dataset and its metadata can be seen by everyone. The data is not public."
}

const classificationAlt = {
  "public": "The data is public information, it can be used by everyone, everywere for any purpose.",
  "internal": "The dataset may contain company internal information. If disclosed, the impact to business is minimal.",
  "confidential": "The dataset may contain business critical internal information. If disclosed, business or brand could be negatively effected.",
  "restricted": "The dataset may contain highly sensitive information. If disclosed, it will have significant financial or legal impact."
}

const piLabels = {
  "none": "None",
  "pi": "PI",
  "spi": "SPI"
}

const piAlt = {
  "none": "The data definetely does not contain any personal information.",
  "pi": "The dataset may contain personal information.",
  "spi": "The dataset may contain sensitive personal information according to GDPR."
}

const zoneAlt = {
  'raw': 'The data as it is stored in the source system or received from an external provider.',
  'prepared': 'The data asset has been cleansed and prepared for a specific use case.',
  'gold': 'The data has the highest possible quality and fullfills all regulatory requirements towards linage and provenance.'
}

const states = {
  'approved': {
    label: 'Approved',
    color: 'green',
    colorCode: '#4caf50',
    textColor: '#fff'
  },
  'review-required': {
    label: 'Review pending',
    color: 'yellow',
    colorCode: '#ffca28',
    textColor: '#333'
  },
  'deprecated': {
    label: 'Deprecated',
    color: 'orange',
    colorCode: '#ff9800',
    textColor: '#333'
  }
}

function Field({ label, value, tooltip }) {
  return <FlexboxGrid.Item colspan={ 5 } style={{ 
      borderLeft: '2px solid #cacaca', 
      height: '60px',
      paddingTop: '5px', 
      paddingLeft: '10px', 
      paddingBottom: '10px' }}>

    <Whisper
      trigger="hover"
      placement="bottom"
      speaker={
        <Tooltip>{ tooltip }</Tooltip>
      }>
      
      <div>
        <b>{ label }</b><br />
        { value }
      </div>
    </Whisper>
  </FlexboxGrid.Item>
}

function ReviewBadge({ state }) {
  return <FlexboxGrid.Item colspan={ 4 } style={{ borderLeft: `2px solid ${states[state].colorCode}`, padding: '12px 0', height: '60px' }}>
    <Tag 
      color={ states[state].color } 
      style={{ padding: '7px 10px', display: 'block', borderTopLeftRadius: 0, borderBottomLeftRadius: 0, color: states[state].textColor }}>
        <Icon icon="check-circle-o" /> { states[state].label }
    </Tag>
  </FlexboxGrid.Item>
}

function DataAssetProperties({ resource }) {
  const visibility = resource.visibility || 'unknown';
  const classification = resource.classification || 'unknown';
  const pi = resource.personalInformation || 'unknown';
  const zone = resource.zone || 'unknown';

  return <FlexboxGrid justify="space-between" style={{ marginBottom: '20px' }}>
    <Field label="Visibility" value={ _.capitalize(visibility) } tooltip={ visibilityAlt[visibility] } />
    <Field label="Classification" value={ _.capitalize(classification) } tooltip={ classificationAlt[classification] } />
    <Field label="Personal Data" value={ piLabels[pi] } tooltip={ piAlt[pi] } />
    <Field label="Stage" value={ _.capitalize(zone) } tooltip={ zoneAlt[zone] } />
    <ReviewBadge state={ resource.state } />
  </FlexboxGrid>
}

DataAssetProperties.propTypes = {
  resource: PropTypes.object
};

export default DataAssetProperties;
