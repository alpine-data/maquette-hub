/**
 *
 * CreateStream
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
import makeSelectCreateStream from './selectors';
import reducer from './reducer';
import saga from './saga';
import { create } from './actions';

import { Affix, Message } from 'rsuite';
import Container from 'components/Container'
import CreateStreamForm from 'components/CreateStreamForm';
import Background from '../../resources/datashop-background.png';

export function CreateStream(props) {
  useInjectReducer({ key: 'createStream', reducer });
  useInjectSaga({ key: 'createStream', saga });

  return (
    <div>
      <Helmet>
        <title>Create new stream &middot; Maquette</title>
      </Helmet>


      <Affix top={56}>
        <div className="mq--page-title">
          <Container fluid>
            <h1>Create a new stream</h1>
          </Container>
        </div>
      </Affix>

      <Container md className="mq--main-content" background={ Background }>
        <p className="mq--p-leading">
          A stream publishes data as a stream of events/ updates. Existing data is retained for a specified duration. A consumer can `listen` to the data.
        </p>

        { 
          _.get(props, 'createStream.error') && <>
            <Message type="error" title="Couldn't create data source" description={ _.get(props, 'createStream.error') } /> 
          </>
        }
        <hr />

        <CreateStreamForm 
          { ...props }
          onCreateStream={ data => props.dispatch(create(data)) } />
      </Container>
    </div>
  );
}

CreateStream.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createStream: makeSelectCreateStream(),
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

export default compose(withConnect)(CreateStream);
