/**
 *
 * ProjectExperiments
 *
 */

import React, { useEffect } from 'react';
// import PropTypes from 'prop-types';
import styled from 'styled-components';

const FRAME_ID = 'mlflow_frame';

var IFrame = styled.iframe`
  width: 100%;
  border: 0;
  margin-top: 15px;
`;

function ProjectExperiments() {
  useEffect(() => {
    const interval = setInterval(() => {
      document.getElementById(FRAME_ID).style.height = frames[FRAME_ID].document.body.scrollHeight + "px";
    }, 1500);

    return () => clearInterval(interval);
  });

  return <IFrame 
    name={ FRAME_ID }
    id={ FRAME_ID }
    src="http://localhost:3030/_mlflow/#/" 
    onLoad={ (self) => {
      const css = _.last(frames[FRAME_ID].document.styleSheets);
      css.insertRule('.App-header { display: none }', css.cssRules.length);
      css.insertRule('html, body { overflow: hidden }', css.cssRules.length);
    } } />;
}

ProjectExperiments.propTypes = {};

export default ProjectExperiments;
