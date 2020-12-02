/**
 *
 * ProjectDataAssets
 *
 */
import _ from 'lodash';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import React from 'react';
import Container from '../Container';
import DataAssetBrowser from '../DataAssetBrowser';

import Background from '../../resources/projects-background.png';
import StartSearch from '../StartSearch';
import NewDataAsset from '../NewDataAsset';

function GetStarted(props) {
  return <Container lg className="mq--main-content" background={ Background }>
    <h4>This project has no access to data yet</h4>

    <p className="mq--p-leading">
      To access data from this project's runtime, browse available data sets and ask for access to them.
    </p>

    <StartSearch 
      title="Search existing data assets" 
      searchAllLabel={ `Browse existing assets` }
      searchLabel= { `Search existing asssets` }
      link="/shop/browse" />

    <br /><br />
    <h5>Create a new data asset</h5>
    <NewDataAsset { ...props } />
  </Container>;
}

function Browse(props) {
  const assets = _.get(props, 'project.data.project.assets');
  
  return <Container lg className="mq--main-content" background={ Background }>
    <DataAssetBrowser assets={ assets } />
  </Container>
}

function ProjectDataAssets(props) {
  const assets = _.get(props, 'project.data.project.assets');

  if (_.isEmpty(assets)) {
    return <GetStarted { ...props } />;
  } else {
    return <Browse { ...props } />; 
  }
}

ProjectDataAssets.propTypes = {};

export default ProjectDataAssets;
