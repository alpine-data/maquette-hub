/**
 *
 * DatasetOverview
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';

import Container from '../Container';
import DataExplorer from '../DataExplorer';
import DatasetCodeExamples from '../DatasetCodeExamples';
import VersionsTimeline from '../VersionsTimeline';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';
SyntaxHighlighter.registerLanguage('json', json);

import { FlexboxGrid } from 'rsuite';

function Browse(props) {
  const dataset = _.get(props, 'dataset.view.asset');
  const versions = _.get(dataset, 'versions') || [];
  const version = _.get(props, 'dataset.version') || _.first(versions).version;

  const schema = _.get(_.find(versions, v => v.version == version), 'schema') || {};
  const statistics = _.get(_.find(versions, v => v.version == version), 'statistics.columns') || [];


  return <Container fluid className="mq--main-content">
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 8 }>
        <h4>Versions</h4>

        <VersionsTimeline 
          dataset={ dataset } 
          versions={ versions } 
          activeVersion={ version } 
          onSelectVersion={ version => props.onSelectVersion(version) } /> 

        <hr />

        <h4>Schema <span className="mq--sub">v{ version }</span></h4>
        <SyntaxHighlighter showLineNumbers language="json" style={docco}>
          { 
            JSON.stringify(schema, null, 2)
          }
        </SyntaxHighlighter>
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 15 }>
        <h4>Fields <span className="mq--sub">v{ version }</span></h4>
        <DataExplorer stats={ statistics } />
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Container>;
}

function GetStarted(props) {
  return <Container md className="mq--main-content">
    <h4>Get started with Maquette Datasets</h4>

    <p className="mq--p-leading">
      A dataset contains structured data, like table. A dataset may contain multiple, immutable versions of its data. See below to use one of the Maquette SDKs to push data into the dataset.
    </p>

    <DatasetCodeExamples 
      canConsume={ false }
      dataset={ _.get(props, 'dataset.view.asset.properties.metadata.name') } 
      version={ '1.0' } />
  </Container>
}

function DatasetOverview(props) {
  if (_.isEmpty(_.get(props, 'dataset.view.asset.versions'))) {
    return <GetStarted { ...props } />;
  } else {
    return <Browse { ...props } />
  }
}

DatasetOverview.propTypes = {
  
};

DatasetOverview.defaultProps = {
  onSelectVersion: console.log
}

export default DatasetOverview;
