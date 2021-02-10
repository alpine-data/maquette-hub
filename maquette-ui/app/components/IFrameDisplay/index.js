/**
 *
 * IFrameDisplay
 *
 */

import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import Loader from '../Loader';

var IFrame = styled.iframe`
  width: 100%;
  border: 0;
  margin-top: 15px;
`;

function IFrameDisplay({ frameId, onLoad, src }) {
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    const interval = setInterval(() => {  
      document.getElementById(frameId).style.height = frames[frameId].document.body.scrollHeight + "px";
    }, 1500);

    return () => clearInterval(interval);
  });

  return <>
    {
      loaded || <Loader />
    }

    <IFrame 
      name={ frameId }
      id={ frameId }
      src={ src }
      style={{ display: "none" }}
      onLoad={ () => {
        const css = _.last(frames[frameId].document.styleSheets);
        onLoad(css);
        setLoaded(true);

        document.getElementById(frameId).style.display = 'block';
      } } />
  </>;
}

IFrameDisplay.propTypes = {
  frameId: PropTypes.string.isRequired,
  onLoad: PropTypes.func,
  src: PropTypes.string.isRequired
};

IFrameDisplay.defaultProps = {
  onLoad: () => {}
}

export default IFrameDisplay;
