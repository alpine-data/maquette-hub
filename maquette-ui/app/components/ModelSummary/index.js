/**
 *
 * ModelSummary
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import ModernSummary, { TrendMetric, TextMetric } from '../ModernSummary';

import { timeAgo } from '../../utils/helpers';

function ModelSummary({ title, tags, link, warnings, owner, updated }) {
  return <ModernSummary
    title={ title }
    tags={ tags }
    link={ link }
    metrics={ [
      <TrendMetric
        value={ warnings } 
        label="Warnings" 
        text="+3 last 7 days"
        trend="up"
        sentiment="negative" />,
      <TextMetric 
        label="Owner" 
        value={ <Link to={ `/users/${owner.id}` } >{ owner.name }</Link> } />,
      <TextMetric 
        label="Updated" 
        value={ timeAgo(updated) } />
    ] } />
}

ModelSummary.propTypes = {
  title: PropTypes.string.isRequired,
  tags: PropTypes.arrayOf(PropTypes.string),
  link: PropTypes.string.isRequired,
  warnings: PropTypes.number.isRequired,
  owner: PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired
  }),
  updated: PropTypes.string.isRequired
};

export default ModelSummary;
