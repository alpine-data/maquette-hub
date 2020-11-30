/**
 *
 * Dashboard
 *
 */

import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectDashboard from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load } from './actions';

import { makeSelectCurrentUser } from '../App/selectors';

import Container from 'components/Container';
import StartSearch from 'components/StartSearch';
import Summary from 'components/Summary';

import Background from '../../resources/platform-background.png'
import Lifecycle from '../../resources/lifecycle.png'
import DataShop from '../../resources/datashop.png'
import Projects from '../../resources/projects.png'

import { Icon, FlexboxGrid, Button, ButtonToolbar, IconButton } from 'rsuite';
import { Link } from 'react-router-dom';

const DataShopTeaser = styled.div`
  background-image: url(${DataShop});
  background-size: 40% auto;
  background-position: top right;
  height: 200px;

  margin-top: 30px;
`;

function ProjectSummary({ project }) {
  return <Summary to={ `/${project.name}` }>
      <Summary.Header icon="project" category="Project">{ project.title }</Summary.Header>
      <Summary.Body>
        { project.summary }
      </Summary.Body>
      <Summary.Footer>
        { new Date(project.modified.at).toLocaleString() } by { project.modified.by }
      </Summary.Footer>
    </Summary>;
}

function Display({ dashboard, user, dispatch, ...props }) {
  var projects = dashboard.data.projects;

  return (
    <div>
      <Helmet>
        <title>Maquette</title>
      </Helmet>

      <Container xlg className="mq--main-content mq--dashboard" background={ Background }>
        <img 
          src={ Lifecycle } 
          alt="Maquette Data Science &amp; Machine Learning Lifecycle"
          width="100%"
          style={{ marginTop: "250px", marginBottom: "100px" }} /> 

        <h3>Welcome to Maquette, { user.name }</h3>

        <FlexboxGrid justify="space-between" style={{ marginTop: "30px", marginBottom: "30px" }} align="middle">
          <FlexboxGrid.Item colspan={ 11 }>
            <p className="mq--p-leading">
              Offer, browse and consume data. Review and monitor the usage of your owned data, assess available data or refine existing data.
            </p>

            <StartSearch 
              searchAllLabel={ `Browse existing assets` }
              searchLabel= { `Search asssets` }
              link="/shop/browse" />
          </FlexboxGrid.Item>
          <FlexboxGrid.Item colspan={ 12 }>
            <img src={ DataShop } alt="Maquette Data Shop" width="100%" />
          </FlexboxGrid.Item>
        </FlexboxGrid>
        
        <hr />

        <FlexboxGrid justify="space-between" style={{ marginTop: "30px", marginBottom: "30px" }} align="top">
          <FlexboxGrid.Item colspan={ 4 }>
            <img src={ Projects } alt="Maquette Data Shop" width="100%" />
          </FlexboxGrid.Item>

          <FlexboxGrid.Item colspan={ 18 }>
            <p className="mq--p-leading">
              A project contains all kinds of resources you need to your Data Science and Machine Learning Project.
            </p>
            <ButtonToolbar>
              <IconButton 
                color="green" 
                placement="right" 
                icon={<Icon icon="arrow-circle-right" />} 
                size="lg"
                componentClass={ Link }
                to="/new/project" >Create Project</IconButton>
            </ButtonToolbar>

            {
              !_.isEmpty(projects) && <>
                <br />
                <hr />
                <br />
                <h4>Your Projects</h4>
                <Summary.Summaries>
                  { _.map(projects, project => <ProjectSummary key={ project.id } project={ project } />) }
                </Summary.Summaries>
              </>
            }
          </FlexboxGrid.Item>
        </FlexboxGrid>
      </Container>
    </div>
  );
}

export function Dashboard(props) {
  useInjectReducer({ key: 'dashboard', reducer });
  useInjectSaga({ key: 'dashboard', saga });

  const data = _.get(props, 'dashboard.data');
  const error = _.get(props, 'dashboard.error');
  const loading = _.get(props, 'dashboard.loading');
  const [initialized, setInitialized] = useState(initialized, setInitialized);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load());
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

Dashboard.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  dashboard: makeSelectDashboard(),
  user: makeSelectCurrentUser()
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

export default compose(withConnect)(Dashboard);
