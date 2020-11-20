/**
 *
 * DataBadges
 *
 */

import React from 'react';
import { Tooltip, Whisper } from 'rsuite';
import PropTypes from 'prop-types';
// import styled from 'styled-components';

const visibilityColors = {
  "private": "lightgrey",
  "public": "brightgreen"
}

const visibilityAlt = {
  "private": "The dataset is entirely hidden from other users.",
  "public": "The dataset and its metadata can be seen by everyone. The data is not public."
}

const classificationColors = {
  "public": "brightgreen",
  "internal": "yellow",
  "confidential": "orange",
  "restricted": "red"
}

const classificationAlt = {
  "public": "The data is public information, it can be used by everyone, everywere for any purpose.",
  "internal": "The dataset may contain company internal information. If disclosed, the impact to business is minimal.",
  "confidential": "The dataset may contain business critical internal information. If disclosed, business or brand could be negatively effected.",
  "restricted": "The dataset may contain highly sensitive information. If disclosed, it will have significant financial or legal impact."
}

const piLabels = {
  "none": "None",
  "pi": "Personal Information",
  "spi": "Sensitive Personal Information"
}

const piColors = {
  "none": "brightgreen",
  "pi": "orange",
  "spi": "red"
}

const piAlt = {
  "none": "The data definetely does not contain any personal information.",
  "pi": "The dataset may contain personal information.",
  "spi": "The dataset may contain sensitive personal information according to GDPR."
}

function DataBadges({ resource, ...props }) {
  const visibility = resource.visibility || 'unknown';
  const classification = resource.classification || 'unknown';
  const pi = resource.personalInformation || 'unknown';

  return <p className="mq--p-badges" { ...props }>
    <Whisper
      trigger="hover"
      placement="bottom"
      speaker={
        <Tooltip>{ visibilityAlt[visibility] }</Tooltip>
      }>
      <img 
        src={ "https://img.shields.io/badge/Visibility-" + _.capitalize(visibility) + "-" + visibilityColors[visibility] } 
        alt={ visibilityAlt[visibility] } />
    </Whisper>
    &nbsp;
    <Whisper
      trigger="hover"
      placement="bottom"
      speaker={
        <Tooltip>{ classificationAlt[classification] }</Tooltip>
      }>
      <img 
        src={ "https://img.shields.io/badge/Classification-" + _.capitalize(classification) + "-" + classificationColors[classification] } 
        alt={ visibilityAlt[classification] } />
    </Whisper>
    &nbsp;
    <Whisper
      trigger="hover"
      placement="bottom"
      speaker={
        <Tooltip>{ piAlt[pi] }</Tooltip>
      }>
      <img 
        src={ "https://img.shields.io/badge/Personal Information-" + piLabels[pi] + "-" + piColors[pi] } 
        alt={ visibilityAlt[pi] } />
    </Whisper>
  </p>;
}

DataBadges.propTypes = {
  resource: PropTypes.object
};

export default DataBadges;
