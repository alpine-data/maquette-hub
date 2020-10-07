/**
 *
 * Summary
 *
 */
import React from 'react';
import PropTypes from 'prop-types';

import { Icon, Panel, Placeholder } from 'rsuite'

function Summary({children, ...props}) {
  return <a href="#" className="mq-summary rs-panel rs-panel-default rs-panel-in" { ...props }>
    { children }
  </a>;
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

Summary.Header = ({icon, category, children, ...props}) => {
  return <div className="rs-panel-heading" role="rowheader" tabIndex="-1" { ...props }>
      { category && <div className="mq-summary--type">{ icon && <Icon icon={ icon } /> } { category }</div> }
      { children }
    </div>
}

Summary.Header.propTypes = {
  icon: PropTypes.string,
  category: PropTypes.string
}

Summary.Body = ({ children, ...props }) => {
  return <div className="rs-panel-body" { ...props }>{ children }</div>
}

Summary.Footer = ({ children, ...props }) => {
  return <div className="rs-panel-footer" { ...props }>
    { children }
  </div>
} 

export default Summary;
