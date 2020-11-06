/**
 *
 * Project
 *
 */
import _ from 'lodash';
import React, { useEffect } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectProject from './selectors';
import reducer from './reducer';
import saga from './saga';
import { getProject as getProjectAction } from './actions';

import BadgedButton from 'components/BadgedButton';
import Container from 'components/Container';
import EditableParagraph from 'components/EditableParagraph';
import Summary from 'components/Summary';

import { Nav, Dropdown, ButtonToolbar, Icon, FlexboxGrid, Button } from 'rsuite';
import { Link } from 'react-router-dom';

function Resources(props) {
  let name = _.get(props, 'match.params.name') || 'Unknown Project';
  const summary = _.get(props, 'project.project.summary');

  return <Container md className="mq--main-content">
    <EditableParagraph className="mq--p-leading" value={ summary } />

    <ButtonToolbar>
      <BadgedButton icon="table" label="8" size="sm">Sets</BadgedButton>
      <BadgedButton icon="retention" label="8" size="sm">Collections</BadgedButton>
      <BadgedButton icon="realtime" label="8" size="sm">Streams</BadgedButton>
      <BadgedButton icon="database" label="8" size="sm">Sources</BadgedButton>
      <Dropdown appearance="default" icon={ <Icon icon="plus" /> } color="green" title="Create"  size="sm">
        <Dropdown.Item>New Dataset</Dropdown.Item>
        <Dropdown.Item>New Datasource</Dropdown.Item>
        <Dropdown.Item>New Stream</Dropdown.Item>
        <Dropdown.Item>New Collection</Dropdown.Item>
      </Dropdown>
    </ButtonToolbar>

    <Summary.Summaries>
      <Summary to={ `${name}/resources/datasets/some-project` }>
        <Summary.Header icon="table" category="Data Set">some-dataset/some-project</Summary.Header>
        <Summary.Body>
            Lorem ipsum dolor sit amet
        </Summary.Body>
        <Summary.Footer>
          Hello World! &middot; Huhuuu!
        </Summary.Footer>
      </Summary>

      <Summary>
        <Summary.Header icon="table" category="Data Set">some-dataset/some-project</Summary.Header>
        <Summary.Body>
            Lorem ipsum dolor sit amet
        </Summary.Body>
        <Summary.Footer>
          Hello World! &middot; Huhuuu!
        </Summary.Footer>
      </Summary>
    </Summary.Summaries>

    <p>
      At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
    </p>
  </Container>;
}

function Display(props) {
  const name = _.get(props, 'match.params.name') || 'Unknown Project';
  const tab = _.get(props, 'match.params.tab') || 'resources';
  const title = _.get(props, 'project.project.title') || _.get(props, 'project.project.name');

  return (
    <div>
      <Helmet>
        <title>Project</title>
        <meta name="description" content="Description of Project" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }><h1><Link to={ `/${name}` }>{ title }</Link></h1></FlexboxGrid.Item>
            <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
              <Button size="sm" active><Icon icon="heart" /> 42</Button>
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="resources" componentClass={ Link } to={ `/${name}` }>Resources</Nav.Item>
          <Nav.Item eventKey="workspaces" componentClass={ Link } to={ `/${name}/workspaces` }>Workspaces</Nav.Item>
          <Nav.Item eventKey="members" componentClass={ Link } to={ `/${name}/members` }>Members</Nav.Item>
          <Nav.Item eventKey="settings" componentClass={ Link } to={ `/${name}/settings` }>Settings</Nav.Item>
        </Nav>
      </div>
      
      { tab == 'resources' && <Resources { ...props} /> }
    </div>
  );
}

function Error(props) {
  return <div>
    <Helmet>
      <title>Project</title>
      <meta name="description" content="Description of Project" />
    </Helmet>

    <Container md className="mq--main-content">
      <Summary.Summaries>
        <Summary.Empty>
          ¯\_(ツ)_/¯<br />{ props.project.error }
        </Summary.Empty>
      </Summary.Summaries>
    </Container>
  </div>;
}

export function Project(props) {
  useInjectReducer({ key: 'project', reducer });
  useInjectSaga({ key: 'project', saga });

  let name = _.get(props, 'match.params.name') || 'Unknown Project';

  useEffect(() => {
    if (props.project.id != name) {
      props.dispatch(getProjectAction(name));
    }
  });

  if (props.project.loading) {
    return <></>;
  } else if (props.project.error) {
    return <Error { ...props } />;
  } else {
    return <Display { ...props } />; 
  }
}

Project.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  project: makeSelectProject(),
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

export default compose(withConnect)(Project);
