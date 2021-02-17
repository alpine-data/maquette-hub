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

import { LineChart, Line, CartesianGrid, XAxis, YAxis, ResponsiveContainer } from 'recharts';
import { FlexboxGrid } from 'rsuite';

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
  const name = props.stream.view.stream.name;

  return <Container fluid className="mq--main-content">
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 11 }>
        <h4>Stream records count</h4>

        <ResponsiveContainer width='100%' aspect={3.0/1.0}>
          <LineChart
            data={data[props.stream.view.stream.name] || data['default']}
            >

            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Line type="monotone" dataKey="pv" stroke="#8884d8" activeDot={{ r: 8 }} />
          </LineChart>
        </ResponsiveContainer>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 12 }>
        <h4>Message Schema</h4>
        <SyntaxHighlighter showLineNumbers language="json" style={docco}>
          { 
            JSON.stringify(props.stream.view.stream.schema, null, 2)
          }
        </SyntaxHighlighter>
      </FlexboxGrid.Item>
    </FlexboxGrid>
  </Container>;
}

StreamOverview.propTypes = {};

export default StreamOverview;
