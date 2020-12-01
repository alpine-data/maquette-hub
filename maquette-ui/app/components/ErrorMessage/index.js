/**
 *
 * ErrorMessage
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Button } from 'rsuite';
// import styled from 'styled-components';

function ErrorMessage({ title, message, onDismiss }) {
  return <div className="mq--error-message">
      { title && <><b>{ title }</b><br /></> }
      <p>
        {message}
      </p>

      { onDismiss && <Button appearance="ghost" onClick={ onDismiss }>Dismiss</Button> }
    </div>;
}

ErrorMessage.propTypes = {
  title: PropTypes.string,
  message: PropTypes.string,
  
  onDismiss: PropTypes.func
};

export default ErrorMessage;
