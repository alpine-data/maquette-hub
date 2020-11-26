/**
 *
 * CreateDataAccessRequest
 *
 */

import React, { useEffect, useState} from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectCreateDataAccessRequest from './selectors';
import reducer from './reducer';
import saga from './saga';
import { init, submit } from './actions';

import { Affix, Button, ButtonToolbar, Message } from 'rsuite';

import Background from '../../resources/datashop-background.png';

import Container from '../../components/Container';
import CreateDataAccessRequestForm from '../../components/CreateDataAccessRequestForm';
import { Link } from 'react-router-dom';

export function Error({ error }) {
  return <Container md className="mq--main-content" background={ Background }>
    <Message
      type="error"
      title="Something went wrong ..."
      description={
        <p>{ error }</p>
      } />
  </Container>
}

export function Form(props) {
  return <Container md className="mq--main-content" background={ Background }>
    <p className="mq--p-leading">
      To gain access to an data asset for a specific project a data access request must be issued. The request will be answered by the data owner.
    </p>

    <hr />

    <CreateDataAccessRequestForm 
      { ...props } 
      onSubmit={ req => props.dispatch(submit(req)) } />
  </Container>;
}

export function MissingParameters() {
  return <Container md className="mq--main-content" background={ Background }>
    <Message 
      type="error"
      title="Missing parameters"
      description={
        <p>Target project and target data asset are not defined.</p>
      } />
  </Container>
}

export function NoProjects() {
  return <Container md className="mq--main-content" background={ Background }>
    <Message
      type="error"
      title="No available project"
      description={
        <>
          <p>You have no available project which you can request access for.</p>
          <ButtonToolbar>
            <Button color="green" componentClass={ Link } to="/new/project">Create new project</Button>
          </ButtonToolbar>
        </>
      } />
  </Container>
}

export function CreateDataAccessRequest(props) {
  useInjectReducer({ key: 'createDataAccessRequest', reducer });
  useInjectSaga({ key: 'createDataAccessRequest', saga });

  const [initialized, setInitialized] = useState(false);

  const query = new URLSearchParams(_.get(props, 'location.search') || '');
  const targetProject = query.get('project');
  const targetDataset = query.get('asset');

  const missingParameters = !targetProject || !targetDataset;
  const error = _.get(props, 'createDataAccessRequest.error');
  const loading = _.get(props, 'createDataAccessRequest.loading');
  const origins = _.map(
    _.get(props, 'createDataAccessRequest.data.projects') || [],
    origin => {
      return {
        label: origin.title,
        value: origin.name
      }
    });

  useEffect(() => {
    if (!missingParameters && !initialized) {
      props.dispatch(init(targetProject, targetDataset));
      setInitialized(true);
    }
  })

  return (
    <div>
      <Helmet>
        <title>Request data access &middot; Maquette</title>
      </Helmet>

      <Affix top={56}>
        <div className="mq--page-title">
          <Container fluid>
            <h1>Create new data access request</h1>
          </Container>
        </div>
      </Affix>

      {
        (missingParameters && <MissingParameters />) || 
        (loading && <>Loading</>) || 
        (error && <Error error={ error } /> ) || 
        (_.isEmpty(origins) && <NoProjects />) ||
        <Form { ...props } project={ targetProject } asset={ targetDataset } origins={ origins } />
      }
    </div>
  );
}

CreateDataAccessRequest.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createDataAccessRequest: makeSelectCreateDataAccessRequest(),
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

export default compose(withConnect)(CreateDataAccessRequest);
