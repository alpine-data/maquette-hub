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
    <DatasetCodeExamples dataset={ source.name } version="1.0" />
  </Container>;
}

DataSourceOverview.propTypes = {};

export default DataSourceOverview;
