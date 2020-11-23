/**
 *
 * DataShop
 *
 */

import React, { useState } from 'react';
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

const icons = {
  collection: 'retention',
  dataset: 'table',
  stream: 'realtime',
  source: 'database',
  repository: 'code-fork'
}

function New(props) {
  const project = _.get(props, 'project.project.name');

  return <>
    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['dataset'] } size="2x" className="mq--icon-cycle" />
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
            to={ `/new/dataset?project=${project}` }>Create a dataset</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['stream'] } size="2x" className="mq--icon-cycle" />
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
            to={ `/new/stream?project=${project}` }>Create a stream</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['source'] } size="2x" className="mq--icon-cycle" />
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
            to={ `/new/source?project=${project}` }>Create a data source</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['collection'] } size="2x" className="mq--icon-cycle" />
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
            to={ `/new/collection?project=${project}` }>Create a collection</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['repository'] } size="2x" className="mq--icon-cycle" />
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
            to={ `/new/repository?project=${project}` }>Create a data repository</IconButton>
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

function GetStarted(props) {
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
    <New { ...props } />
  </Container>;
}

/*
 * 
 * Search & Browse
 * 
 */
function Asset({ project, asset }) {
  const type = _.get(asset, 'type');
  const name = _.get(asset, 'name');
  const title = _.get(asset, 'title');
  const summary = _.get(asset, 'summary');
  const updatedAt = new Date(_.get(asset, 'updated.at')).toLocaleString();

  return <Summary to={ `/${project.name}/resources/${type}s/${name}` }>
      <Summary.Header icon={ icons[type] } category={ _.capitalize(type) }>
        { title }
        <DataBadges resource={ asset } style={{ marginBottom: 0, marginTop: "10px" }} />
      </Summary.Header>
      <Summary.Body>
        { summary }
      </Summary.Body>
      <Summary.Footer>
        Last update { updatedAt } &middot; 12 version &middot; 3 fields &middot; 712 records
      </Summary.Footer>
    </Summary>;
}

function Browse(props) {
  const project = _.get(props, 'project.project');
  const assets = _.get(props, 'project.data-assets') || [];

  const [query, setQuery] = useState('');
  const [tab, setTab] = useState('all');
  const [maxCount, setMaxCount] = useState(5);

  const showMore = (event) => {
    event.preventDefault();
    setMaxCount(maxCount + 5);
  }

  const all = _
    .chain(assets)
    .filter(a => {
      if (_.size(query) > 0) {
        const searchstring = a.name + " " + a.title + " " + a.summary;
        return _.lowerCase(searchstring).indexOf(_.lowerCase(query)) >= 0;
      } else {
        return true;
      }
    })
    .sortBy('name')
    .value();

  const datasets = _.filter(all, asset => asset.type == 'dataset');
  const streams = _.filter(all, asset => asset.type == 'stream');
  const sources = _.filter(all, asset => asset.type == 'source');
  const collections = _.filter(all, asset => asset.type == 'collection');
  const repositories = _.filter(all, asset => asset.type == 'repository');

  const lists = { all, datasets, streams, sources, collections, repositories }

  return <Container xlg background={ Background } className="mq--main-content">
    <h4>Browse data assets</h4>
    <Form fluid>
      <FormGroup>
        <InputGroup style={{ width: "100%" }}>
          <Input placeholder="Filter assets" size="lg" value={ query } onChange={ v => setQuery(v) } />
          <InputGroup.Button color="blue">
            <Icon icon="filter" />&nbsp;&nbsp;Filter assets
          </InputGroup.Button>
        </InputGroup>
      </FormGroup>
    </Form>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={4}>
        <Nav vertical activeKey={ tab } onSelect={ v => { setTab(v); setMaxCount(5); } } appearance="subtle">
          <Nav.Item eventKey="all"><Icon icon="circle" /> All results <NavTag>{ _.size(all) }</NavTag></Nav.Item>
          <Nav.Item eventKey="datasets"><Icon icon="table" /> Datasets <NavTag>{ _.size(datasets) }</NavTag></Nav.Item>
          <Nav.Item eventKey="streams"><Icon icon="realtime" /> Streams <NavTag>{ _.size(streams) }</NavTag></Nav.Item>
          <Nav.Item eventKey="sources"><Icon icon="database" /> Sources <NavTag>{ _.size(sources) }</NavTag></Nav.Item>
          <Nav.Item eventKey="collections"><Icon icon="retention" /> Collections <NavTag>{ _.size(collections) }</NavTag></Nav.Item>
          <Nav.Item eventKey="repositories"><Icon icon="code-fork" /> Repositories <NavTag>{ _.size(repositories) }</NavTag></Nav.Item>
        </Nav>

        <br />
        <div style={{ textAlign: "center" }}>
          <Button>Hide linked Assets</Button>
        </div>
        <br />
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={20} style={{ paddingLeft: "20px" }}>
        {
          _.size(lists[tab]) > 0 &&  <>
            <Summary.Summaries style={{ marginTop: 0 }}>
              {
                _.map(_.slice(lists[tab], 0, maxCount), asset => <Asset project={ project } asset={ asset } key={ asset.name } />)
              }
            </Summary.Summaries>

            {
              _.size(lists[tab]) > maxCount && 
                <Divider><a href="#" onClick={ showMore }>Show more - { _.size(lists[tab]) - maxCount } remaining</a></Divider> ||
                <Divider />
            }
          </>
        }
      </FlexboxGrid.Item>
    </FlexboxGrid>

    <h4>
      Create a new data asset
    </h4>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={ 4 } />
      <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
        <New { ...props } />
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Container>
}

function DataShop(props) {
  const assets = _.get(props, 'project.data-assets') || [];

  return _.isEmpty(assets) && <GetStarted {...props} /> || <Browse {...props} />;
}

DataShop.propTypes = {};

export default DataShop;
