/**
 *
 * Tabs
 *
 */
import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { Nav } from 'rsuite';
// import styled from 'styled-components';

function Tabs({ content }) {
  const [tab, setTab] = useState(content[0].key);

  return <>
    <Nav activeKey={ tab } onSelect={ v => setTab(v) } appearance="subtle">
      { _.map(content, c => <Nav.Item eventKey={ c.key } key={ c.key }>{ c.label }</Nav.Item>) }
    </Nav>

    { _.find(content, c => c.key == tab).component }
  </>;
}

Tabs.propTypes = {
  content: PropTypes.arrayOf(PropTypes.shape({
    key: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    component: PropTypes.elementType.isRequired
  }))
};

export default Tabs;
