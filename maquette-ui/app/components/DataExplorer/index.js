/**
 *
 * DataExplorer
 *
 */
import _ from 'lodash';
import React from 'react';
import { Divider, FlexboxGrid, Icon, Tag } from 'rsuite';
import FlexboxGridItem from 'rsuite/lib/FlexboxGrid/FlexboxGridItem';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import SampleImage from './sample-statistic.png';

const sample = [
  {
    "name": "city",
    "type": "text",
    "stats": {
      "valid": [312, 82],
      "mismatched": [12, 8],
      "missing": [65, 10],
      
      details: [
        [
          ["Unique", "42", ""],
          ["Most Common", "London", ""]
        ],
        [
          ["Foo", "Bar", "Bla"]
        ]
      ]
    }
  },
  {
    "name": "population",
    "type": "numeric",
    "stats": {
      "valid": [312, 90],
      "mismatched": [12, 10],
      "missing": [0, 0],
      
      details: [
        [
          ["Mean", "30.3", ""],
          ["Std. Deviation", "14.2", ""]
        ],
        [
          ["Quantiles", "0.17", "Min"],
          ["", 21, "25%"],
          ["", 27, "50%"],
          ["", 39, "75%"],
          ["", 76, "Max"]
        ]
      ]
    }
  }
]

function StatisticGroup({ group }) {
  return <>
    <Divider />
    { 
      _.map(group, line => {
        return <FlexboxGrid key={ line }>
          <FlexboxGrid.Item colspan={14}>{ line[0] }</FlexboxGrid.Item>
          <FlexboxGridItem colspan={5} className="number">{ line[1] }</FlexboxGridItem>
          <FlexboxGridItem colspan={5} className="number mq--sub">{ line[2] }</FlexboxGridItem>
        </FlexboxGrid>;
      })
    }
  </>
}

function Statistics({ stats }) {
  console.log(stats);
  return <FlexboxGrid.Item colspan={11}>
    <div className="mq--explorer--field--correctness">
      <div className="valid" style={{ width: stats.valid[1] + "%" }} />
      <div className="mismatched" style={{ width: stats.mismatched[1] + "%" }} />
      <div className="missing" style={{ width: stats.missing[1] + "%" }} />
    </div>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={14}>Valid <span className="legend valid"></span></FlexboxGrid.Item>
      <FlexboxGridItem colspan={5} className="number">{ stats.valid[0] }</FlexboxGridItem>
      <FlexboxGridItem colspan={5} className="number mq--sub">{ stats.valid[1] }%</FlexboxGridItem>
    </FlexboxGrid>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={14}>Mismatched <span className="legend mismatched"></span></FlexboxGrid.Item>
      <FlexboxGridItem colspan={5} className="number">{ stats.mismatched[0] }</FlexboxGridItem>
      <FlexboxGridItem colspan={5} className="number mq--sub">{ stats.mismatched[1] }%</FlexboxGridItem>
    </FlexboxGrid>

    <FlexboxGrid>
      <FlexboxGrid.Item colspan={14}>Missing <span className="legend missing"></span></FlexboxGrid.Item>
      <FlexboxGridItem colspan={5} className="number">{ stats.missing[0] }</FlexboxGridItem>
      <FlexboxGridItem colspan={5} className="number mq--sub">{ stats.missing[1] }%</FlexboxGridItem>
    </FlexboxGrid>

    { _.map(stats.details, group => <StatisticGroup group={ group } key={ group } />) }
  </FlexboxGrid.Item>;
}

function Field({ field }) {
  const icons = {
    numeric: "hashtag",
    text: "font",
    date: "calendar",
    bool: "check2"
  }

  console.log(field);
  console.log(field.stats);

  return <div className="mq--explorer--field">
    <p className="mq--explorer--field--name"><Icon icon={ icons[field.type] } size="2x" />&nbsp;{ field.name }<span className="mq--sub">, { field.type }</span></p>
    <FlexboxGrid>
      <FlexboxGrid.Item colspan={12}>
        <img src={ SampleImage } className="mq--explorer--field--image" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={1}></FlexboxGrid.Item>
      <Statistics stats={ field.stats } />
    </FlexboxGrid>
  </div>
}

function DataExplorer() {
  return <>
      { _.map(sample, f => <Field field={ f } key={ f.name } />) }
    </>;
}

DataExplorer.propTypes = {};

export default DataExplorer;