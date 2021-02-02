/**
 *
 * Project
 *
 */
import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectProject from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load, update, dismissError } from './actions';

import ProjectOverview from '../../components/ProjectOverview';
import ProjectSettings from '../../components/ProjectSettings';
import ProjectDataAssets from '../../components/ProjectDataAssets';
import Sandboxes from '../../components/Sandboxes';
import ViewContainer from '../../components/ViewContainer';

export function Project(props) {
  useInjectReducer({ key: 'project', reducer });
  useInjectSaga({ key: 'project', saga });

  const [initialized, setInitialized] = useState(false);
  const data = _.get(props, 'project.data');
  const project = _.get(props, 'match.params.project');

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(project));
      setInitialized(true);
    }
  });

  const onUpdate = (values) => {
    const current = _.pick(_.get(data, 'project'), 'name', 'title', 'summary');
    const updated = _.assign(current, values, { project });
    props.dispatch(update('projects update', updated));
  }

  const onGrant = (value) => {
    const request = _.assign(value, { project });
    props.dispatch(update('projects grant', request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { project });
    props.dispatch(update('projects revoke', request));
  }

  return <ViewContainer 
    background='projects'
    loading={ _.get(props, 'project.loading') }

    likes={ 23 }
    liked={ true }
    onChangeLike={ console.log }

    error={ _.get(props, 'project.error') }
    onCloseError={ () => props.dispatch(dismissError()) }

    canChangeSummary={ data.isAdmin }
    summary={ _.get(data, 'project.summary') }
    onChangeSummary={ summary => onUpdate({ summary }) }

    titles={ [ { link: `/${project}`, label: _.get(data, 'project.title') || project }] }
  
    activeTab='overview'
    tabs={ [
      {
        label: 'Overview',
        key: 'overview',
        link: `/${project}`,
        visible: true,
        component: () => <ProjectOverview view={ data } />
      },
      {
        label: 'Data Assets',
        link: `/${project}/data`,
        key: 'data',
        visible: true,
        component: () => <ProjectDataAssets { ...props } />
      },
      {
        label: 'Data Repositories',
        link: `/${project}/repositories`,
        key: 'repositories',
        visible: true,
        component: () => <>Data Repositories</>
      },
      {
        label: 'Sandboxes',
        link: `/${project}/sandboxes`,
        key: 'sandboxes',
        visible: true,
        component: () => <Sandboxes { ...props} />
      },
      {
        label: 'Jobs',
        link: `/${project}/jobs`,
        key: 'jobs',
        visible: true,
        component: () => <>Jobs</>
      },
      {
        label: 'Experiments',
        link: `/${project}/experiments`,
        key: 'experiments',
        visible: true,
        component: () => <>Experiments</>
      },
      {
        label: 'Settings',
        link: `/${project}/settings`,
        key: 'settings',
        visible: data.isAdmin,
        component: () => {
          return <ProjectSettings 
            { ...props} 
            onUpdate={ onUpdate }
            onGrant={ onGrant }
            onRevoke={ onRevoke } /> 
        }
      }
    ] }
    { ...props } />
}

Project.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  project: makeSelectProject(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(Project);
