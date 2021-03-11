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

function ModelSummary({ title, tags, link, exceptions, warnings, updatedBy, updated }) {
  return <ModernSummary
    title={ title }
    tags={ tags }
    link={ link }
    metrics={ [
      <TrendMetric
          value={ exceptions }
          label='Exceptions'
          text='Must be fixed'
          trend='right'
          sentiment='negative' />,
      <TrendMetric
        value={ warnings }
        label='Warnings'
        text='Have a look'
        trend='right'
        sentiment='neutral' />,
      <TextMetric 
        label="Updated by" 
        value={ <Link to={ `/users/${updatedBy.id}` } >{ updatedBy.name }</Link> } />,
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
  exceptions: PropTypes.number.isRequired,
  updatedBy: PropTypes.shape({
    id: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired
  }),
  updated: PropTypes.string.isRequired
};

export default ModelSummary;
