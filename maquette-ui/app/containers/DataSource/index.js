/**
 *
 * DataSource
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
import makeSelectDataSource from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load, update, testConnection } from './actions';

import Background from '../../resources/datashop-background.png';
import Container from 'components/Container';
import DataAccessRequests from '../../components/DataAccessRequests';
import DataBadges from 'components/DataBadges';
import EditableParagraph from 'components/EditableParagraph';
import Error from '../../components/Error';

import { Nav, FlexboxGrid, Affix } from 'rsuite';
import { Link } from 'react-router-dom';
import DataSourceOverview from '../../components/DataSourceOverview';
import DataSourceSettings from '../../components/DataSourceSettings';

function Display(props) {
  const source = _.get(props, 'match.params.source');
  const tab = _.get(props, 'match.params.tab') || 'overview';

  const isOwner = _.get(props, 'dataSource.data.isOwner');
  const canAccessData = _.get(props, 'dataSource.data.canAccessData');
  const error = _.get(props, 'dataSource.error');

  const onUpdate = (values) => {
    const current = _.pick(
      _.get(props, 'dataSource.data.source'), 
      'name', 'title', 'summary', 'visibility', 'classification', 'personalInformation');

    const updated = _.assign(current, values, { source });

    props.dispatch(update('sources update', updated));
  }

  const onUpdateDatabaseProperties = (values) => {
    const updated = _.assign({}, values, { source });
    props.dispatch(update('sources update db', updated));
  }

  const onGrant = (value) => {
    const request = _.assign(value, { source });
    props.dispatch(update('sources grant', request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { source });
    props.dispatch(update('sources revoke', request));
  }

  return <div>
    <Helmet>
      <title>{ _.get(props, 'dataSource.data.source.title') } &middot; Maquette</title>
    </Helmet>

    <Affix top={ 56 }>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 16 }>
              <h1><Link to="/shop/browse">Data Shop</Link> / <Link to={ `/shop/sources/${source}` }>{ _.get(props, 'dataSource.data.source.title') }</Link></h1>
              <EditableParagraph 
                value={ _.get(props, 'dataSource.data.source.summary') } 
                onChange={ summary => onUpdate({ summary }) }
                disabled={ !isOwner }
                className="mq--p-leading" />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } className="mq--buttons">
              <DataBadges resource={ _.get(props, 'dataSource.data.source') } />
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/shop/sources/${source}` }>Overview</Nav.Item>

          {
              canAccessData && <Nav.Item eventKey="data" componentClass={ Link } to={ `/shop/sources/${source}/data` }>Data</Nav.Item>
          }

          <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/shop/sources/${source}/access-requests` }>Access requests</Nav.Item>

          { 
            isOwner && <Nav.Item eventKey="settings" componentClass={ Link } to={ `/shop/sources/${source}/settings` }>Settings</Nav.Item>
          }
        </Nav>
      </div>
    </Affix>

    {
      error && <ErrorMessage title="An error occurred saving the changes" message={ error } onDismiss={ () => props.dispatch(dismissError()) } />
    }

    { 
      tab == 'overview' && <>
        <DataSourceOverview { ...props }  /> 
      </>
    }

    { 
      tab == 'access-requests' && <>
        <DataAccessRequests 
          { ...props }
          asset={ props.dataSource.data.source }
          view={ props.dataSource }
          onGrant={ request => props.dispatch(update("sources access-requests grant", request)) }
          onReject={ request => props.dispatch(update("sources access-requests reject", request)) }
          onRequest={ request => props.dispatch(update("sources access-requests update", request)) }
          onWithdraw={ request => props.dispatch(update("sources access-requests withdraw", request)) } />
      </> 
    }

    { 
      tab == 'settings' && <>
        <DataSourceSettings
          { ...props }
          onTestConnection={ value => props.dispatch(testConnection(value)) }
          onUpdateDatabaseProperties={ onUpdateDatabaseProperties }
          onUpdate={ onUpdate }
          onGrant={ onGrant }
          onRevoke={ onRevoke } />
      </>
    }
  </div>
}

export function DataSource(props) {
  useInjectReducer({ key: 'dataSource', reducer });
  useInjectSaga({ key: 'dataSource', saga });

  const source = _.get(props, 'match.params.source');
  const data = _.get(props, 'dataSource.data');
  const error = _.get(props, 'dataSource.error');
  const loading = _.get(props, 'dataSource.loading');
  
  const [initialized, setInitialized] = useState(false);
  
  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(source))
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

DataSource.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  dataSource: makeSelectDataSource(),
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

export default compose(withConnect)(DataSource);
