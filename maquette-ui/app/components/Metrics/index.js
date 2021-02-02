/**
 *
 * Metrics
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import { FlexboxGrid, Icon } from 'rsuite';
import { Link } from 'react-router-dom';

const MetricWrapper = styled(FlexboxGrid.Item)`
  border: 1px solid #ccc;
  background-color: rgba(256, 256, 256, 0.7);
  margin-bottom: 20px;
  margin-right: 20px;
  padding: 20px;

  color: #333;
  text-align: center;

  :hover {
    cursor: pointer;
    text-decoration: none;
  }
`

function Metric({ icon, count, label, link, emptyLabel, emptyLink }) {
  if (count <= 0 && emptyLabel) {
    return <MetricWrapper colspan={7} align="center" componentClass={ Link } to={ emptyLink || link }>
      <Icon icon={ icon } size="2x" /><br />
      <p className="mq--p-leading" style={{ marginBottom: 0 }}>{ emptyLabel }</p>
    </MetricWrapper>;
  } else {
    return <MetricWrapper colspan={7} align="center" componentClass={ Link } to={ link }>
      <Icon icon={ icon } size="2x" /><br />
      <p className="mq--p-leading" style={{ marginBottom: 0 }}><b>{ count }</b> { label }</p>
    </MetricWrapper>;
  }
}

function Metrics({ metrics }) {
  return <FlexboxGrid>
    { _.map(metrics, metric => <Metric { ...metric } key={ metric.label } /> )}
  </FlexboxGrid>;
}

Metrics.propTypes = {
  metrics: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    icon: PropTypes.string.isRequired,
    count: PropTypes.number.isRequired,
    link: PropTypes.string.isRequired,

    emptyLabel: PropTypes.string,
    emptyLink: PropTypes.string
  }))
};

export default Metrics;
