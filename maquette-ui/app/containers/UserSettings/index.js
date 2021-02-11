/**
 *
 * UserSettings
 *
 */

import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';
import { produce } from 'immer';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectUserSettings from './selectors';
import reducer from './reducer';
import saga from './saga';
import VerticalTabs from '../../components/VerticalTabs';
import Container from '../../components/Container';
import ViewContainer from '../../components/ViewContainer';
import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock } from 'rsuite';
import { Avatar } from '../../components/UserProfileOverview';
import { load, update } from './actions';

const SECRET = '__secret__';

function ProfileSettings({ profile = { name: 'Gustav', title: 'Schmock', location: 'Brotdorf', bio: 'Lorem Ipsum' }, onChange = console.log }) {
  const [ state, setState ] = useState(profile);
  const changed = !_.isEqual(state, profile);

  const onChanged = (field) => (value) => {
    return setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  return <>
    <h4>Profile Settings</h4>

    <Form fluid>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 17 }>
          <FormGroup>
            <ControlLabel>Name</ControlLabel>
            <FormControl name="name" value={ state.name || '' } onChange={ onChanged("name") } />
          </FormGroup>

          <FormGroup>
            <ControlLabel>Job Title</ControlLabel>
            <FormControl name="title"  value={ state.title || '' } onChange={ onChanged("title") } />
          </FormGroup>

          <FormGroup>
            <ControlLabel>Office Location</ControlLabel>
            <FormControl name="location" value={ state.location || '' } onChange={ onChanged("location") } />
          </FormGroup>

          <FormGroup>
            <ControlLabel>Bio</ControlLabel>
            <FormControl name="bio" value={ state.bio || '' } onChange={ onChanged("bio") } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 6 } style={{ textAlign: 'center' }}>
          <Avatar src={ `${state.avatar}&s=200` } />
          <div className="mq--sub">
            The profile image can be configured using <a href="http://gravatar.com" target="_blank">Gravatar</a>.
          </div>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <ButtonToolbar>
            <Button 
              disabled={ !changed }
              onClick={ () => onChange(state) }
              appearance="primary" 
              type="submit">Save changes</Button>
          </ButtonToolbar>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>
  </>
}

function SSHConfiguration({ git, onChange = console.log }) {
  const [ state, setState ] = useState(git);
  const [ editing, setEditing ] = useState(false);
  const changed = !_.isEqual(state, git);

  const onChanged = (field) => (value) => {
    return setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  return <>
    <FormGroup>
      <ControlLabel>SSH Keys</ControlLabel>
      <HelpBlock>The SSH Keys are used to configure Git Access from Sandboxes.</HelpBlock>
    </FormGroup>

    { 
      (_.isEmpty(state.privateKey) || _.isEmpty(state.publicKey) || state.privateKey === SECRET) && !editing && <>
        <ButtonToolbar>
          <Button appearance="ghost" onClick={ () => setEditing(true) }>Setup keys</Button>
        </ButtonToolbar>
      </> || <>
        <ButtonToolbar>
          <Button appearance="ghost" onClick={ () => setEditing(true) }>Update keys</Button>
        </ButtonToolbar>
      </>
    }

    {
      editing && <>
        <FormGroup>
          <ControlLabel>Public Key</ControlLabel>
          <FormControl 
            rows={ 5 } 
            name="publicKey" 
            componentClass="textarea" 
            style={{ fontFamily: 'monospace' }} 
            value={ state.publicKey !== SECRET && state.publicKey || "" }
            onChange={ onChanged("publicKey") } />
        </FormGroup>

        <FormGroup>
          <ControlLabel>Private Key</ControlLabel>
          <FormControl 
            rows={ 5 } 
            name="privateKey" 
            componentClass="textarea" 
            style={{ fontFamily: 'monospace' }}
            value={ state.privateKey !== SECRET && state.privateKey || "" }
            onChange={ onChanged("privateKey") } />
        </FormGroup>

        <ButtonToolbar>
          <Button 
            disabled={ !changed }
            onClick={ () => onChange(state) }
            appearance="primary" 
            type="submit">Save SSH Keys</Button>
        </ButtonToolbar>
      </>
    }
  </>;
}

function GitSettings({ git = {}, onChange = console.log }) {
  const [ state, setState ] = useState(git);
  const changed = !_.isEqual(state, git);

  const onChanged = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  useEffect(() => {
    setState(git);
  }, [ git ])

  return <>
    <h4>Git Settings</h4>

    <Form fluid>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={ 17 }>
          <FormGroup>
            <ControlLabel>GitHub Username</ControlLabel>
            <FormControl name="username" value={ state.username } onChange={ onChanged("username") } />
          </FormGroup>

          <FormGroup>
            <ControlLabel>Access Token</ControlLabel>
            <FormControl type="password" name="password" placeholder="Enter new password" value={ state.password !== SECRET && state.password || "" } onChange={ onChanged("password") } />
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <ButtonToolbar>
            <Button 
              disabled={ !changed }
              onClick={ () => onChange(state) }
              appearance="primary" 
              type="submit">Save changes</Button>
          </ButtonToolbar>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 17 }>
          <SSHConfiguration git={ git } onChange={ onChange } />
        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Form>
  </>
}

export function UserSettings(props) {
  useInjectReducer({ key: 'userSettings', reducer });
  useInjectSaga({ key: 'userSettings', saga });

  const [initialized, setInitialized] = useState(false);
  const data = _.get(props, 'userSettings.data');

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(true));
      setInitialized(true);
    }
  }, []);

  const onChange = (field) => value => {
    const request = produce(data, draft => {
      draft[field] = value;
    });

    props.dispatch(update(request))
  }

  return <ViewContainer
    titles={ [ { label: 'User Settings' } ] }
    loading={ props.userSettings.loading }
    error={ props.userSettings.error }

    content={ () => <>
      <Container lg className="mq--main-content">
        <VerticalTabs 
          active={ _.get(props, 'match.params.tab') || 'profile' }
          tabs={ [
            {
              key: "profile",
              label: "Profile",
              link: "/user/settings",
              visible: true,
              component: () => <ProfileSettings { ...data } onChange={ onChange('profile') } />
            },
            {
              key: "git",
              label: "Git Settings",
              link: "/user/settings/git",
              visible: true,
              component: () => <GitSettings { ...data.settings } onChange={ git => onChange('settings')({ git }) } />
            }
          ] } />
        </Container>
      </> }
      />;
}

UserSettings.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  userSettings: makeSelectUserSettings(),
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

export default compose(withConnect)(UserSettings);
