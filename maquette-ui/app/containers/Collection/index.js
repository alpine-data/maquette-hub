/**
 *
 * Collection
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
import makeSelectCollection from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load, update, dismissError } from './actions';

import { Affix, FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

import Container from 'components/Container';
import DataAccessRequests from '../../components/DataAccessRequests';
import DataBadges from 'components/DataBadges';
import EditableParagraph from 'components/EditableParagraph';
import Error from '../../components/Error';
import ErrorMessage from '../../components/ErrorMessage';
import CollectionOverview from 'components/CollectionOverview';
import CollectionSettings from 'components/CollectionSettings';

import Background from '../../resources/datashop-background.png';

function Display(props) {
  const collection = _.get(props, 'match.params.collection');
  const tab = _.get(props, 'match.params.tab') || 'overview';

  const isOwner = _.get(props, 'collection.data.isOwner');
  const canAccessData = _.get(props, 'collection.data.canAccessData');
  const error = _.get(props, 'collection.error');

  const onUpdate = (values) => {
    const current = _.pick(
      _.get(props, 'collection.data.collection'), 
      'name', 'title', 'summary', 'visibility', 'classification', 'personalInformation');

    const updated = _.assign(current, values, { collection });

    props.dispatch(update('collections update', updated));
  }

  const onGrant = (value) => {
    const request = _.assign(value, { collection });
    props.dispatch(update('collections grant', request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { collection });
    props.dispatch(update('collections revoke', request));
  }

  return <div>
    <Helmet>
      <title>{ _.get(props, 'collection.data.collection.title') } &middot; Maquette</title>
    </Helmet>

    <Affix top={ 56 }>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 16 }>
            <h1><Link to="/shop/browse">Data Shop</Link> / <Link to={ `/shop/collections/${collection}` }>{ _.get(props, 'collection.data.collection.title') }</Link></h1>

            <EditableParagraph 
                value={ _.get(props, 'collection.data.collection.summary') } 
                onChange={ summary => onUpdate({ summary }) }
                disabled={ !isOwner }
                className="mq--p-leading" />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } className="mq--buttons">
              <DataBadges resource={ _.get(props, 'collection.data.collection') } />
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/shop/collections/${collection}` }>Overview</Nav.Item>

          {
              canAccessData && <Nav.Item eventKey="data" componentClass={ Link } to={ `/shop/collections/${collection}/data` }>Data</Nav.Item>
          }

          <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/shop/collections/${collection}/access-requests` }>Access requests</Nav.Item>

          { 
            isOwner && <Nav.Item eventKey="settings" componentClass={ Link } to={ `/shop/collections/${collection}/settings` }>Settings</Nav.Item>
          }
        </Nav>
      </div>
    </Affix>

    {
      error && <ErrorMessage title="An error occurred saving the changes" message={ error } onDismiss={ () => props.dispatch(dismissError()) } />
    }

    { 
      tab == 'overview' && <>
        <CollectionOverview { ...props } />
      </>
    }

    { 
      tab == 'access-requests' && <>
        <DataAccessRequests 
          { ...props }
          asset={ props.collection.data.collection }
          view={ props.collection }
          onGrant={ request => props.dispatch(update("collections access-requests grant", request)) }
          onReject={ request => props.dispatch(update("collections access-requests reject", request)) }
          onRequest={ request => props.dispatch(update("collections access-requests update", request)) }
          onWithdraw={ request => props.dispatch(update("collections access-requests withdraw", request)) } />
      </> 
    }

    { 
      tab == 'settings' && <>
        <CollectionSettings 
          { ...props }
          onUpdate={ onUpdate }
          onGrant={ onGrant }
          onRevoke={ onRevoke } />
      </>
    }
  </div>;
}

export function Collection(props) {
  useInjectReducer({ key: 'collection', reducer });
  useInjectSaga({ key: 'collection', saga });

  const collection = _.get(props, 'match.params.collection');
  const data = _.get(props, 'collection.data');
  const error = _.get(props, 'collection.error');
  const loading = _.get(props, 'collection.loading');
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(collection))
      setInitialized(true);
    }
  });

  if (!initialized || loading) {
    return <div className="mq--loading" />
  } else if (!data && error) {
    return <Error background={ Background } message={ error } />
  } else {
    return <Display { ...props } />
  }
}

Collection.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  collection: makeSelectCollection(),
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

export default compose(withConnect)(Collection);
