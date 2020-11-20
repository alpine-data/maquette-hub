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
import { initialize } from './actions';

import Container from 'components/Container';
import StackDeployment from 'components/StackDeployment';

import Background from '../../resources/sandboxes-background.png';

import { Affix, Button, FlexboxGrid, Icon, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

export function Overview(props) {
  const sandbox = _.get(props, 'sandbox.sandbox');
  const stacks = _.get(props, 'sandbox.stacks');

  return <Container lg className="mq--main-content" background={ Background }>
    { 
      _.map(sandbox.stacks, stack => <StackDeployment stacks={ stacks } deployedStack={ stack } key={ stack.configuration.stack } />)
    } 
  </Container>
}

export function Display(props) {
  const project = _.get(props, 'sandbox.project');
  const sandbox = _.get(props, 'sandbox.sandbox');
  const tab = _.get(props, 'match.params.tab') || 'overview';

  return <div>
    <Helmet>
      <title>Sandbox</title>
      <meta name="description" content="Description of Sandbox" />
    </Helmet>

    <Affix>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }>
              <h1><Link to={ `/${project.name}` }>{ project.title }</Link> / <Link to={ `/${project.name}/resources/sandboxes` }>Sandboxes</Link> / <Link to={ `/${project.name}/resources/sandboxes/${sandbox.name}` }>{ sandbox.name }</Link></h1>
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

export function Loading() {
  return <>Loading</>
}

export function Sandbox(props) {
  useInjectReducer({ key: 'sandbox', reducer });
  useInjectSaga({ key: 'sandbox', saga });

  const projectName = _.get(props, 'match.params.name') || 'project';
  const sandboxName = _.get(props, 'match.params.sandbox') || 'sandbox';

  const loading = _.get(props, 'sandbox.loading');
  const isLoading = _.indexOf(loading, 'sandbox') > -1 || _.indexOf(loading, 'project') > -1 || _.indexOf(loading, 'stacks') > -1;

  const [initialized, setInitialized] = useState(initialized, setInitialized);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(initialize(projectName, sandboxName));
      setInitialized(true);
    }
  })
  
  return <>{ isLoading && <Loading { ...props } /> || <Display { ...props } /> }</>
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
