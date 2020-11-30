import propTypes from 'prop-types';
import styled from 'styled-components';
import cx from 'classnames';

/**
 *
 * Container
 *
 */
import React from 'react';
import { FlexboxGrid } from 'rsuite';

const Content = styled.div`
  padding: 20px;
`;

function Container({ background, fluid, md, lg, xlg, children, className, ...props }) {
  const classNames = (fluid && "mq--container-fluid") || (lg && "mq--container-xlg") || (lg && "mq--container-lg") || (md && "mq--container-md") || "mq--container";

  const content = <FlexboxGrid justify="center" className={ cx(classNames, className) } { ...props }>
    <FlexboxGrid.Item colspan={ (fluid && 24) || (xlg && 18) || (lg && 14) || (md && 12) || 10 }>
      <Content>
        { children }
      </Content>
    </FlexboxGrid.Item>
  </FlexboxGrid>;

  if (background) {
    return <div className={ cx("mq--page-background", className) } style={{ backgroundImage: `url(${background})` }}>
      { content }
    </div>
  } else {
    return content;
  }
}

Container.propTypes = {
  background: propTypes.any,
  fluid: propTypes.bool,
  md: propTypes.bool,
  lg: propTypes.bool,
  xlg: propTypes.bool
};

export default Container;
