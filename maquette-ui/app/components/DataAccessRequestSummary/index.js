/**
 *
 * DataAccessRequest
 *
 */
import _ from 'lodash';
import React from 'react';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import Summary from '../Summary';

function DataAccessRequestSummary({ dataset, request }) {

  return <Summary to={ `/shop/datasets/${dataset}/access-requests/${request.id}` }>
    <Summary.Header>{ request.project.title } <span className="mq--sub">#{ request.id }</span></Summary.Header>
    <Summary.Body>
      { _.last(request.events).reason }
    </Summary.Body>
    <Summary.Footer>
      Status: { request.status } &middot; Last update { new Date(_.first(request.events).created.at).toLocaleString() } by { _.first(request.events).created.by }
    </Summary.Footer>
  </Summary>;
}

DataAccessRequestSummary.propTypes = {};

export default DataAccessRequestSummary;
