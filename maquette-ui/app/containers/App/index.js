import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import { makeSelectApp }  from './selectors';
import { changeUser } from './actions';
import reducer from './reducer';
import saga from './saga';

import { Switch, Route } from 'react-router-dom';

import Layout from 'components/Layout';

import CreateDataAccessRequest from '../CreateDataAccessRequest';
import CreateProject from 'containers/CreateProject/Loadable';
import CreateDataset from 'containers/CreateDataset/Loadable';
import CreateSandbox from 'containers/CreateSandbox/Loadable';
import Dashboard from 'containers/Dashboard/Loadable';
import Dataset from 'containers/Dataset/Loadable';
import NotFoundPage from 'containers/NotFoundPage/Loadable';
import Project from 'containers/Project/Loadable';
import Sandbox from 'containers/Sandbox/Loadable';
import Search from 'containers/Search/Loadable';

import GlobalStyle from '../../global-styles';
import './custom-theme.less';

export function App({ app, onUserChanged }) {
  useInjectReducer({ key: 'app', reducer });
  useInjectSaga({ key: 'app', saga });

  return <Layout username={ app.currentUser.name } onUserChanged={ onUserChanged }>
      <Switch>
        <Route exact path="/" component={Dashboard} />
        <Route path="/search" component={Search} />
        <Route path="/new/data-access-request" component={CreateDataAccessRequest} />
        <Route path="/new/project" component={CreateProject} />
        <Route path="/new/dataset" component={CreateDataset} />
        <Route path="/new/sandbox" component={CreateSandbox} />
        <Route path="/:name/resources/datasets/:dataset" exact component={Dataset} />
        <Route path="/:name/resources/datasets/:dataset/:tab" exact component={Dataset} />
        <Route path="/:name/resources/datasets/:dataset/:tab/:id" exact component={Dataset} />
        <Route path="/:name/resources/sandboxes/:sandbox" exact component={Sandbox} />
        <Route path="/:name" exact component={Project} />
        <Route path="/:name/:tab" exact component={Project} />
        <Route path="/:name" exact component={Project} />
        <Route path="/:name/:tab/:sub" exact component={Project} />
        <Route component={NotFoundPage} />
      </Switch>
      <GlobalStyle />
    </Layout>;
}

App.propTypes = {
  app: PropTypes.object,
  dispatch: PropTypes.func.isRequired,

  onUserChanged: PropTypes.func
};

const mapStateToProps = createStructuredSelector({
  app: makeSelectApp(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
    onUserChanged: id => dispatch(changeUser(id))
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(App);
