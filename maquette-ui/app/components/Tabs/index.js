/**
 *
 * Tabs
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
// import styled from 'styled-components';

class Tabs extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      active: 'home'
    };

    this.handleSelect = this.handleSelect.bind(this);
  }
  handleSelect(activeKey) {
    this.setState({ active: activeKey });
  }

}

function Tabs() {
  return <div />;
}

Tabs.propTypes = {
  content: PropTypes.object
};

export default Tabs;
