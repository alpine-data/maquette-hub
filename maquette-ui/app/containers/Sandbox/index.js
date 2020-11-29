/**
 *
 * Sandbox
 *
 */

import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectSandbox from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load } from './actions';

import Container from 'components/Container';
import StackDeployment from 'components/StackDeployment';

import Background from '../../resources/sandboxes-background.png';

import { Affix, Button, FlexboxGrid, Icon, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

export function Overview(props) {
  const sandbox = _.get(props, 'sandbox.data.sandbox');
  const stacks = _.get(props, 'sandbox.data.stacks');

  return <Container lg className="mq--main-content" background={ Background }>
    { 
      _.map(sandbox.stacks, stack => <StackDeployment stacks={ stacks } deployedStack={ stack } key={ stack.configuration.stack } />)
    } 
  </Container>
}

export function Display(props) {
  const project = _.get(props, 'sandbox.data.project');
  const sandbox = _.get(props, 'sandbox.data.sandbox');
  const tab = _.get(props, 'match.params.id') || 'overview';

  return <div>
    <Helmet>
      <title>{ sandbox.name } &middot; { project.title } &middot; Maquette</title>
    </Helmet>

    <Affix>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }>
              <h1><Link to={ `/${project.name}` }>{ project.title }</Link> / <Link to={ `/${project.name}/sandboxes` }>Sandboxes</Link> / <Link to={ `/${project.name}/sandboxes/${sandbox.name}` }>{ sandbox.name }</Link></h1>
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
              <Button size="sm" active><Icon icon="heart" /> 42</Button>
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>

        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/${project.name}/resources/sandboxes/${sandbox.name}` }>Overview</Nav.Item>
        </Nav>
      </div>
    </Affix>

    { tab == 'overview' && <Overview { ...props } /> }
  </div>
}

export function Sandbox(props) {
  useInjectReducer({ key: 'sandbox', reducer });
  useInjectSaga({ key: 'sandbox', saga });

  const project = _.get(props, 'match.params.project');
  const sandbox = _.get(props, 'match.params.sandbox');

  const data = _.get(props, 'sandbox.data');
  const error = _.get(props, 'sandbox.error');
  const loading = _.get(props, 'sandbox.loading');
  const [initialized, setInitialized] = useState(initialized, setInitialized);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(project, sandbox));
      setInitialized(true);
    }
  })
  
  if (!initialized || loading) {
    return <div className="mq--loading" />
  } else if (!data && error) {
    return <Error background={ Background } message={ error } />
  } else {
    return <Display { ...props } />
  }
}

Sandbox.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  sandbox: makeSelectSandbox(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(Sandbox);
