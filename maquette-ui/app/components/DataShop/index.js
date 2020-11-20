/**
 *
 * DataShop
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';
import styled from 'styled-components';

import Container from '../Container';
import DataBadges from '../DataBadges';
import StartSearch from '../StartSearch';

import Background from '../../resources/datashop-background.png';
import { Button, ButtonToolbar, Divider, FlexboxGrid, Form, FormGroup, Icon, IconButton, Input, InputGroup, Nav, Tag } from 'rsuite';
import { Link } from 'react-router-dom';
import Summary from '../Summary';

const DataAssetGrid = styled(FlexboxGrid)`
  margin-bottom: 20px;
`;

const NavTag = styled(Tag)`
  position: absolute;
  right: 15px;
`;

function New() {
  return <>
    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon="table" size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Dataset</b>
        <p>
          A dataset contains structured data, like table. A dataset may contain multiple, immutable versions of its data.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to="/new/dataset">Create a dataset</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon="realtime" size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Streams</b>
        <p>
          A stream publishes data as a stream of events/ updates. Existing data is retained for a specified amount of time. A consumer can `listen` to the data.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to="/new/stream">Create a stream</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon="database" size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Data Sources</b>
        <p>
          A data source helps to offer access productive data stores. A consumer can access transactional data stores with near-realtime up-to-date data.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to="/new/source">Create a data source</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon="retention" size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Collections</b>
        <p>
          A collection stores versioned file sets. Each set contains a immutable set of files for further processing.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to="/new/collection">Create a collection</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon="code-fork" size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Data Repository</b>
        <p>
          A data repository can be used by data scientists to store intermediate data objects, e.g. feature-engineered matrices. A data repository cannot be shared across Maquette projects.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to="/new/repository">Create a data repository</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>
  </>
}

/*
 * 
 * Get Started View
 *
 */

function GetStarted() {
  return <Container md background={ Background } className="mq--main-content">
    <h4>Get started with Maquette Data Shop</h4>

    <p className="mq--p-leading">
      Offer, browse and consumer data. Review and monitor the usage of your owned data, assess available data or refine existing data.
    </p>

    <StartSearch 
      title="Search existing data assets" 
      searchAllLabel="Browse existing 428 assets"
      searchLabel="Search within 428 asssets"
      link="/foo" />

    <br /><br />
    <h5>Create a new data asset</h5>
    <New />
  </Container>;
}

/*
 * 
 * Search & Browse
 * 
 */
function Dataset({ project, ds }) {
  return <Summary to={ `/${project}/resources/datasets/${ds.name}` }>
      <Summary.Header icon="table" category="Dataset">
        { ds.title }
        <DataBadges resource={ ds } style={{ marginBottom: 0, marginTop: "10px" }} />
      </Summary.Header>
      <Summary.Body>
        { ds.summary }
      </Summary.Body>
      <Summary.Footer>
        Last update { new Date(ds.updated.at).toLocaleString() } &middot; 12 version &middot; 3 fields &middot; 712 records
      </Summary.Footer>
    </Summary>;
}

function Browse(props) {
  const project = _.get(props, 'project.project');
  const datasets = _.get(props, 'project.datasets') || [];

  return <Container xlg background={ Background } className="mq--main-content">
    <h4>Browse data assets</h4>
    <Form fluid>
      <FormGroup>
        <InputGroup style={{ width: "100%" }}>
          <Input placeholder="Filter assets" size="lg" />
          <InputGroup.Button color="blue">
            <Icon icon="filter" />&nbsp;&nbsp;Filter assets
          </InputGroup.Button>
        </InputGroup>
      </FormGroup>
    </Form>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={4}>
        <Nav vertical activeKey={ "all" } appearance="subtle">
          <Nav.Item eventKey="all" componentClass={ Link } to={ `/settings` }><Icon icon="circle" /> All results <NavTag>12</NavTag></Nav.Item>
          <Nav.Item eventKey="datasets" componentClass={ Link } to={ `/settings` }><Icon icon="table" /> Datasets <NavTag>12</NavTag></Nav.Item>
          <Nav.Item eventKey="streams" componentClass={ Link } to={ `/settings` }><Icon icon="realtime" /> Streams <NavTag>12</NavTag></Nav.Item>
          <Nav.Item eventKey="sources" componentClass={ Link } to={ `/settings` }><Icon icon="database" /> Sources <NavTag>12</NavTag></Nav.Item>
          <Nav.Item eventKey="collections" componentClass={ Link } to={ `/settings` }><Icon icon="retention" /> Collections <NavTag>12</NavTag></Nav.Item>
          <Nav.Item eventKey="repositories" componentClass={ Link } to={ `/settings` }><Icon icon="code-fork" /> Repositories <NavTag>12</NavTag></Nav.Item>
        </Nav>

        <br />
        <div style={{ textAlign: "center" }}>
          <Button>Hide linked Assets</Button>
        </div>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={20} style={{ paddingLeft: "20px" }}>
        <Summary.Summaries style={{ marginTop: 0 }}>
          {
            _.map(datasets, ds => <Dataset project={ project } ds={ ds } key={ ds.name } />)
          }
          <Summary>
            <Summary.Header icon="table" category="Dataset">Hello World</Summary.Header>
            <Summary.Body>Huhu</Summary.Body>
          </Summary>
          <Summary>
            <Summary.Header>Hello World</Summary.Header>
            <Summary.Body>Huhu</Summary.Body>
          </Summary>
        </Summary.Summaries>

        <Divider>Show more - 12 remaining</Divider>
      </FlexboxGrid.Item>
    </FlexboxGrid>

    <h4>
      <span className="mq--sub" style={{ fontWeight: "normal" }}>Not found what you're looking for?</span><br />
      Create a new data asset
    </h4>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={ 4 } />
      <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
        <New />
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Container>
}

function DataShop(props) {
  console.log(props);
  const datasets = _.get(props, 'project.datasets') || [];

  return !_.isEmpty(datasets) && <GetStarted /> || <Browse {...props} />;
}

DataShop.propTypes = {};

export default DataShop;
