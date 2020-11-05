/**
 *
 * CreateDataset
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
import makeSelectCreateDataset from './selectors';
import reducer from './reducer';
import saga from './saga';

import { makeSelectCurrentUser } from '../App/selectors';

import Container from 'components/Container'
import CreateDatasetForm from 'components/CreateDatasetForm'

export function CreateDataset({ user, dispatch }) {
  useInjectReducer({ key: 'createDataset', reducer });
  useInjectSaga({ key: 'createDataset', saga });

  return (
    <div>
      <Helmet>
        <title>CreateDataset</title>
        <meta name="description" content="Description of CreateDataset" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <h1>Create a new dataset</h1>
        </Container>
      </div>

      <Container md className="mq--main-content">
        <p className="mq--p-leading">
          A dataset contains structured data records with a consistent schema, just like a data frame. A single data set may contain multiple versions of the dataset with a varying schema.
        </p>
        <hr />
        <CreateDatasetForm user={ user } onSubmit={ data => dispatch(createProject(data)) } />
      </Container>
    </div>
  );
}

CreateDataset.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createDataset: makeSelectCreateDataset(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
    user: makeSelectCurrentUser()
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(CreateDataset);
