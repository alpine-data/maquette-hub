/**
 *
 * SourceOverview
 *
 */

import React from 'react';

import Container from '../Container';
import DataExplorer from '../DataExplorer';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';
SyntaxHighlighter.registerLanguage('json', json);

import { FlexboxGrid } from 'rsuite';

function SourceOverview(props) {
  const view = _.get(props, 'source.view');
  const schema = _.get(view, 'asset.customDetails.schema');
  const records = _.get(view, 'asset.customDetails.records');
  const statistics = _.get(view, 'asset.customDetails.columns') || [];

  if (!view) {
    return <></>;
  } else {
    return <Container fluid className="mq--main-content">
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 8 }>
          <p className="mq--p-leading">
            The data source contains <b>{ records }</b> records.
          </p>

          <h4>Schema</h4>
          <SyntaxHighlighter showLineNumbers language="json" style={docco}>
            { 
              JSON.stringify(schema, null, 2)
            }
          </SyntaxHighlighter>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 15 }>
          <h4>Fields</h4>

          { 
            _.isEmpty(statistics) && <>
              <p>Currently, there is no field data available.</p>
            </> || <>
              <DataExplorer stats={ statistics } />
            </>
          }
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Container>;
  }
}

SourceOverview.propTypes = {};

export default SourceOverview;
