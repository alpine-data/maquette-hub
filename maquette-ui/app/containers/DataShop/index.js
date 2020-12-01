/**
 *
 * DataShop
 *
 */
import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectDataShop from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load } from './actions';

import Container from '../../components/Container';
import DataAssetBrowser from '../../components/DataAssetBrowser';
import Error from '../../components/Error';
import NewDataAsset from '../../components/NewDataAsset';
import StartSearch from '../../components/StartSearch';

import Background from '../../resources/datashop-background.png';
import { Affix, FlexboxGrid, Nav } from 'rsuite';
import { Link } from 'react-router-dom';

/*
 * 
 * Get Started View
 *
 */

function GetStarted(props) {
  const allAssstes = _.size(_.get(props, 'dataShop.data.allAssets'));

  return <>
    <h4>Get started with Maquette Data Shop</h4>

    <p className="mq--p-leading">
      Offer, browse and consume data. Review and monitor the usage of your owned data, assess available data or refine existing data.
    </p>

    <StartSearch 
      title="Search existing data assets" 
      searchAllLabel={ `Browse existing ${allAssstes} assets` }
      searchLabel= { `Search within ${allAssstes} asssets` }
      link="/shop/browse" />

    <br /><br />
    <h5>Create a new data asset</h5>
    <NewDataAsset { ...props } />
  </>;
}

function Browse(props) {
  const assets = _.get(props, 'dataShop.data.allAssets');

  return <Container xlg background={ Background } className="mq--main-content">
    <DataAssetBrowser {...props} assets={ assets } />
  </Container>;
}

function Owned(props) {
  const userAssets = _.get(props, 'dataShop.data.userAssets');

  if (_.isEmpty(userAssets)) {
    return <Container md background={ Background } className="mq--main-content">
      <GetStarted { ...props } />
    </Container>
  } else {
    return <Container xlg background={ Background } className="mq--main-content">
      <DataAssetBrowser {...props} assets={ userAssets } />
      <br /><br />
      <h4>Create a new data asset</h4>
      <FlexboxGrid>
        <FlexboxGrid.Item colspan={4} />
        <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
          <NewDataAsset { ...props } />
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Container>
  }
}

function Display(props) {
  const tab = _.get(props, 'match.params.tab') || 'owned';

  return (
    <div>
      <Helmet>
        <title>DataShop &middot; Maquette</title>
      </Helmet>

      <Affix top={ 56 }>
        <div className="mq--page-title">
          <Container fluid>
            <h1>Data Shop</h1>
          </Container>
          <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
            <Nav.Item eventKey="owned" componentClass={ Link } to="/shop">Your assets</Nav.Item>
            <Nav.Item eventKey="browse" componentClass={ Link } to="/shop/browse">Browse assets</Nav.Item>
          </Nav>
        </div>
      </Affix>
      { tab == "owned" && <Owned { ...props } /> }
      { tab == "browse" && <Browse { ...props } /> }
    </div>
  );
}

export function DataShop(props) {
  useInjectReducer({ key: 'dataShop', reducer });
  useInjectSaga({ key: 'dataShop', saga });

  const tab = _.get(props, 'match.params.tab') || 'owned';
  const [initialized, setInitialized] = useState(initialized, setInitialized);

  const loading = _.get(props, 'dataShop.loading');
  const data = _.get(props, 'dataShop.data');
  const error = _.get(props, 'dataShop.error');

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(true));
      setInitialized(true);
    }
  })

  if (!initialized || loading) {
    return <div className="mq--loading" />
  } else if (!data && error) {
    return <Error background={ Background } message={ error } />
  } else {
    return <Display { ...props } />
  }
}

DataShop.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  dataShop: makeSelectDataShop(),
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

export default compose(withConnect)(DataShop);
