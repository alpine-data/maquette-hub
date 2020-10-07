/**
 *
 * BadgedButton
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import { Button, Icon } from 'rsuite';

function BadgedButton({icon, label, children,...props}) {
  return <Button className="mq-badged-button" { ...props }>{ icon && <><Icon icon={ icon } /> </> }{ children } { label && <span className="mq-label">{ label }</span> }</Button>;
}

BadgedButton.propTypes = {
  icon: PropTypes.string,
  label: PropTypes.string
};

export default BadgedButton;
