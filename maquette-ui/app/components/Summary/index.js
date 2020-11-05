/**
 *
 * Summary
 *
 */
import React from 'react';
import PropTypes from 'prop-types';
import cx from 'classnames';

import { FlexboxGrid, Icon, Panel, Placeholder } from 'rsuite'
import { Link } from 'react-router-dom';


function Summary({children, to = '#', ...props}) {
  return <Link to={ to } className="mq-summary rs-panel rs-panel-default rs-panel-in" { ...props }>
    { children }
  </Link>;
}

Summary.propTypes = {
  projectTitle: PropTypes.string,
  projectId: PropTypes.string,
  title: PropTypes.string,
  summary: PropTypes.string,
  versions: PropTypes.number,
  records: PropTypes.number,
  lastUpdate: PropTypes.number // unix timestamp
};

Summary.Empty = ({children, className,  ...props}) => {
  return <div className={ cx("mq-summary rs-panel rs-panel-default rs-panel-in mq--summary--empty", className) } { ...props }>
    <FlexboxGrid align="middle" justify="center">
      <FlexboxGrid.Item>
        { children }
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </div>;
}

Summary.Header = ({icon, category, children, className, ...props}) => {
  return <div className={ cx("rs-panel-heading", className) } role="rowheader" tabIndex="-1" { ...props }>
      { category && <div className="mq-summary--type">{ icon && <Icon icon={ icon } /> } { category }</div> }
      { children }
    </div>
}

Summary.Header.propTypes = {
  icon: PropTypes.string,
  category: PropTypes.string
}

Summary.Body = ({ children, className, ...props }) => {
  return <div className={ cx("rs-panel-body", className) } { ...props }>{ children }</div>
}

Summary.Footer = ({ children, className, ...props }) => {
  return <div className={ cx("rs-panel-footer", className) } { ...props }>
    { children }
  </div>
} 

Summary.Summaries = ({ children, className, ...props }) => {
  return <div className={ cx("mq--summaries", className) } { ...props }>
      { children }
    </div>
}

export default Summary;
