/**
 *
 * DataAssetOverview
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import { FlexboxGrid, Tooltip, Whisper } from 'rsuite';

import Container from '../Container';
import DataAssetProperties from '../DataAssetProperties';
import UserCard from '../UserCard';

const Review = styled.div`
  display: inline-block;
  line-height: 2em;
  background-color: #ff0000;
`;

function DataAssetOverview({ view, container }) {
  return <Container>
    <h5>Properties</h5>
    <DataAssetProperties resource={ view[container] } />

    <h5>Key Contacts</h5>
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 12 }>
        { 
          _.map(view.owners, owner => <UserCard key={ owner.id } user={ owner } role='Data Owner' />)
        }
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 12 }>
      { 
          _.map(view.stewards, owner => <UserCard key={ owner.id } user={ owner } role='Data Steward' />)
        }
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Container>;
}

DataAssetOverview.propTypes = {};

export default DataAssetOverview;
