/**
 *
 * CreateDataSource
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
import makeSelectCreateDataSource from './selectors';
import reducer from './reducer';
import saga from './saga';
import { Affix } from 'rsuite';

import Container from 'components/Container'
import CreateDataSourceForm from 'components/CreateDataSourceForm'

import Background from '../../resources/datashop-background.png';

export function CreateDataSource(props) {
  useInjectReducer({ key: 'createDataSource', reducer });
  useInjectSaga({ key: 'createDataSource', saga });

  return (
    <div>
      <Helmet>
        <title>Create a new data source &middot; Maquette</title>
      </Helmet>

      <Affix top={56}>
        <div className="mq--page-title">
          <Container fluid>
            <h1>Create a new data source</h1>
          </Container>
        </div>
      </Affix>

      <Container md className="mq--main-content" background={ Background }>
        <p className="mq--p-leading">
          A data source allows direct or indirect access to data of a database. The consumer does not need to have the actual database credentials, nor the database driver. Access is possible by using the SDK client only.
        </p>

        { 
          _.get(props, 'createDataSource.error') && <>
            <Message type="error" title="Couldn't create data source" description={ _.get(props, 'createDataSource.error') } /> 
          </>
        }
        <hr />

        <CreateDataSourceForm { ...props } />
      </Container>
    </div>
  );
}

CreateDataSource.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createDataSource: makeSelectCreateDataSource(),
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

export default compose(withConnect)(CreateDataSource);
