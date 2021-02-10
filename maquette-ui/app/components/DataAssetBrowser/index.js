/**
 *
 * DataAssetBrowser
 *
 */

import React, { useState } from 'react';
import styled from 'styled-components';

import { Divider, FlexboxGrid, FormGroup, Form, Icon, Input, InputGroup, Nav, Tag } from 'rsuite';
import DataBadges from '../DataBadges';
import Summary from '../Summary';
import NewDataAsset, { icons } from '../NewDataAsset';

const NavTag = styled(Tag)`
  position: absolute;
  right: 15px;
`;

export function Asset({ asset }) {
  const type = _.get(asset, 'type');
  const name = _.get(asset, 'name');
  const title = _.get(asset, 'title');
  const summary = _.get(asset, 'summary');
  const updatedAt = new Date(_.get(asset, 'updated.at')).toLocaleString();

  return <Summary to={ `/shop/${type}s/${name}` }>
      <Summary.Header icon={ icons[type] } category={ _.capitalize(type) }>
        { title }
        <DataBadges resource={ asset } style={{ marginBottom: 0, marginTop: "10px" }} />
      </Summary.Header>
      <Summary.Body>
        { summary }
      </Summary.Body>
      <Summary.Footer>
        Last update { updatedAt } &middot;
      </Summary.Footer>
    </Summary>;
}

function DataAssetBrowser(props) {
  const assets = _.get(props, 'assets') || [];
  
  const locationQuery = new URLSearchParams(_.get(props, 'location.search') || '');
  const [query, setQuery] = useState(locationQuery.get('q') || '');
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

  return <>
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
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={20} style={{ paddingLeft: "20px" }}>
        {
          _.size(lists[tab]) > 0 &&  <>
            <Summary.Summaries style={{ marginTop: 0 }}>
              {
                _.map(_.slice(lists[tab], 0, maxCount), asset => <Asset asset={ asset } key={ asset.name } />)
              }
            </Summary.Summaries>

            {
              _.size(lists[tab]) > maxCount && 
                <Divider><a href="#" onClick={ showMore }>Show more - { _.size(lists[tab]) - maxCount } remaining</a></Divider> ||
                <></>
            }
          </>
        }
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </>
}

DataAssetBrowser.propTypes = {};

export default DataAssetBrowser;
