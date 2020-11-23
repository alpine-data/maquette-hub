/**
 *
 * Project
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
import makeSelectProject from './selectors';
import reducer from './reducer';
import saga from './saga';
import { 
  init as initAction,
  grantAccess as grantAccessAction,
  revokeAccess as revokeAccessAction,
  updateProject,
  updateProject as updateProjectAction } from './actions';

import BadgedButton from 'components/BadgedButton';
import Container from 'components/Container';
import DataShop from '../../components/DataShop';
import EditableParagraph from 'components/EditableParagraph';
import Members from '../../components/Members';
import ResourceSettings from 'components/ResourceSettings';
import Sandboxes from '../../components/Sandboxes';
import Summary from 'components/Summary';

import { Button, ButtonToolbar, Nav, Dropdown, Icon, FlexboxGrid, Affix, Whisper, Tooltip } from 'rsuite';
import { Link } from 'react-router-dom';

function mapPersonalInformationToLabel(personalInformation) {
  switch (personalInformation) {
    case "none":
      return "no personal information";
    case "pi":
      return "personal information";
    case "spi":
      return "sensitive personal information";
  }
}

function DatasetSummary({ project, ds }) {
  return <Summary to={ `/${project}/resources/datasets/${ds.name}` }>
      <Summary.Header icon="table" category="Dataset">{ ds.title }</Summary.Header>
      <Summary.Body>
        { ds.summary }
      </Summary.Body>
      <Summary.Footer>
        { ds.visibility } &middot; { ds.classification } &middot; { mapPersonalInformationToLabel(ds.personalInformation) } 
      </Summary.Footer>
    </Summary>;
}

function Resources({ dispatch, ...props }) {
  const name = _.get(props, 'match.params.name') || 'Unknown Project';
  const summary = _.get(props, 'project.project.summary');

  const datasets = _.get(props, 'project.datasets') || [];

  return <Container md className="mq--main-content">
    <EditableParagraph 
      className="mq--p-leading" 
      value={ summary } 
      onChange={ summary => {
        const title = _.get(props, 'project.project.title');
        const name = _.get(props, 'project.project.name');

        dispatch(updateProject(name, name, title, summary));
      } } />

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
      { _.map(datasets, ds => <DatasetSummary key={ ds.id } project={ name } ds={ ds } />) }
    </Summary.Summaries>

    <p>
      At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
    </p>
  </Container>;
}

function Settings({ dispatch, ...props }) {
  const project = _.get(props, 'match.params.name') || 'Unknown Project';
  const sub = _.get(props, 'match.params.sub') || 'options';

  const members = _.map(_.get(props, 'project.project.authorizations') || [], a => {
    const user = _.get(a, 'authorization.user');
    const role = _.get(a, 'authorization.role');
    const type = _.get(a, 'authorization.type');

    return {
      id: user || role || '',
      name: user && _.capitalize(user),
      type: type,
      role: 'member'
    };
  });

  const roles = [
    {
      "label": "Member",
      "value": "member",
      "role": "Master"
    }
  ];

  return <Container xlg className="mq--main-content">
    <FlexboxGrid>
      <FlexboxGrid.Item colspan={4}>
        <Nav vertical activeKey={ sub } appearance="subtle">
          <Nav.Item eventKey="options" componentClass={ Link } to={ `/${project}/settings` }>Options</Nav.Item>
          <Nav.Item eventKey="members" componentClass={ Link } to={ `/${project}/settings/members` }>Manage members</Nav.Item>
        </Nav>
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={1}></FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={19}>
        { sub == 'options' && <ResourceSettings 
            title={ _.get(props, 'project.project.title') }
            name={ _.get(props, 'project.project.name') } 
            onUpdate={ (title, name) => dispatch(updateProjectAction(project, name, title, _.get(props, 'project.project.summary'))) } /> }

        { sub == 'members' && <Members 
            members={ members } 
            roles={ roles } 
            onMemberAdded={ (type, name) => dispatch(grantAccessAction(project, type, name)) }
            onMemberRemoved={ (type, name) => dispatch(revokeAccessAction(project, type, name)) } /> }
      </FlexboxGrid.Item>      
    </FlexboxGrid>
  </Container>
}

function Display(props) {
  const name = _.get(props, 'match.params.name') || 'Unknown Project';
  const tab = _.get(props, 'match.params.tab') || 'data';
  const title = _.get(props, 'project.project.title') || _.get(props, 'project.project.name');
  const summary = _.get(props, 'project.project.summary');

  return (
    <div>
      <Helmet>
        <title>Project</title>
        <meta name="description" content="Description of Project" />
      </Helmet>

      <Affix top={56}>
        <div className="mq--page-title">
          <Container fluid>
            <FlexboxGrid align="middle">
              <FlexboxGrid.Item colspan={ 20 }>
                <h1><Link to={ `/${name}` }>{ title }</Link></h1>
                <EditableParagraph value={ summary } className="mq--p-leading" />
              </FlexboxGrid.Item>
              <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
                <ButtonToolbar>
                  <Whisper
                    trigger="hover"
                    placement="bottom"
                    speaker={ <Tooltip>3 members</Tooltip> }>
                      
                      <Button size="sm" active><Icon icon="user-o" />&nbsp;&nbsp;3</Button>
                  </Whisper>
                  <Button size="sm" active><Icon icon="bookmark-o" />&nbsp;&nbsp;42</Button>
                </ButtonToolbar>
              </FlexboxGrid.Item>
            </FlexboxGrid>
          </Container>
          
          <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
            <Nav.Item eventKey="data" componentClass={ Link } to={ `/${name}` }>Data</Nav.Item>
            <Nav.Item eventKey="datashop" componentClass={ Link } to={ `/${name}/datashop` }>Data Shop</Nav.Item>
            <Nav.Item eventKey="experiments" componentClass={ Link } to={ `/${name}/experiments` }>Experiments</Nav.Item>
            <Nav.Item eventKey="models" componentClass={ Link } to={ `/${name}/models` }>Models</Nav.Item>
            <Nav.Item eventKey="sandboxes" componentClass={ Link } to={ `/${name}/sandboxes` }>Sandboxes</Nav.Item>
            <Nav.Item eventKey="projects" componentClass={ Link } to={ `/${name}/projects` }>Projects</Nav.Item>
            <Nav.Item eventKey="templates" componentClass={ Link } to={ `/${name}/templates` }>Templates</Nav.Item>
            <Nav.Item eventKey="settings" componentClass={ Link } to={ `/${name}/settings` }>Settings</Nav.Item>
          </Nav>
        </div>
      </Affix>
      
      { tab == 'data' && <Resources { ...props} /> }
      { tab == 'datashop' && <DataShop { ...props} /> }
      { tab == 'settings' && <Settings { ...props} /> }
      { tab == 'sandboxes' && <Sandboxes { ...props} /> }
    </div>
  );
}

function Error(props) {
  const error = _.get(props, 'project.errors.project.response.message') || 'Unknown error occurred.';

  return <div>
    <Helmet>
      <title>Project cannot be displayed &middot; Maquette</title>
    </Helmet>

    <Container md className="mq--main-content">
      <Summary.Summaries>
        <Summary.Empty>
          ¯\_(ツ)_/¯<br />{ error }
        </Summary.Empty>
      </Summary.Summaries>
    </Container>
  </div>;
}

export function Project(props) {
  useInjectReducer({ key: 'project', reducer });
  useInjectSaga({ key: 'project', saga });

  const [initialized, setInitialized] = useState(false);
  const error = _.get(props, 'project.errors.project');
  const loading = _.get(props, 'project.loading');
  const name = _.get(props, 'match.params.name');

  useEffect(() => {
    if (!initialized) {
      props.dispatch(initAction(name));
      setInitialized(true);
    }
  });

  if (!_.isEmpty(loading)) {
    return <>Loading</>;
  } else if (error) {
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
