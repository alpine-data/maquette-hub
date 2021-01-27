/**
 *
 * DataSourceOverview
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import Container from '../Container';
import DataExplorer from '../DataExplorer';
import Background from '../../resources/datashop-background.png';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';
import DatasetCodeExamples from '../DatasetCodeExamples';
SyntaxHighlighter.registerLanguage('json', json);

function DataSourceOverview(props) {
  const source = _.get(props, 'dataSource.data.source');
  const schema = _.get(props, 'dataSource.data.source.schema');
  const records = _.get(props, 'dataSource.data.source.records');
  const statistics = _.get(props, 'dataSource.data.source.statistics.columns') || [];

  return <Container background={ Background } className="mq--main-content">
    <p className="mq--p-leading">
      The data source contains <b>{ records }</b> records.
    </p>

    <h4>Schema</h4>
    <SyntaxHighlighter showLineNumbers language="json" style={docco}>
      { 
        JSON.stringify(schema, null, 2)
      }
    </SyntaxHighlighter>
    
    <hr />
    <DataExplorer stats={ statistics } />

    <hr />
    <h4>Related data assets <span className="mq--sub">(alpha)</span></h4>
    {
      _.includes(['swiss-agency-clients'], _.get(props, 'dataSource.data.source.name')) && <>
        <img 
          width="100%"
          src="https://mermaid.ink/img/eyJjb2RlIjoiZ3JhcGggTFJcbiAgICBiMmJbRGF0YXNldDxiciAvPkJpc25vZGUgUmlzayBTY29yZSAtIENvbXBhbmllc11cbiAgICBjbGllbnRzW0RhdGEgU291cmNlPGJyIC8-U3dpc3MgQWdlbmN5IENsaWVudHNdXG4gICAgbmV3c1tTdHJlYW08YnIgLz5Eb3cgSm9uZXMgTmV3c11cbiAgICBldmVudHNbU3RyZWFtPGJyIC8-Q29tbWVyY2lhbCBDbGllbnQgTmV3c11cbiAgICBzdWdnZXN0ZWRbXCJTdHJlYW08YnIgLz5OZXh0IEJlc3QgQWN0aW9ucyAoQ29tbWVyY2lhbClcIl1cblxuICAgIGNsaWVudHMgLS0-IGV2ZW50c1xuICAgIGIyYiAtLT4gZXZlbnRzXG4gICAgbmV3cyAtLT4gZXZlbnRzXG4gICAgZXZlbnRzIC0tPiBzdWdnZXN0ZWRcbiIsIm1lcm1haWQiOnsidGhlbWUiOiJuZXV0cmFsIn0sInVwZGF0ZUVkaXRvciI6ZmFsc2V9" 
          alt="Stream dependencies" />
        <p className="mq--sub">Last Analysis: 26.01.2020 10:31</p>
      </> || <>
        <p>No dependencies to other assets found.</p>
      </>
    }

    <hr />
    <DatasetCodeExamples dataset={ source.name } version="1.0" />
  </Container>;
}

DataSourceOverview.propTypes = {};

export default DataSourceOverview;
