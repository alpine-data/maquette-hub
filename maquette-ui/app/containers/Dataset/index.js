/**
 *
 * Dataset
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
import makeSelectDataset from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load, update, selectVersion, dismissError } from './actions';

import Container from 'components/Container';
import DataAccessLogs from '../../components/DataAccessLogs';
import DataAccessRequests from '../../components/DataAccessRequests';
import DataBadges from 'components/DataBadges';
import DatasetOverview from '../../components/DatasetOverview';
import DatasetSettings from '../../components/DatasetSettings';
import EditableParagraph from 'components/EditableParagraph';
import Error from '../../components/Error';

import { Nav, FlexboxGrid, Affix } from 'rsuite';
import { Link } from 'react-router-dom';

import Background from '../../resources/datashop-background.png';
import ErrorMessage from '../../components/ErrorMessage';

/**
 * Display tab.
 * 
 * @param {*} props 
 */
function Display(props) {
  const dataset = _.get(props, 'match.params.dataset');
  const tab = _.get(props, 'match.params.tab') || 'overview';

  const isOwner = _.get(props, 'dataset.data.isOwner');
  const canAccessData = _.get(props, 'dataset.data.canAccessData');
  const error = _.get(props, 'dataset.error');

  const onUpdate = (values) => {
    const current = _.pick(
      _.get(props, 'dataset.data.dataset'), 
      'name', 'title', 'summary', 'visibility', 'classification', 'personalInformation');

    const updated = _.assign(current, values, { dataset });

    props.dispatch(update('datasets update', updated));
  }

  const onGrant = (value) => {
    const request = _.assign(value, { dataset });
    props.dispatch(update('datasets grant', request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { dataset });
    props.dispatch(update('datasets revoke', request));
  }

  return <div>
    <Helmet>
      <title>{ _.get(props, 'dataset.data.dataset.title') } &middot; Maquette</title>
    </Helmet>

    <Affix top={ 56 }>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 16 }>
              <h1><Link to="/shop/browse">Data Shop</Link> / <Link to={ `/shop/datasets/${dataset}` }>{ _.get(props, 'dataset.data.dataset.title') }</Link></h1>
              <EditableParagraph 
                value={ _.get(props, 'dataset.data.dataset.summary') } 
                onChange={ summary => onUpdate({ summary }) }
                disabled={ !isOwner }
                className="mq--p-leading" />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } className="mq--buttons">
              <DataBadges resource={ _.get(props, 'dataset.data.dataset') } />
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/shop/datasets/${dataset}` }>Overview</Nav.Item>

          {/*
              canAccessData && <Nav.Item eventKey="data" componentClass={ Link } to={ `/shop/datasets/${dataset}/data` }>Data</Nav.Item>
          */}

          <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/shop/datasets/${dataset}/access-requests` }>Access requests</Nav.Item>

          {
              isOwner && <Nav.Item eventKey="access-logs" componentClass={ Link } to={ `/shop/datasets/${dataset}/access-logs` }>Access logs</Nav.Item>
          }

          { 
            isOwner && <Nav.Item eventKey="settings" componentClass={ Link } to={ `/shop/datasets/${dataset}/settings` }>Settings</Nav.Item>
          }
        </Nav>
      </div>
    </Affix>

    {
      error && <ErrorMessage title="An error occurred saving the changes" message={ error } onDismiss={ () => props.dispatch(dismissError()) } />
    }

    { 
      tab == 'overview' && <>
        <DatasetOverview 
          { ...props } 
          onSelectVersion={ version => props.dispatch(selectVersion(version)) } /> 
      </>
    }

    { 
      tab == 'access-requests' && <>
        <DataAccessRequests 
          { ...props }
          asset={ props.dataset.data.dataset }
          view={ props.dataset }
          onGrant={ request => props.dispatch(update("datasets access-requests grant", request)) }
          onReject={ request => props.dispatch(update("datasets access-requests reject", request)) }
          onRequest={ request => props.dispatch(update("datasets access-requests update", request)) }
          onWithdraw={ request => props.dispatch(update("datasets access-requests withdraw", request)) } />
      </> 
    }

    {
      tab == 'access-logs' && <>
        <DataAccessLogs
          { ...props }
          logs={ props.dataset.data.logs }
          asset={ props.dataset.data.dataset } />
      </>
    }

    { 
      tab == 'settings' && <>
        <DatasetSettings 
          { ...props }
          onUpdate={ onUpdate }
          onGrant={ onGrant }
          onRevoke={ onRevoke } />
      </>
    }
  </div>;
}

/**
 * Component. 
 * 
 * @param {*} props 
 */
export function Dataset(props) {
  useInjectReducer({ key: 'dataset', reducer });
  useInjectSaga({ key: 'dataset', saga });

  const dataset = _.get(props, 'match.params.dataset');
  const data = _.get(props, 'dataset.data');
  const error = _.get(props, 'dataset.error');
  const loading = _.get(props, 'dataset.loading');
  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(dataset))
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

Dataset.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  dataset: makeSelectDataset(),
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

export default compose(withConnect)(Dataset);
