/**
 *
 * Stream
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
import makeSelectStream from './selectors';
import reducer from './reducer';
import saga from './saga';
import { Affix, FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

import Container from 'components/Container';
import DataAccessRequests from '../../components/DataAccessRequests';
import DataBadges from 'components/DataBadges';
import EditableParagraph from 'components/EditableParagraph';
import StreamOverview from 'components/StreamOverview';

function Display(props) {

  const isOwner = false;
  const stream = 'stock-prices';
  const streamData = { classification: 'internal', personalInformation: 'none', visibility: 'public' }
  const tab = _.get(props, 'match.params.tab') || 'overview';

  return <div>
    <Helmet>
      <title>Stock Prices &middot; Maquette</title>
    </Helmet>

    <Affix top={ 56 }>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 16 }>
            <h1><Link to="/shop/browse">Data Shop</Link> / <Link to={ `/shop/streams/stock-prices` }>Stock Prices</Link></h1>

            <EditableParagraph 
              value="Continuously updated stock prices from common exchanges." 
              onChange={ console.log }
              disabled={ true }
              className="mq--p-leading" />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } className="mq--buttons">
              <DataBadges resource={ streamData  } />
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/shop/streams/${stream}` }>Overview</Nav.Item>
          <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/shop/streams/${stream}/access-requests` }>Access requests</Nav.Item>
        </Nav>
      </div>
    </Affix>

    { 
      tab == 'overview' && <>
        <StreamOverview { ...props } />
      </>
    }

    { 
      tab == 'access-requests' && <>
        <DataAccessRequests 
          { ...props }
          onGrant={ request => props.dispatch(update("datasets access-requests grant", request)) }
          onReject={ request => props.dispatch(update("datasets access-requests reject", request)) }
          onRequest={ request => props.dispatch(update("datasets access-requests update", request)) }
          onWithdraw={ request => props.dispatch(update("datasets access-requests withdraw", request)) } />
      </> 
    }
  </div>;
}

export function Stream(props) {
  useInjectReducer({ key: 'stream', reducer });
  useInjectSaga({ key: 'stream', saga });

  return (
    <Display { ...props } />
  );
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
