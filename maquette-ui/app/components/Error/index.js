/**
 *
 * Error
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Helmet } from 'react-helmet';
import Container from '../Container';
import Summary from '../Summary';
// import styled from 'styled-components';

function Error({ background, message }) {
  return <div>
    <Helmet>
      <title>Error &middot; Maquette</title>
    </Helmet>

    <Container md className="mq--main-content" background={ background }>
      <Summary.Summaries>
        <Summary.Empty>
          ¯\_(ツ)_/¯<br />{ message }
        </Summary.Empty>
      </Summary.Summaries>
    </Container>
  </div>
}

Error.propTypes = {
  background: PropTypes.any,
  message: PropTypes.string
};

export default Error;
