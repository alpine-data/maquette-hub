/**
 *
 * DataAssetProjects
 *
 */

import _ from 'lodash';
import React from 'react';
import ProjectSummary from '../ProjectSummary';
import Summary from '../Summary';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

function DataAssetProjects({ asset }) {
  console.log(asset);

  const projects = _.map(_.get(asset, 'accessRequests'), request => <ProjectSummary key={ request.id } project={ request.project } />)

  if (_.isEmpty(projects)) {
    return <>No subscribed projects yet.</>
  } else {
    return <Summary.Summaries>
      { projects }
    </Summary.Summaries>;
  }
}

DataAssetProjects.propTypes = {};

export default DataAssetProjects;
