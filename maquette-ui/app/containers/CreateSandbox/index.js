/**
 *
 * CreateSandbox
 *
 */
import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectCreateSandbox from './selectors';
import reducer from './reducer';
import saga from './saga';
import { init, submit } from './actions';

import Container from '../../components/Container';
import CreateSandboxForm from '../../components/CreateSandboxForm';

import Background from '../../resources/sandboxes-background.png';
import Error from '../../components/Error';

export function CreateSandbox(props) {
  useInjectReducer({ key: 'createSandbox', reducer });
  useInjectSaga({ key: 'createSandbox', saga });

  const [initialized, setInitialized] = useState(false);
  const data = _.get(props, 'createSandbox.data');
  const loading = _.get(props, 'createSandbox.loading');
  const error = _.get(props, 'createSandbox.error');
  const stacks = _.get(props, 'createSandbox.data.stacks');
  const projects = _.get(props, 'createSandbox.data.projects');

  const query = new URLSearchParams(_.get(props, 'location.search') || '');
  const showForm = stacks && projects;

  useEffect(() => {
    if (!initialized) {
      props.dispatch(init());
      setInitialized(true);
    }
  });

  if (!initialized || loading) {
    return <div className="mq--loading" />;
  } else if (!data && error) {
    return <Error background={ Background } message={ error } />
  } else {
    return <div>
      <Helmet>
        <title>CreateSandbox</title>
        <meta name="description" content="Description of CreateSandbox" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <h1>Create a new sandbox</h1>
        </Container>
      </div>

      <Container lg className="mq--main-content" background={ Background }>
        <p className="mq--p-leading">
          A sandbox is an isolated infrastructure environment which may contain multiple stacks of technologies to work with data.
        </p>

        { false && <Message type="error" description={ createProject.error } /> }

        <hr />
        {  
          showForm && <CreateSandboxForm 
            stacks={ stacks } 
            projects={ projects } 
            project={ query.get("project") || projects[0] }
            onSubmit={ request => props.dispatch(submit(request)) } /> 
        }
      </Container>
    </div>;
  }
}

CreateSandbox.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createSandbox: makeSelectCreateSandbox(),
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

export default compose(withConnect)(CreateSandbox);
