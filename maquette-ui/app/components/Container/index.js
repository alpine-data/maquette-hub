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

function Container({ fluid, md, lg, children, className, ...props }) {
  const classNames = (fluid && "mq--container-fluid") || (lg && "mq--container-lg") || (md && "mq--container-md") || "mq--container";

  return <FlexboxGrid justify="center" className={ cx(classNames, className) } { ...props }>
      <FlexboxGrid.Item colspan={ (fluid && 24) || (lg && 14) || (md && 12) || 10 }>
        <Content>
          { children }
        </Content>
      </FlexboxGrid.Item>
  </FlexboxGrid>;
}

Container.propTypes = {
  fluid: propTypes.bool
};

export default Container;
