/**
 *
 * ProjectSummary
 *
 */

import React from 'react';

import Summary from '../Summary';

function ProjectSummary({ project }) {
  return <Summary to={ `/${project.name}` }>
      <Summary.Header icon="project" category="Project">{ project.title }</Summary.Header>
      <Summary.Body>
        { project.summary }
      </Summary.Body>
      <Summary.Footer>
        { new Date(project.modified.at).toLocaleString() } by { project.modified.by }
      </Summary.Footer>
    </Summary>;
}

ProjectSummary.propTypes = {};

export default ProjectSummary;
