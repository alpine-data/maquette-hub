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
import DataAssetProjects from '../DataAssetProjects';
import DataAssetProperties from '../DataAssetProperties';
import UserCard from '../UserCard';
import CodeExamples from '../CodeExamples';

const Review = styled.div`
  display: inline-block;
  line-height: 2em;
  background-color: #ff0000;
`;

function DataAssetOverview({ view, container, codeExamples }) {
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

    <>
      <hr />
      <h5>Related data assets <span className="mq--sub">(alpha)</span></h5>
      {
        _.includes(['next-best-action-commercial', 'dow-jones-news', 'commercial-client-news'], name) && <>
          <img 
            width="100%"
            src="https://mermaid.ink/img/eyJjb2RlIjoiZ3JhcGggTFJcbiAgICBiMmJbRGF0YXNldDxiciAvPkJpc25vZGUgUmlzayBTY29yZSAtIENvbXBhbmllc11cbiAgICBjbGllbnRzW0RhdGEgU291cmNlPGJyIC8-U3dpc3MgQWdlbmN5IENsaWVudHNdXG4gICAgbmV3c1tTdHJlYW08YnIgLz5Eb3cgSm9uZXMgTmV3c11cbiAgICBldmVudHNbU3RyZWFtPGJyIC8-Q29tbWVyY2lhbCBDbGllbnQgTmV3c11cbiAgICBzdWdnZXN0ZWRbXCJTdHJlYW08YnIgLz5OZXh0IEJlc3QgQWN0aW9ucyAoQ29tbWVyY2lhbClcIl1cblxuICAgIGNsaWVudHMgLS0-IGV2ZW50c1xuICAgIGIyYiAtLT4gZXZlbnRzXG4gICAgbmV3cyAtLT4gZXZlbnRzXG4gICAgZXZlbnRzIC0tPiBzdWdnZXN0ZWRcbiIsIm1lcm1haWQiOnsidGhlbWUiOiJuZXV0cmFsIn0sInVwZGF0ZUVkaXRvciI6ZmFsc2V9" 
            alt="Stream dependencies" />
          <p className="mq--sub">Last Analysis: 26.01.2020 10:31</p>
        </> || <>
          <p>No dependencies to other assets found.</p>
        </>
      }
    </>

    <>
      <hr />
      <h5>Projects using this { container }</h5>
      <DataAssetProjects asset={ view[container] } />
    </>

    {
      !_.isEmpty(codeExamples) && <>
        <hr />
        <h5>Code Examples</h5>
        <CodeExamples samples={ codeExamples } asset={ view[container].name } />
      </>
    }
  </Container>;
}

DataAssetOverview.propTypes = {};

export default DataAssetOverview;
