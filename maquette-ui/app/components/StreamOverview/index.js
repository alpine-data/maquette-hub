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

const data = [
  {
    name: prettyTime(60), pv: 2400, amt: 2400,
  },
  {
    name: prettyTime(50), pv: 1398, amt: 2210,
  },
  {
    name: prettyTime(40), pv: 9800, amt: 2290,
  },
  {
    name: prettyTime(30), pv: 3908, amt: 2000,
  },
  {
    name: prettyTime(20), pv: 4800, amt: 2181,
  },
  {
    name: prettyTime(10), pv: 3800, amt: 2500,
  },
  {
    name: prettyTime(0), pv: 4300, amt: 2100,
  },
];

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
  return <Container md background={ Background } className="mq--main-content">
    <h4>Stream records count</h4>

    <ResponsiveContainer width='100%' aspect={3.0/1.0}>
      <LineChart
        data={data}
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
        JSON.stringify(schema, null, 2)
      }
    </SyntaxHighlighter>

    <StreamCodeExamples />
  </Container>;
}

StreamOverview.propTypes = {};

export default StreamOverview;
