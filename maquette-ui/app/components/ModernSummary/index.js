/**
 *
 * ModernSummary
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';

import cx from 'classnames';

import { Link } from 'react-router-dom';
import { FlexboxGrid, Icon } from 'rsuite';

export function TrendMetric({ value, label, text, trend, sentiment }) {
  return <FlexboxGrid align="middle" className="mq--metrics--trend">
    <FlexboxGrid.Item className="mq--metrics--trend--value">{ value }</FlexboxGrid.Item>
    <FlexboxGrid.Item>
      <span className="mq--metrics--trend--label">{ label }</span>
      <span className={ `mq--metrics--trend--diff ${sentiment}` }>
        <span className="mq--icon"><Icon icon={ `caret-${trend}` } /></span> { text }
      </span>
    </FlexboxGrid.Item>
  </FlexboxGrid>
}

TrendMetric.propTypes = {
  value: PropTypes.number.isRequired,
  label: PropTypes.string.isRequired,
  text: PropTypes.string.isRequired,
  trend: PropTypes.oneOf(["up", "down", "right"]).isRequired,
  sentiment: PropTypes.oneOf(["positive", "negative", "neutral"]).isRequired
}

TrendMetric.defaultProps = {
  value: 0,
  label: 'Label',
  text: 'No changes',
  trend: 'right',
  sentiment: 'neutral'
}

export function TextMetric({ value, label, icon, sentiment }) {
  return <div className="mq--metrics--text">
    <span className="mq--metrics--text--label">
      { icon && <><Icon icon={ icon } />&nbsp;</> }
      { label }
    </span>
    <span className="mq--metrics--text--value">
      { sentiment && <span className={ sentiment }><Icon icon="dot-circle-o" />&nbsp;</span> }
      { value }
    </span>
  </div>
}

function ModernSummary({ title, tags, metrics, metricColspan, link, appearance, actions, actionsColspan }) {
  let availableColumns = 24 - (1 - _.isEmpty(link));

  if (!_.isEmpty(actions)) {
    availableColumns = availableColumns - actionsColspan;
  }

  const titleColspan = availableColumns - (_.size(metrics) * metricColspan);

  return <div 
    className={ cx({ 
      'mq--modern-summary': true, 
      [`mq--modern-summary-${appearance}`]: true,
      'mq--modern-summary-link': !_.isEmpty(link) }) }>

    <FlexboxGrid align="middle" justify="space-between">
      <FlexboxGrid.Item colspan={ titleColspan }>
        <span className="mq--modern-summary--title">{ title }</span>
        {
          _.size(tags) > 0 && <>
            <ul className="mq--modern-summary--flavours">
              { 
                _.map(tags, tag => <li key={ tag }>{ tag }</li>)
              }
            </ul>   
          </>
        }
      </FlexboxGrid.Item>
      {
        _.map(metrics, (metric, idx) => <React.Fragment key={ `metric-${idx}` }>
          <FlexboxGrid.Item>
            { metric }
          </FlexboxGrid.Item>
        </React.Fragment>)
      }
      {
        link && <>
          <FlexboxGrid.Item colspan={ 1 } style={{ textAlign: 'right' }}>
            <Link to={ link }><Icon icon="ellipsis-h" /></Link>
          </FlexboxGrid.Item>
        </>
      }
      {
        actions && <>
          <FlexboxGrid.Item colspan={ actionsColspan } style={{ textAlign: 'right' }}>
            { actions }
          </FlexboxGrid.Item>
        </>
      }
    </FlexboxGrid>
  </div>;
}

ModernSummary.propTypes = {
  appearance: PropTypes.oneOf(['normal', 'ghost']),
  title: PropTypes.string.isRequired,
  tags: PropTypes.arrayOf(PropTypes.node).isRequired,
  metrics: PropTypes.arrayOf(PropTypes.node).isRequired,
  metricColspan: PropTypes.number.isRequired,
  link: PropTypes.string
};

ModernSummary.defaultProps = {
  metricColspan: 5,
  actionsColspan: 2,
}

export default ModernSummary;
