/**
 *
 * Dataset
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
import makeSelectDataset from './selectors';
import reducer from './reducer';
import saga from './saga';

import Container from 'components/Container';

import { Nav, Dropdown, ButtonToolbar, Icon, FlexboxGrid, Button } from 'rsuite';
import { Link } from 'react-router-dom';

export function Dataset(props) {
  useInjectReducer({ key: 'dataset', reducer });
  useInjectSaga({ key: 'dataset', saga });

  const project = _.get(props, 'match.params.name') || 'Unknown Project';
  const dataset = _.get(props, 'match.params.dataset') || 'Unknown Datasource';
  const tab = _.get(props, 'match.params.tab') || 'overview';

  return (
    <div>
      <Helmet>
        <title>Dataset</title>
        <meta name="description" content="Description of Dataset" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }><h1><Link to={ `/${project}` }>Foo Bar</Link> / <Link to={ `/${project}/resources/datasets/${dataset}` }>Lorem Ipsum Dolor</Link></h1></FlexboxGrid.Item>
            <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
              <Button size="sm" active><Icon icon="heart" /> 42</Button>
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}` }>Overview</Nav.Item>
          <Nav.Item eventKey="data" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/data` }>Data</Nav.Item>
          <Nav.Item eventKey="subscriptions" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/subscriptions` }>Subscriptions</Nav.Item>
          <Nav.Item eventKey="discuss" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/discuss` }>Discuss</Nav.Item>
        </Nav>
      </div>
    </div>
  );
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
