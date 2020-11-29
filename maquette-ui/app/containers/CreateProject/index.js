/**
 *
 * CreateProject
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectCreateProject from './selectors';
import reducer from './reducer';
import saga from './saga';
import { createProject as createProjectAction } from './actions';

import { makeSelectCurrentUser } from '../App/selectors';

import Container from 'components/Container'
import CreateProjectForm from 'components/CreateProjectForm'

import { Message } from 'rsuite';

import Background from '../../resources/projects-background.png';

export function CreateProject({ createProject, user, dispatch }) {
  useInjectReducer({ key: 'createProject', reducer });
  useInjectSaga({ key: 'createProject', saga });

  return (
    <div>
      <Helmet>
        <title>CreateProject</title>
        <meta name="description" content="Description of CreateProject" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <h1>Create a new project</h1>
        </Container>
      </div>

      <Container md className="mq--main-content" background={ Background }>
        <p className="mq--p-leading">
          A project contains all kinds of resources you need to your Data Science and Machine Learning Project.
        </p>

        { createProject.error && <Message type="error" description={ createProject.error } /> }

        <hr />
        <CreateProjectForm user={ user } onSubmit={ data => dispatch(createProjectAction(data)) } />
      </Container>
    </div>
  );
}

CreateProject.propTypes = {
  dispatch: PropTypes.func.isRequired,
  user: PropTypes.object.isRequired
};

const mapStateToProps = createStructuredSelector({
  createProject: makeSelectCreateProject(),
  user: makeSelectCurrentUser()
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

export default compose(withConnect)(CreateProject);
