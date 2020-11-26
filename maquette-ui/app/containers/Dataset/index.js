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
import produce from 'immer';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectDataset from './selectors';
import reducer from './reducer';
import saga from './saga';
import { 
  init as initAction,
  selectVersion as selectVersionAction,
  grantAccess as grantAccessAction,
  revokeAccess as revokeAccessAction,
  updateDataAccessRequest as updateDataAccessRequestAction,
  updateDataset as updateDatasetAction } from './actions';

import Container from 'components/Container';
import DataAccessRequest from 'components/DataAccessRequest';
import DataAccessRequests from '../../components/DataAccessRequests';
import DataAccessRequestSummary from 'components/DataAccessRequestSummary';
import CreateDataAccessRequestForm from 'components/CreateDataAccessRequestForm';
import DataBadges from 'components/DataBadges';
import DatasetOverview from '../../components/DatasetOverview';
import EditableParagraph from 'components/EditableParagraph';
import Members from '../../components/Members';
import ResourceSettings from '../../components/ResourceSettings';
import Summary from '../../components/Summary';
import VersionsTimeline from '../../components/VersionsTimeline';

import { Nav, FlexboxGrid, Button, FormGroup, Form, FormControl, Message, Affix } from 'rsuite';
import { Link } from 'react-router-dom';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';
import DataGovernanceOptions from '../../components/DataGovernanceOptions';

SyntaxHighlighter.registerLanguage('json', json);

/**
 * Access Requests tab.
 * 
 * @param {*} props 
 */
function AccessRequestsHallo(props) {
  const loading = _.get(props, 'dataset.loading');
  const project = _.get(props, 'match.params.name') || 'Unknown Project';
  const projects = _.filter(_.get(props, 'dataset.projects') || [], p => p.name != project)
  const dataset = _.get(props, 'match.params.dataset') || 'Unknown Datasource';
  const id = _.get(props, 'match.params.id') || false;
  const updating = _.get(props, 'dataset.data_access_requests.updating') || false;

  const requests = {};
  requests["all"] = _.get(props, 'dataset.data.accessRequests') || [];
  requests["open"] = _.filter(requests["all"], r => r.status == "requested" || r.status == "rejected");
  requests["active"] = _.filter(requests["all"], r => r.status == "granted");
  requests["closed"] =_.filter(requests["all"], r => r.status == "withdrawn" || r.status == "expired");


  const [state, setState] = useState({
    tab: (requests["open"].length > 0 && "open") || (requests["active"].length > 0 && "active") || "closed"
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  if (loading) {
    return <></>;
  } else if (id == "new") {
    return <Container lg className="mq--main-content">
        <h3>New Data Access Request</h3>
        <hr />
        <p className="mq--p-leading">
          You can request access to the data on behalf of a project you are a member. If the request is granted, you can use the data within this project.
        </p>

        {
          _.size(projects) == 0 && <Message type="warning" description="You are not member of any project to request access for this data." />
        }

        <CreateDataAccessRequestForm 
          projects={ projects } 
          onSubmit={ d => props.dispatch(createDataAccessRequestAction(project, dataset, d.origin, d.reason)) } />
      </Container>;
  } else if (id) {
    const request = _.find(requests["all"], r => r.id == id) || false;

    if (request) {
      return <Container lg className="mq--main-content">
        <DataAccessRequest 
          project={ project } 
          dataset={ dataset } 
          request={ request } 
          updating={ updating }
          onGrant={ args => props.dispatch(updateDataAccessRequestAction("datasets access-requests grant", args)) } 
          onReject={ args => props.dispatch(updateDataAccessRequestAction("datasets access-requests reject", args)) }
          onRequest={ args => props.dispatch(updateDataAccessRequestAction("datasets access-requests update", args)) }
          onWithdraw={ args => props.dispatch(updateDataAccessRequestAction("datasets access-requests withdraw", args)) } />
      </Container>;
    } else {
      return <Container lg className="mq--main-content">
        <Summary.Summaries>
          <Summary.Empty>
            Data Request Access #{id} not found.
          </Summary.Empty>
        </Summary.Summaries>
      </Container>;
    }
  } else {
    return <Container lg className="mq--main-content">

        <Form fluid>
          <FlexboxGrid>
            <FlexboxGrid.Item colspan={ 14 }>
              <FormGroup>
                <FormControl name="filter" size="lg" placeholder="Filter Data Access Requests" />
              </FormGroup>
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 2 }></FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } align="right">
              <Button color="green" size="lg" to={ `/${project}/resources/datasets/${dataset}/access-requests/new` } componentClass={ Link }>Create new Request</Button>
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Form>

        {
          requests["all"].length > 0 && <Summary.Summaries>
            <Summary.Summaries.Header>
                <Nav activeKey={ state.tab } onSelect={ onChange('tab') }>
                  <Nav.Item eventKey="open">Open ({ requests["open"].length })</Nav.Item>
                  <Nav.Item eventKey="active">Active ({ requests["active"].length })</Nav.Item>
                  <Nav.Item eventKey="closed">Closed ({ requests["closed"].length })</Nav.Item>
                </Nav>
              </Summary.Summaries.Header>

              { _.map(requests[state.tab], request => <DataAccessRequestSummary project={ project } dataset={ dataset } request={ request } key={ request.id } />) }

              { requests[state.tab].length == 0 && <Summary.Empty>Sorry. No results here.</Summary.Empty>}
            </Summary.Summaries>
        }

        {
          requests["all"].length == 0 && <Summary.Summaries>
            <Summary.Empty>
              ¯\_(⊙︿⊙)_/¯<br />
              There is nothing here for you.
            </Summary.Empty>
          </Summary.Summaries>
        }
    </Container>;
  }
}

/**
 * Display tab.
 * 
 * @param {*} props 
 */
function Display(props) {
  const project = _.get(props, 'match.params.name');
  const dataset = _.get(props, 'match.params.dataset');
  const tab = _.get(props, 'match.params.tab') || 'overview';

  
  const show = _.get(props, 'dataset.data.dataset.name') || false;

  return <div>
    <Helmet>
      <title>{ _.get(props, 'dataset.data.project.title') } / { _.get(props, 'dataset.data.dataset.title') } &middot; Maquette</title>
    </Helmet>

    <Affix top={ 56 }>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 16 }>
              <h1><Link to={ `/${project}` }>{ _.get(props, 'dataset.data.project.title') }</Link> / <Link to={ `/${project}/resources/datasets/${dataset}` }>{ _.get(props, 'dataset.data.dataset.title') }</Link></h1>
              <EditableParagraph value={ _.get(props, 'dataset.data.dataset.summary') } className="mq--p-leading" />
            </FlexboxGrid.Item>

            <FlexboxGrid.Item colspan={ 8 } className="mq--buttons">
              <DataBadges resource={ _.get(props, 'dataset.data.dataset') } />
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
          <Nav.Item eventKey="overview" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}` }>Overview</Nav.Item>
          <Nav.Item eventKey="data" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/data` }>Data</Nav.Item>
          <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/access-requests` }>Access Requests</Nav.Item>
          <Nav.Item eventKey="discuss" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/discuss` }>Discuss</Nav.Item>
          <Nav.Item eventKey="settings" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/settings` }>Settings</Nav.Item>
        </Nav>
      </div>
    </Affix>

    { 
      show && tab == 'overview' && <>
        <DatasetOverview 
          { ...props } 
          onSelectVersion={ version => props.dispatch(selectVersionAction(version)) } /> 
      </>
    }

    { 
      show && tab == 'access-requests' && <>
        <DataAccessRequests { ...props } />
      </> 
    }

    { 
      show && tab == 'settings' && <>
        <Settings { ...props } /> 
      </>
    }
  </div>;
}

/**
 * Settings tab.
 * @param {*} props 
 */
function Settings({ dispatch, ...props }) {
  const project = _.get(props, 'match.params.name') || 'project';
  const dataset = _.get(props, 'match.params.dataset') || 'dataset';
  const sub = _.get(props, 'match.params.id') || 'options'

  const members = _.map(_.get(props, 'dataset.data.owners') || [], a => {
    const user = _.get(a, 'user');
    const type = _.get(a, 'type');

    return {
      id: user || role || '',
      name: user && _.capitalize(user),
      type: type,
      role: 'owner'
    };
  });

  const roles = [
    {
      "label": "Data Owner",
      "value": "owner",
      "role": "Master"
    }
  ];

  return <Container xlg className="mq--main-content">
    <FlexboxGrid>
      <FlexboxGrid.Item colspan={4}>
        <Nav vertical activeKey={ sub } appearance="subtle">
          <Nav.Item eventKey="options" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/settings` }>Options</Nav.Item>
          <Nav.Item eventKey="governance" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/settings/governance` }>Governance</Nav.Item>
        </Nav>
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={1}></FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={19}>
        { sub == 'options' && <ResourceSettings 
            resource="Dataset"
            title={ _.get(props, 'dataset.data.title') }
            name={ _.get(props, 'dataset.data.name') }
            onUpdate={ (title, name) => {
              const visibility = _.get(props, 'dataset.data.visibility');
              const classification = _.get(props, 'dataset.data.classification');
              const personalInformation = _.get(props, 'dataset.data.personalInformation');
              const summary = _.get(props, 'dataset.data.summary');

              dispatch(updateDatasetAction(project, dataset, name, title, summary, visibility, classification, personalInformation));
            } } /> }

        { sub == 'governance' && <>
            <DataGovernanceOptions 
              visibility={ _.get(props, 'dataset.data.visibility') }
              classification={ _.get(props, 'dataset.data.classification') }
              personalInformation={ _.get(props, 'dataset.data.personalInformation') }
              onUpdate={
                (visibility, classification, personalInformation) => {
                  const title = _.get(props, 'dataset.data.title');
                  const summary = _.get(props, 'dataset.data.summary');

                  dispatch(updateDatasetAction(project, dataset, dataset, title, summary, visibility, classification, personalInformation));
                }
              } />

            <hr />

            <Members 
              title="Manage responsibilities"
              members={ members } 
              roles={ roles } 
              onMemberAdded={ (type, name) => dispatch(grantAccessAction(project, dataset, name)) }
              onMemberRemoved={ (type, name) => dispatch(revokeAccessAction(project, dataset, name)) } /> 
          </>
        }
      </FlexboxGrid.Item>      
    </FlexboxGrid>
  </Container>

}

/**
 * Error Display.
 * 
 * @param {*} props 
 */
function Error(props) {
  return <div>
    <Helmet>
      <title>Dataset</title>
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

/**
 * Component. 
 * 
 * @param {*} props 
 */
export function Dataset(props) {
  useInjectReducer({ key: 'dataset', reducer });
  useInjectSaga({ key: 'dataset', saga });

  const project = _.get(props, 'match.params.name');
  const dataset = _.get(props, 'match.params.dataset');

  const [initialized, setInitialized] = useState(false);

  useEffect(() => {
    if (!initialized) {
      props.dispatch(initAction(project, dataset))
      setInitialized(true);
    }
  });

  if (!initialized || _.size(_.get(props, 'dataset.loading')) > 0) {
    return <>Loading</>
  } else if (props.dataset.error) {
    return <Error { ...props } />
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
