/**
 *
 * Project
 *
 */
import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectProject from './selectors';
import reducer from './reducer';
import saga from './saga';
import { load, update, dismissError } from './actions';

import Container from 'components/Container';
import EditableParagraph from 'components/EditableParagraph';
import Error from '../../components/Error';
import Sandboxes from '../../components/Sandboxes';

import { Button, ButtonToolbar, Nav, Icon, FlexboxGrid, Affix, Whisper, Tooltip } from 'rsuite';
import { Link } from 'react-router-dom';

import ProjectBackground from '../../resources/projects-background.png';
import ProjectSettings from '../../components/ProjectSettings';
import ErrorMessage from '../../components/ErrorMessage';

function Display(props) {
  const tab = _.get(props, 'match.params.tab') || 'sandboxes';
  const name = _.get(props, 'project.data.project.name');
  const title = _.get(props, 'project.data.project.title');
  const summary = _.get(props, 'project.data.project.summary');
  const isAdmin = _.get(props, 'project.data.isAdmin');
  const isMember = _.get(props, 'project.data.isMember');
  const error = _.get(props, 'project.error');

  const onUpdate = (values) => {
    const current = _.pick(_.get(props, 'project.data.project'), 'name', 'title', 'summary');

    const updated = _.assign(current, values, { project: name });

    props.dispatch(update('projects update', updated));
  }

  const onGrant = (value) => {
    const request = _.assign(value, { project: name });
    props.dispatch(update('projects grant', request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { project: name });
    props.dispatch(update('projects revoke', request));
  }

  return (
    <div>
      <Helmet>
        <title>{ title } &middot; Maquette</title>
      </Helmet>

      <Affix top={56}>
        <div className="mq--page-title">
          <Container fluid>
            <FlexboxGrid align="middle">
              <FlexboxGrid.Item colspan={ 20 }>
                <h1><Link to={ `/${name}` }>{ title }</Link></h1>
                <EditableParagraph 
                  value={ summary } 
                  onChange={ summary => onUpdate({ summary }) }
                  disabled={ !isAdmin }
                  className="mq--p-leading" />
              </FlexboxGrid.Item>
              <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
                <ButtonToolbar>
                  <Whisper
                    trigger="hover"
                    placement="bottom"
                    speaker={ <Tooltip>3 members</Tooltip> }>
                      
                      <Button size="sm" active><Icon icon="user-o" />&nbsp;&nbsp;3</Button>
                  </Whisper>
                  <Button size="sm" active><Icon icon="bookmark-o" />&nbsp;&nbsp;42</Button>
                </ButtonToolbar>
              </FlexboxGrid.Item>
            </FlexboxGrid>
          </Container>
          
          <Nav appearance="subtle" activeKey={ tab } className="mq--nav-tabs">
            <Nav.Item eventKey="experiments" componentClass={ Link } to={ `/${name}/experiments` }>Experiments</Nav.Item>
            <Nav.Item eventKey="models" componentClass={ Link } to={ `/${name}/models` }>Models</Nav.Item>
            <Nav.Item eventKey="sandboxes" componentClass={ Link } to={ `/${name}/sandboxes` }>Sandboxes</Nav.Item>
            <Nav.Item eventKey="templates" componentClass={ Link } to={ `/${name}/templates` }>Templates</Nav.Item>
            <Nav.Item eventKey="settings" componentClass={ Link } to={ `/${name}/settings` }>Settings</Nav.Item>
          </Nav>
        </div>
      </Affix>

      {
        error && <ErrorMessage title="An error occurred saving the changes" message={ error } onDismiss={ () => props.dispatch(dismissError()) } />
      }

      { 
        tab == 'settings' && <>
          <ProjectSettings 
            { ...props} 
            onUpdate={ onUpdate }
            onGrant={ onGrant }
            onRevoke={ onRevoke } /> 
        </>
      }

      { tab == 'sandboxes' && <Sandboxes { ...props} /> }
    </div>
  );
}

export function Project(props) {
  useInjectReducer({ key: 'project', reducer });
  useInjectSaga({ key: 'project', saga });

  const [initialized, setInitialized] = useState(false);
  const error = _.get(props, 'project.error');
  const data = _.get(props, 'project.data');
  const loading = _.get(props, 'project.loading');
  const project = _.get(props, 'match.params.project');
  const isMember = _.get(props, 'project.data.isMember');

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(project));
      setInitialized(true);
    }
  });

  if (!initialized || loading) {
    return <div className="mq--loading" /> 
  } else if (!data && error) {
    return <Error background={ ProjectBackground } message={ error } />;
  } else if (!isMember) {
    return <Error background={ ProjectBackground } message="You are not authorized to view this project." />;
  } else {
    return <Display { ...props } />; 
  }
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
