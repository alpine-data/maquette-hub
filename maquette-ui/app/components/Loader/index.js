/**
 *
 * Loader
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';

import { Loader as LoaderComponent } from 'rsuite';

import LoaderMessages from './loading_messages.json';

function Loader({ message }) {
  return <LoaderComponent vertical size="lg" center content={ message || _.sample(LoaderMessages) } />;
}

Loader.propTypes = {
  message: PropTypes.string
};

export default Loader;
