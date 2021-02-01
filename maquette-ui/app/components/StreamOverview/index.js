/**
 *
 * StreamOverview
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';

import Container from '../Container';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import json from 'react-syntax-highlighter/dist/esm/languages/hljs/json';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';
SyntaxHighlighter.registerLanguage('json', json);

import Background from '../../resources/datashop-background.png';
import StreamCodeExamples from '../StreamCodeExamples';

import { LineChart, Line, CartesianGrid, XAxis, YAxis, ResponsiveContainer } from 'recharts';

const now = new Date();

const prettyTime = (minAgo) => {
  let s = new Date((new Date() - (minAgo * 60 * 1000))).toLocaleTimeString();
  s = s.substr(0, 2) + ":" + (Math.floor((s.substr(3, 2) * 1) / 10) * 10);

  if (s.endsWith(":0")) {
    return s + "0";
  } else {
    return s;
  }
}

const data = {
  "dow-jones-news": [
    {
      name: prettyTime(60), pv: 0, amt: 2400,
    },
    {
      name: prettyTime(50), pv: 139, amt: 2210,
    },
    {
      name: prettyTime(40), pv: 98, amt: 2290,
    },
    {
      name: prettyTime(30), pv: 128, amt: 0,
    },
    {
      name: prettyTime(20), pv: 0, amt: 0,
    },
    {
      name: prettyTime(10), pv: 0, amt: 0,
    },
    {
      name: prettyTime(0), pv: 0, amt: 0,
    },
  ],
  "default": [
    {
      name: prettyTime(30), pv: 24, amt: 0,
    },
    {
      name: prettyTime(20), pv: 43, amt: 0,
    },
    {
      name: prettyTime(10), pv: 0, amt: 0,
    },
    {
      name: prettyTime(0), pv: 0, amt: 0,
    }
  ]
};

const schema = {
  "type": "record",
  "name": "StockPrice",
  "fields": [
    {
      "name": "symbol",
      "type": [ "string" ]
    },
    {
      "name": "price",
      "type": ["int"]
    }
  ]
};

function StreamOverview(props) {
  const name = props.stream.data.stream.name;

  return <Container md background={ Background } className="mq--main-content">
    <h4>Stream records count</h4>

    <ResponsiveContainer width='100%' aspect={3.0/1.0}>
      <LineChart
        data={data[props.stream.data.stream.name] || data['default']}
        >

        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="name" />
        <YAxis />
        <Line type="monotone" dataKey="pv" stroke="#8884d8" activeDot={{ r: 8 }} />
      </LineChart>
    </ResponsiveContainer>

    <hr />

    <h4>Schema</h4>
    <SyntaxHighlighter showLineNumbers language="json" style={docco}>
      { 
        JSON.stringify(props.stream.data.stream.schema, null, 2)
      }
    </SyntaxHighlighter>

    <hr />

    <h4>Related data assets <span className="mq--sub">(alpha)</span></h4>
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

    <StreamCodeExamples stream={ props.stream.data.stream.name } />
  </Container>;
}

StreamOverview.propTypes = {};

export default StreamOverview;
