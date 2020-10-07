/**
 *
 * Section
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

const SectionBox = styled.div`
  margin-bottom: 3em;

  :last-child {
    margin-bottom: 0;
  }

  & h5 {
    font-weight: normal;
    font-size: 1.1em;
    margin-bottom: 1em;
  }
`;

function Section({ title, children }) {
  return <SectionBox>
      <h5>{ title }</h5>
      { children }
    </SectionBox>;
}

Section.propTypes = {
  title: PropTypes.string
};

export default Section;
