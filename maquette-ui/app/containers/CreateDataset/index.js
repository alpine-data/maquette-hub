/**
 *
 * CreateDataset
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
import makeSelectCreateDataset from './selectors';
import reducer from './reducer';
import saga from './saga';
import { createDataset as createDatasetAction, loadProjects } from './actions';

import { makeSelectCurrentUser } from '../App/selectors';

import Container from 'components/Container'
import CreateDatasetForm from 'components/CreateDatasetForm'

import { Affix } from 'rsuite';

import Background from '../../resources/datashop-background.png';

export function CreateDataset(props) {
  useInjectReducer({ key: 'createDataset', reducer });
  useInjectSaga({ key: 'createDataset', saga });

  const [initialized, setInitialized] = useState(false);
  const projects = _.get(props, 'createDataset.projects');

  const query = new URLSearchParams(_.get(props, 'location.search') || '');
  const showForm = !_.isEmpty(projects);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(loadProjects());
      setInitialized(true);
    }
  })

  return <div>
    <Helmet>
      <title>Create a new dataset &middot; Maquette</title>
    </Helmet>

    <Affix top={56}>
      <div className="mq--page-title">
        <Container fluid>
          <h1>Create a new dataset</h1>
        </Container>
      </div>
    </Affix>

    <Container md className="mq--main-content" background={ Background }>
      <p className="mq--p-leading">
        A dataset contains structured data records with a consistent schema, just like a data frame. A single data set may contain multiple versions of the dataset with a varying schema.
      </p>

      <hr />

      { 
        showForm && <CreateDatasetForm 
          project={ query.get("project") }
          projects={ projects } 
          onSubmit={ data => props.dispatch(createDatasetAction(data.project, data.title, data.name, data.summary, data.visibility, data.classification, data.personalInformation)) } /> }
    </Container>
  </div>;
}

CreateDataset.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createDataset: makeSelectCreateDataset(),
  user: makeSelectCurrentUser()
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(CreateDataset);
