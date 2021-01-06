/**
 *
 * ErrorMessage
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Button } from 'rsuite';

function ErrorMessage({ title, message, onDismiss, dismissLabel = 'Dismiss' }) {
  return <div className="mq--error-message">
      { title && <><b>{ title }</b><br /></> }
      <p>
        {message}
      </p>

      { onDismiss && <Button appearance="ghost" onClick={ onDismiss }>{ dismissLabel }</Button> }
    </div>;
}

ErrorMessage.propTypes = {
  title: PropTypes.string,
  message: PropTypes.string,
  
  onDismiss: PropTypes.func
};

export default ErrorMessage;
