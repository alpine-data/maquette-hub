/**
 *
 * ProjectOverview
 *
 */

import React from 'react';

import Container from '../Container';
import MarkdownBox from '../MarkdownBox';
import Metrics from '../Metrics';

function ProjectOverview({ view }) {
  const metrics = [
    {
      icon: 'table',
      label: 'Data Assets',
      count: 2,
      link: `/${view.project.name}`
    },
    {
      icon: 'code-fork',
      label: 'Data Repositories',
      count: 2,
      link: `/${view.project.name}/repositories`
    },
    {
      icon: 'terminal',
      label: 'Sandboxes',
      count: 2,
      link: `/${view.project.name}/sandboxes`
    }, 
    {
      icon: 'creative',
      label: 'Running Jobs',
      count: 2,
      link: `/${view.project.name}/jobs/running`
    },
    {
      icon: 'check',
      label: 'Executed Jobs',
      count: 2,
      link: `/${view.project.name}/jobs/success`
    }
  ]

  return <Container lg>
    <Metrics metrics={ metrics } />
    
    <MarkdownBox />
  </Container>;
}

ProjectOverview.propTypes = {};

export default ProjectOverview;
