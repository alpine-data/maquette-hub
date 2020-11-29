/**
 *
 * DataAccessRequestsList
 *
 */

import _ from 'lodash';
import React, { useState } from 'react';
import styled from 'styled-components';

import { Tag, FlexboxGrid, Nav } from 'rsuite';
import DataAccessRequestSummary from '../DataAccessRequestSummary';
import Summary from '../Summary';

const NavTag = styled(Tag)`
  position: absolute;
  right: 15px;
`;

function DataAccessRequestsList({ requests }) {
  const [tab, setTab] = useState('all');

  const groups = {
    all: requests,
    requested: _.filter(requests, r => r.status == 'requested'),
    granted: _.filter(requests, r => r.status == 'granted'),
    rejected: _.filter(requests, r => r.status == 'rejected'),
    expired: _.filter(requests, r => r.status == 'expired'),
    withdrawn: _.filter(requests, r => r.status == 'withdrawn')
  }

  return <FlexboxGrid>
    <FlexboxGrid.Item colspan={ 4 }>
      <Nav vertical activeKey={ tab } onSelect={ value => setTab(value) } appearance="subtle">
        <Nav.Item eventKey="all">All <NavTag>{ _.size(groups['all']) }</NavTag></Nav.Item>
        <Nav.Item eventKey="requested">Requested <NavTag>{ _.size(groups['requested']) }</NavTag></Nav.Item>
        <Nav.Item eventKey="granted">Granted <NavTag>{ _.size(groups['granted']) }</NavTag></Nav.Item>
        <Nav.Item eventKey="rejected">Rejected <NavTag>{ _.size(groups['rejected']) }</NavTag></Nav.Item>
        <Nav.Item eventKey="expired">Expired <NavTag>{ _.size(groups['expired']) }</NavTag></Nav.Item>
        <Nav.Item eventKey="withdrawn">Withdrawn <NavTag>{ _.size(groups['withdrawn']) }</NavTag></Nav.Item>
      </Nav>
    </FlexboxGrid.Item>
    <FlexboxGrid.Item colspan={ 20 } style={{ paddingLeft: "20px" }}>
      <Summary.Summaries style={{ margin: 0 }}>
        {
          _.map(groups[tab], r => <DataAccessRequestSummary key={ r.id } dataset="some-dataset" request={ r } />)
        }

        {
          _.isEmpty(groups[tab]) && <>
              <Summary.Empty>
                No access request exists in this state.
              </Summary.Empty>
          </>
        }
      </Summary.Summaries>
    </FlexboxGrid.Item>
  </FlexboxGrid>;
}

DataAccessRequestsList.propTypes = {};

export default DataAccessRequestsList;
