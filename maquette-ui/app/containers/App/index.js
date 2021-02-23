import React, { useEffect } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import { makeSelectApp }  from './selectors';
import { changeUser, initialize } from './actions';
import reducer from './reducer';
import saga from './saga';

import { Switch, Route } from 'react-router-dom';

import Layout from 'components/Layout';

import Collection from '../Collection/Loadable';
import CreateDataAccessRequest from '../CreateDataAccessRequest/Loadable';
import CreateCollection from 'containers/CreateCollection/Loadable';
import CreateProject from 'containers/CreateProject/Loadable';
import CreateDataset from 'containers/CreateDataset/Loadable';
import CreateSource from 'containers/CreateSource/Loadable';
import CreateSandbox from 'containers/CreateSandbox/Loadable';
import CreateStream from 'containers/CreateStream/Loadable';
import Dashboard from 'containers/Dashboard/Loadable';
import Dataset from 'containers/Dataset/Loadable';
import DataShop from 'containers/DataShop/Loadable';
import NotFoundPage from 'containers/NotFoundPage/Loadable';
import Project from 'containers/Project/Loadable';
import Sandbox from 'containers/Sandbox/Loadable';
import Search from 'containers/Search/Loadable';
import Source from 'containers/Source/Loadable';
import Stream from 'containers/Stream/Loadable';
import UserProfile from 'containers/UserProfile/Loadable';
import UserSettings from 'containers/UserSettings/Loadable';

import GlobalStyle from '../../global-styles';
import './custom-theme.less';

export function App({ app, onInitialize, onUserChanged }) {
  useInjectReducer({ key: 'app', reducer });
  useInjectSaga({ key: 'app', saga });

  useEffect(() => {
    onInitialize();
  }, []);

  return <Layout username={ app.currentUser.name } userId={ app.currentUser.id } onUserChanged={ onUserChanged }>
      <Switch>
        <Route exact path="/" component={Dashboard} />
        <Route path="/search" component={Search} />
        
        <Route path="/new/data-access-request" component={CreateDataAccessRequest} />
        <Route path="/new/collection" component={CreateCollection} />
        <Route path="/new/project" component={CreateProject} />
        <Route path="/new/dataset" component={CreateDataset} />
        <Route path="/new/datasource" component={CreateSource} />
        <Route path="/new/sandbox" component={CreateSandbox} />
        <Route path="/new/stream" component={CreateStream} />

        <Route path="/shop" exact component={DataShop} />
        <Route path="/shop/:tab" exact component={DataShop} />

        <Route path="/shop/collections/:asset" exact component={Collection} />
        <Route path="/shop/collections/:asset/:tab/tree/:tag/*" component={Collection}  />
        <Route path="/shop/collections/:asset/:tab/tree/:tag" component={Collection}  />
        <Route path="/shop/collections/:asset/:tab" exact component={Collection} />
        <Route path="/shop/collections/:asset/:tab/:id" exact component={Collection} />

        <Route path="/shop/datasets/:asset" exact component={Dataset} />
        <Route path="/shop/datasets/:asset/:tab" exact component={Dataset} />
        <Route path="/shop/datasets/:asset/:tab/:id" exact component={Dataset} />
        
        <Route path="/shop/sources/:asset" exact component={Source} />
        <Route path="/shop/sources/:asset/:tab" exact component={Source} />
        <Route path="/shop/sources/:asset/:tab/:id" exact component={Source} />

        <Route path="/shop/streams/:asset" exact component={Stream} />
        <Route path="/shop/streams/:asset/:tab" exact component={Stream} />
        <Route path="/shop/streams/:asset/:tab/:id" exact component={Stream} />

        <Route path="/user/settings" exact component={UserSettings} />
        <Route path="/user/settings/:tab" exact component={UserSettings} />
        <Route path="/users/:id" exact component={UserProfile} />

        <Route path="/:project" exact component={Project} />
        <Route path="/:project/sandboxes/:sandbox" exact component={Sandbox} />

        <Route path="/:project/data/collections/:asset" exact component={Collection} />
        <Route path="/:project/data/collections/:asset/data/tree/:tag/*" component={Collection}  />
        <Route path="/:project/data/collections/:asset/data/tree/:tag" component={Collection}  />
        <Route path="/:project/data/collections/:asset/:tab" exact component={Collection} />
        <Route path="/:project/data/collections/:asset/:tab/:id" exact component={Collection} />

        <Route path="/:project/:tab" exact component={Project} />
        <Route path="/:project/:tab/:id" exact component={Project} />
        <Route path="/:project/:tab/:id/:id2" exact component={Project} />
        <Route path="/:project/:tab/:id/:id2/*" exact component={Project} />

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
    onInitialize: () => dispatch(initialize()),
    onUserChanged: id => dispatch(changeUser(id))
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(App);
