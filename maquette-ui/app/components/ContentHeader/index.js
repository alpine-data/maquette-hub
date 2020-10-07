/**
 *
 * ContentHeader
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

function ContentHeader({ children, ...props }) {
  return <div className="mq-content-header" { ...props }>
      { children }
  </div>;
}

ContentHeader.propTypes = {};

ContentHeader.Subtitle = ({ children, ...props }) => {
  return <div className="mq-content-header--subtitle" { ...props }>
    { children }
  </div>;
}

ContentHeader.H1 = ({ children, ...props }) => { return <h1 {...props}>{ children }</h1> }
ContentHeader.H2 = ({ children, ...props }) => { return <h2 {...props}>{ children }</h2> }
ContentHeader.H3 = ({ children, ...props }) => { return <h3 {...props}>{ children }</h3> }
ContentHeader.H4 = ({ children, ...props }) => { return <h4 {...props}>{ children }</h4> }
ContentHeader.H5 = ({ children, ...props }) => { return <h5 {...props}>{ children }</h5> }
ContentHeader.H6 = ({ children, ...props }) => { return <h6 {...props}>{ children }</h6> }

export default ContentHeader;
