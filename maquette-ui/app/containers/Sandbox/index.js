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

import ViewContainer from '../../components/ViewContainer';

import { getProjectTabs } from '../Project';

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

  return <>
    <Helmet>
      <title>{ sandbox.name } &middot; { project.title } &middot; Maquette</title>
    </Helmet>

    <Overview { ...props } />
  </>
}

export function Sandbox(props) {
  useInjectReducer({ key: 'sandbox', reducer });
  useInjectSaga({ key: 'sandbox', saga });

  const project = _.get(props, 'match.params.project');
  const sandbox = _.get(props, 'match.params.sandbox');

  const data = _.get(props, 'sandbox.data');
  const [initialized, setInitialized] = useState(initialized, setInitialized);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(project, sandbox));
      setInitialized(true);
    }
  })

  const components = {
    'sandboxes': () => <>
      { 
        data && <Display { ...props } />
      }
    </>
  }

  return <ViewContainer 
    background='projects'
    loading={ _.get(props, 'sandbox.loading') || !initialized }
    error={ _.get(props, 'sandbox.error') }

    likes={ 23 }
    liked={ true }
    onChangeLike={ console.log }

    error={ _.get(props, 'sandbox.error') }
    onCloseError={ () => props.dispatch(dismissError()) }

    titles={ [ 
      { link: `/${project}`, label: _.get(data, 'project.title') || project },
      { link: `/${project}/sandboxes`, label: 'Sandboxes' },
      { label: sandbox }
    ] }
    
    activeTab="sandboxes"
    tabs= { getProjectTabs(project, data.isAdmin, data.isMember, components) } />;
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
