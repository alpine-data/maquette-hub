/**
 *
 * ModelSummary
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import ModernSummary, { TrendMetric, TextMetric } from '../ModernSummary';

function ModelSummary() {
  return <ModernSummary
    title="Some Model"
    tags={ ["Python Function", "Scikit Learn"] }
    link="/link/to/model"
    metrics={ [
      <TrendMetric
        value={ 3 } 
        label="Warnings" 
        text="+3 last 7 days"
        trend="up"
        sentiment="negative" />,
      <TextMetric 
        label="Owner" 
        value={ <Link to="/users/foo">Michael Wellner</Link> } />,
      <TextMetric 
        label="Updated" 
        value="3 days ago" />
    ] } />
}

ModelSummary.propTypes = {

};

export default ModelSummary;
