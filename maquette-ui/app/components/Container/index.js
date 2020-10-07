import propTypes from 'prop-types';
/**
 *
 * Container
 *
 */

import React from 'react';
import { FlexboxGrid } from 'rsuite';

function Container({ fluid, children, ...props }) {
  return <FlexboxGrid justify="center" { ...props }>
      <FlexboxGrid.Item colspan={ fluid && 24 || 16 }>
        { children }
      </FlexboxGrid.Item>
  </FlexboxGrid>;
}

Container.propTypes = {
  fluid: propTypes.bool
};

export default Container;
