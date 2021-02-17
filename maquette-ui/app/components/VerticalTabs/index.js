/**
 *
 * VerticalTabs
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import { FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

function VerticalTabs({ tabs, ...props }) {
  const active = props.active || _.get(props, 'match.params.tab') || _.first(tabs).key;
  
  return <FlexboxGrid>
    <FlexboxGrid.Item colspan={ 4 }>
      <Nav vertical activeKey={ active } appearance="subtle">
        {
          _.map(_.filter(tabs, 'visible'), tab => <React.Fragment key={ tab.key }>
            <Nav.Item active={ tab.key == active } eventKey={ tab.key } key={ tab.key } componentClass={ Link } to={ tab.link }>{ tab.label }</Nav.Item>
          </React.Fragment>)
        }
      </Nav>
    </FlexboxGrid.Item>

    <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
      {
        _.map(_.filter(tabs, { key: active }), tab => <React.Fragment key={ tab.key }>
          { tab.component() }
        </React.Fragment>)
      }
    </FlexboxGrid.Item>
  </FlexboxGrid>;
}

VerticalTabs.propTypes = {
  active: PropTypes.string.isRequired,
  tabs: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    link: PropTypes.string.isRequired,
    key: PropTypes.string.isRequired,
    component: PropTypes.func.isRequired,
    visible: PropTypes.bool
  }))
};

export default VerticalTabs;
