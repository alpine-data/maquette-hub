/**
 *
 * CreateProject
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
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
import ViewContainer from '../../components/ViewContainer';

export function CreateProject({ createProject, user, dispatch }) {
  useInjectReducer({ key: 'createProject', reducer });
  useInjectSaga({ key: 'createProject', saga });

 return <ViewContainer
    background="projects"
    titles={ [ { label: 'Create a new project' } ] }
    loading={ _.get(createProject, 'loading') }>

      <Container md>
        <p className="mq--p-leading">
          A project contains all kinds of resources you need to your Data Science and Machine Learning Project.
        </p>

        <hr />

        { 
          createProject.error && <>
            <Message type="error" title="Couldn't create project" description={ createProject.error } style={{ marginBottom: '20px' }} /> 
          </>
        }

        <CreateProjectForm user={ user } onSubmit={ data => dispatch(createProjectAction(data)) } />
      </Container>
  </ViewContainer>
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
