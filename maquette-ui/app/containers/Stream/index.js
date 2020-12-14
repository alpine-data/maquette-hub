/**
 *
 * Stream
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
import makeSelectStream from './selectors';
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
import StreamOverview from 'components/StreamOverview';
import StreamSettings from 'components/StreamSettings';

import Background from '../../resources/datashop-background.png';

function Display(props) {
  const stream = _.get(props, 'match.params.stream');
  const tab = _.get(props, 'match.params.tab') || 'overview';

  const isOwner = _.get(props, 'stream.data.isOwner');
  const canAccessData = _.get(props, 'stream.data.canAccessData');
  const error = _.get(props, 'stream.error');

  const onUpdate = (values) => {
    const current = _.pick(
      _.get(props, 'stream.data.stream'), 
      'name', 'title', 'summary', 'retention', 'schema', 'visibility', 'classification', 'personalInformation');

    const updated = _.assign(current, values, { stream });

    props.dispatch(update('streams update', updated));
  }

  const onGrant = (value) => {
    const request = _.assign(value, { stream });
    props.dispatch(update('streams grant', request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { stream });
    props.dispatch(update('streams revoke', request));
  }

  return <div>
    <Helmet>
      <title>Stock Prices &middot; Maquette</title>
    </Helmet>

    <Affix top={ 56 }>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 16 }>
            <h1><Link to="/shop/browse">Data Shop</Link> / <Link to={ `/shop/streams/${stream}` }>{ _.get(props, 'stream.data.stream.title') }</Link></h1>

            <EditableParagraph 
                value={ _.get(props, 'stream.data.stream.summary') } 
                onChange={ summary => onUpdate({ summary }) }
                disabled={ !isOwner }
                className="mq--p-leading" />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } className="mq--buttons">
              <DataBadges resource={ _.get(props, 'stream.data.stream') } />
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/shop/streams/${stream}` }>Overview</Nav.Item>

          {
              canAccessData && <Nav.Item eventKey="data" componentClass={ Link } to={ `/shop/streams/${stream}/data` }>Data</Nav.Item>
          }

          <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/shop/streams/${stream}/access-requests` }>Access requests</Nav.Item>

          { 
            isOwner && <Nav.Item eventKey="settings" componentClass={ Link } to={ `/shop/streams/${stream}/settings` }>Settings</Nav.Item>
          }
        </Nav>
      </div>
    </Affix>

    {
      error && <ErrorMessage title="An error occurred saving the changes" message={ error } onDismiss={ () => props.dispatch(dismissError()) } />
    }

    { 
      tab == 'overview' && <>
        <StreamOverview { ...props } />
      </>
    }

    { 
      tab == 'access-requests' && <>
        <DataAccessRequests 
          { ...props }
          asset={ props.stream.data.stream }
          view={ props.stream }
          onGrant={ request => props.dispatch(update("streams access-requests grant", request)) }
          onReject={ request => props.dispatch(update("streams access-requests reject", request)) }
          onRequest={ request => props.dispatch(update("streams access-requests update", request)) }
          onWithdraw={ request => props.dispatch(update("streams access-requests withdraw", request)) } />
      </> 
    }

    { 
      tab == 'settings' && <>
        <StreamSettings 
          { ...props }
          onUpdate={ onUpdate }
          onGrant={ onGrant }
          onRevoke={ onRevoke } />
      </>
    }
  </div>;
}

export function Stream(props) {
  useInjectReducer({ key: 'stream', reducer });
  useInjectSaga({ key: 'stream', saga });

  const stream = _.get(props, 'match.params.stream');
  const data = _.get(props, 'stream.data');
  const error = _.get(props, 'stream.error');
  const loading = _.get(props, 'stream.loading');
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(stream))
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

Stream.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  stream: makeSelectStream(),
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

export default compose(withConnect)(Stream);
