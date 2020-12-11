/**
 *
 * CreateCollection
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
import makeSelectCreateCollection from './selectors';
import reducer from './reducer';
import saga from './saga';
import { create } from './actions';

import { Affix, Message } from 'rsuite';

import Background from '../../resources/datashop-background.png';
import Container from 'components/Container'
import CreateCollectionForm from 'components/CreateCollectionForm'

export function CreateCollection(props) {
  useInjectReducer({ key: 'createCollection', reducer });
  useInjectSaga({ key: 'createCollection', saga });

  return (
    <div>
      <Helmet>
        <title>Create a new collection &middot; Maquette</title>
      </Helmet>

      <Affix top={56}>
        <div className="mq--page-title">
          <Container fluid>
            <h1>Create a new collection</h1>
          </Container>
        </div>
      </Affix>

      <Container md className="mq--main-content" background={ Background }>
        <p className="mq--p-leading">
          A collection stores versioned file sets. Each set contains a immutable set of files for further processing.
        </p>

        { 
          _.get(props, 'createCollection.error') && <>
            <Message type="error" title="Couldn't create collection" description={ _.get(props, 'createCollection.error') } /> 
          </>
        }

        <hr />

        <CreateCollectionForm 
            onCreateCollection={ data => props.dispatch(create(data)) }
            { ...props } />
      </Container>
    </div>
  );
}

CreateCollection.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createCollection: makeSelectCreateCollection(),
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

export default compose(withConnect)(CreateCollection);
