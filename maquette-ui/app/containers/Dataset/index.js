/**
 *
 * Dataset
 *
 */

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
  createDataAccessRequest as createDataAccessRequestAction, 
  getDataset as getDatasetAction,
  updateDataAccessRequest as updateDataAccessRequestAction } from './actions';

import Container from 'components/Container';
import DataAccessRequest from 'components/DataAccessRequest';
import DataAccessRequestSummary from 'components/DataAccessRequestSummary';
import DataAccessRequestForm from 'components/DataAccessRequestForm';
import EditableParagraph from 'components/EditableParagraph';
import Summary from '../../components/Summary';

import { Nav, Icon, FlexboxGrid, Button, InputPicker, FormGroup, Form, FormControl, Message } from 'rsuite';
import { Link } from 'react-router-dom';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';

SyntaxHighlighter.registerLanguage('javascript', json);

function Overview(props) {
  const project = _.get(props, 'match.params.name') || 'unknown';
  const projects = _.filter(_.get(props, 'dataset.projects') || [], p => p.name != project)
  const dataset = _.get(props, 'match.params.dataset') || 'Unknown Datasource';

  const summary = _.get(props, 'dataset.dataset.summary') || '';

  return <Container md className="mq--main-content">
    <EditableParagraph className="mq--p-leading" value={ summary } />

    <p>
      At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
    </p>

    <hr />
    <h4>Schema <InputPicker data={ [] } /></h4>
    <SyntaxHighlighter showLineNumbers language="javascript" style={docco}>
      { 
        JSON.stringify([
          {"name": "value", "type": "long"},             // each element has a long
          {"name": "next", "type": ["null", "LongList"]} // optional next element
        ], null, 2) 
      }
    </SyntaxHighlighter>
  </Container>;
}

function AccessRequests(props) {
  const loading = _.get(props, 'dataset.loading');
  const project = _.get(props, 'match.params.name') || 'Unknown Project';
  const projects = _.filter(_.get(props, 'dataset.projects') || [], p => p.name != project)
  const dataset = _.get(props, 'match.params.dataset') || 'Unknown Datasource';
  const id = _.get(props, 'match.params.id') || false;
  const updating = _.get(props, 'dataset.data_access_requests.updating') || false;

  const requests = {};
  requests["all"] = _.get(props, 'dataset.data_access_requests.requests') || [];
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

        <DataAccessRequestForm 
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

function Display(props) {
  const project = _.get(props, 'match.params.name') || 'Unknown Project';
  const dataset = _.get(props, 'match.params.dataset') || 'Unknown Datasource';
  const tab = _.get(props, 'match.params.tab') || 'overview';

  return <div>
    <Helmet>
      <title>Dataset</title>
      <meta name="description" content="Description of Dataset" />
    </Helmet>

    <div className="mq--page-title">
      <Container fluid>
        <FlexboxGrid align="middle">
          <FlexboxGrid.Item colspan={ 20 }>
            <h1><Link to={ `/${project}` }>{ _.get(props, 'dataset.project.title') }</Link> / <Link to={ `/${project}/resources/datasets/${dataset}` }>{ _.get(props, 'dataset.dataset.title') }</Link></h1>
          </FlexboxGrid.Item>

          <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
            <Button size="sm" active><Icon icon="heart" /> 42</Button>
          </FlexboxGrid.Item>
        </FlexboxGrid>
      </Container>
      
      <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
        <Nav.Item eventKey="overview" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}` }>Overview</Nav.Item>
        <Nav.Item eventKey="data" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/data` }>Data</Nav.Item>
        <Nav.Item eventKey="access-requests" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/access-requests` }>Access Requests</Nav.Item>
        <Nav.Item eventKey="discuss" componentClass={ Link } to={ `/${project}/resources/datasets/${dataset}/discuss` }>Discuss</Nav.Item>
      </Nav>
    </div>

    { tab == 'overview' && <Overview { ...props } /> }
    { tab == 'access-requests' && <AccessRequests { ...props } /> }
  </div>;
}

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

export function Dataset(props) {
  useInjectReducer({ key: 'dataset', reducer });
  useInjectSaga({ key: 'dataset', saga });

  const project = _.get(props, 'match.params.name') || 'Unknown Project';
  const dataset = _.get(props, 'match.params.dataset') || 'Unknown Datasource';

  useEffect(() => {
    if (props.dataset.id != `${project}/${dataset}`) {
      props.dispatch(getDatasetAction(project, dataset));
    }
  });

  if (props.loading) {
    return <></>
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
