/**
 *
 * DataExplorer
 *
 */
import _ from 'lodash';
import React from 'react';
import { Divider, FlexboxGrid, Icon } from 'rsuite';
import FlexboxGridItem from 'rsuite/lib/FlexboxGrid/FlexboxGridItem';

function StatisticGroup({ group }) {
  return <>
    <Divider />
    { 
      _.map(group, line => {
        return <FlexboxGrid key={ line }>
          <FlexboxGrid.Item colspan={14}>{ line[0] }</FlexboxGrid.Item>
          <FlexboxGridItem colspan={5} className="number"><nobr>{ line[1] }</nobr></FlexboxGridItem>
          <FlexboxGridItem colspan={5} className="number mq--sub">{ line[2] }</FlexboxGridItem>
        </FlexboxGrid>;
      })
    }
  </>
}

function Statistics({ stats }) {
  return <FlexboxGrid.Item colspan={13}>
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

  const image = _.get(field, 'image')

  return <div className="mq--explorer--field">
    <p className="mq--explorer--field--name"><Icon icon={ icons[field.type] } size="2x" />&nbsp;{ field.name }<span className="mq--sub">, { field.type }</span></p>
    <FlexboxGrid align="top" justify="space-between">
      <FlexboxGrid.Item colspan={10}>
        {
          image && <>
            <img src={ "data:image/png;base64," + image } className="mq--explorer--field--image"  />
          </>
        }
      </FlexboxGrid.Item>
      <Statistics stats={ field.stats } />
    </FlexboxGrid>
  </div>
}

function FieldContainer({ children }) {
  return <FlexboxGrid.Item 
    colspan={ 12 }
    style={{
      backgroundColor: '#ffffffaa',
      padding: '20px',
      borderTop: '2px solid #ccc'
    }}>
    { children }
  </FlexboxGrid.Item>;
}

function DataExplorer({ stats }) {
  return <FlexboxGrid justify="space-between">
    { 
      _.map(stats, f => <FieldContainer key={ f.name }><Field field={ f } /></FieldContainer>) 
    }
  </FlexboxGrid>;
}

DataExplorer.propTypes = {};

export default DataExplorer;
