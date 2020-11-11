/**
 *
 * Dashboard
 *
 */

import React, { useEffect } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectDashboard from './selectors';
import reducer from './reducer';
import saga from './saga';
import { getProjects as getProjectsAction } from './actions';

import { makeSelectCurrentUser } from '../App/selectors';

import Container from 'components/Container';
import Summary from 'components/Summary';

import { Icon, FlexboxGrid, Button } from 'rsuite';

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

export function Dashboard({ dashboard, user, dispatch, ...props }) {
  useInjectReducer({ key: 'dashboard', reducer });
  useInjectSaga({ key: 'dashboard', saga });

  useEffect(() => {
    if (dashboard.user != user.id) {
      dispatch(getProjectsAction(user.id))
    }
  })

  return (
    <div>
      <Helmet>
        <title>Dashboard - Maquette</title>
        <meta name="description" content="Description of Dashboard" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }>
              <h1>Welcome Alice</h1>
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
              <Button size="sm" active><Icon icon="user-o" />&nbsp;&nbsp;42</Button>
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
      </div>

      <Container md className="mq--main-content">
        <Summary.Summaries>      
          { _.map(dashboard.projects, project => <ProjectSummary key={ project.id } project={ project } />) }
        </Summary.Summaries>
      </Container>
    </div>
  );
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
