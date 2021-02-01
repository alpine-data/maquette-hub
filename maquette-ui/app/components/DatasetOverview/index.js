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

import Background from '../../resources/datashop-background.png';

function Browse(props) {
  const dataset = _.get(props, 'dataset.data.dataset');
  const versions = _.get(props, 'dataset.data.dataset.versions') || [];
  const version = _.get(props, 'dataset.version') || _.first(versions).version;

  const schema = _.get(_.find(versions, v => v.version == version), 'schema') || {};
  const statistics = _.get(_.find(versions, v => v.version == version), 'statistics.columns') || [];


  return <Container md background={ Background } className="mq--main-content">
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

    <hr />

    <DataExplorer stats={ statistics } />

    <hr />

    <h4>Related data assets <span className="mq--sub">(alpha)</span></h4>
    {
      _.includes(['db-bb'], dataset.name) && <>
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

    <DatasetCodeExamples dataset={ dataset.name } version={ version } />
  </Container>;
}

function GetStarted(props) {
  return <Container md background={ Background } className="mq--main-content">
    <h4>Get started with Maquette Datasets</h4>

    <p className="mq--p-leading">
      A dataset contains structured data, like table. A dataset may contain multiple, immutable versions of its data. See below to use one of the Maquette SDKs to push data into the dataset.
    </p>

    <DatasetCodeExamples 
      canConsume={ false }
      dataset={ _.get(props, 'dataset.data.dataset.name') } 
      version={ '1.0' } />
  </Container>
}

function DatasetOverview(props) {
  console.log(props);
  if (_.isEmpty(_.get(props, 'dataset.data.dataset.versions'))) {
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
