/**
 *
 * DataAssetSettings
 *
 */

import _ from 'lodash';
import React from 'react';

import { useFormState } from 'utils/hooks';
import { Button, ButtonToolbar, ControlLabel, Form, FormControl, FormGroup } from 'rsuite';

import Container from '../Container';
import DataAssetPropertiesForm, { validate as validateDataAssetProperties } from '../DataAssetPropertiesForm';
import DataAssetReview from '../DataAssetReview';


import Error from '../Error';
import Members from '../Members';
import VerticalTabs from '../VerticalTabs';

function DangerZone({ view, container }) {
  const [state, , onChange, onChangeValues] = useFormState({ name: '' });

  return <>
    <h4>Delete { _.capitalize(container) }</h4>
    <Form fluid>
      <FormGroup>
        <ControlLabel>Type { container }'s name</ControlLabel>
        <FormControl name="repeat-name" value={ state.name } onChange={ onChange('name') } />
      </FormGroup>

      <ButtonToolbar>
        <Button 
          appearance="primary" 
          color="red"
          disabled={ state.name !== _.get(view, 'asset.properties.metadata.name') }
          type="submit">Delete `{ _.get(view, 'asset.properties.metadata.title') }`</Button>
      </ButtonToolbar>
    </Form>
  </>
}

function GeneralSettings({ view, container, onUpdateSettings }) {
  const initialState = _.pick(_.get(view, 'asset.properties.metadata'), 'title', 'name', 'summary', 'visibility', 'classification', 'personalInformation', 'zone');
  const [state, , onChange, onChangeValues] = useFormState(_.assign({}, initialState));
  const isUpdateDisabled = !validateDataAssetProperties(state) || _.isEqual(state, initialState);

  return <>
    <h4>{ _.capitalize(container) } Asset Properties</h4>
    <Form fluid>
      <DataAssetPropertiesForm 
        state={ state } 
        onChangeValues={ onChangeValues }
        onChange={ onChange }
        assetType={ container } />

      <ButtonToolbar>
        <Button 
          appearance="primary" 
          disabled={ isUpdateDisabled } 
          onClick={ () => {
            onUpdateSettings(state)
          } }>

          Update { container }
        </Button>
      </ButtonToolbar>
    </Form>
  </>
}

function AdditionalSettings({ 
  view, container, settingsComponentClass, settingsComponentAdditionalProps, 
  settingsComponentInitialState, settingsComponentValidate,
  onUpdateSettings, ...props }) {

  const initialState = _.assign({}, initialState, _.get(view, 'asset.customSettings'));
  const [state, , onChange, onChangeValues] = useFormState(initialState);
  const isUpdateDisabled = !settingsComponentValidate(state) || _.isEqual(state, initialState);

  const Component = settingsComponentClass;

  return <>
    <h4>{ _.capitalize(container) } Properties</h4>

    <Form fluid>
      <Component 
        state={ state }
        onChange={ onChange }
        onChangeValues={ onChangeValues }
        { ...props }
        { ...settingsComponentAdditionalProps } />

      <ButtonToolbar>
        <Button 
          appearance="primary" 
          disabled={ isUpdateDisabled } 
          onClick={ () => onUpdateSettings({ customSettings: state }, 'update-custom', false) }>

          Update { container }
        </Button>
      </ButtonToolbar>
    </Form>
  </>
}

function Review({ view, container, onUpdateSettings }) {
  return <>
    <h4>Review { _.capitalize(container) }</h4>
    <DataAssetReview 
      { ...view } 
      onApprove={ () => onUpdateSettings({}, 'approve', false) }
      onDecline={ (reason) => onUpdateSettings({ reason }, 'decline', false) }
      onRequestReview={ (message) => onUpdateSettings({ message }, 'request-review', false) } />
  </>
}

function DataAssetSettings({ 
  additionalTabs, basePath, container, view,
  settingsComponentClass, settingsComponentAdditionalProps, settingsComponentInitialState, settingsComponentValidate,
  onGrant, onRevoke, onUpdateSettings, ...props }) {

  if (!_.get(view, 'permissions.canChangeSettings')) {
    return <Error 
      background={ Background }
      message="You are not authorized to view the settings of this asset." />;
  }

  function getTabs() {
    const commonTabs = [
      {
        order: 5,
        key: 'properties',
        label: 'Properties',
        link: `${basePath}`,
        visible: true,
        component: () => <GeneralSettings view={ view } container={ container } onUpdateSettings={ onUpdateSettings } />
      },
      {
        order: 10,
        key: container,
        label: _.startCase(container),
        link: `${basePath}/${container}`,
        visible: !_.isUndefined(settingsComponentClass),
        component: () => <AdditionalSettings 
          view={ view } 
          container={ container } 
          onUpdateSettings={ onUpdateSettings }
          settingsComponentClass={ settingsComponentClass }
          settingsComponentInitialState={ settingsComponentInitialState }
          settingsComponentAdditionalProps={ settingsComponentAdditionalProps }
          settingsComponentValidate={ settingsComponentValidate }
          { ...props } />
      },
      {
        order: 20,
        key: 'members',
        label: 'Members',
        link: `${basePath}/members`,
        visible: true,
        component: () => <Members
          title="Manage members"
          members={ _.get(view, `asset.members`) }
          roles={ [ 
            { value: "consumer", label: "Consumer" },
            { value: "producer", label: "Producer" },
            { value: "member", label: "Member" },
            { value: "owner", label: "Owner" },
            { value: "sme", label: "Data Steward" } ]
          } 
          onGrant={ onGrant }
          onRevoke={ onRevoke } />
      },
      {
        order: 30,
        key: 'danger',
        label: 'Danger Zone',
        link: `${basePath}/danger`,
        visible: true,
        component: () => <DangerZone view={ view } container={ container } />
      },
      {
        order: 40,
        key: 'review',
        label: 'Review',
        link: `${basePath}/review`,
        visible: true,
        component: () => <Review view={ view } container={ container } onUpdateSettings={ onUpdateSettings } />
      },
    ]

    const addedTabs = _.map(additionalTabs, tab => _.assign({}, tab, { 
      link: `${basePath}${tab.link}`,
      component: () => tab.component({ 
        asset: _.get(view, container),
        onUpdateSettings
      })
    }));

    return _.orderBy(_.concat(commonTabs, addedTabs), 'order');
  }

  return <Container lg>
    <VerticalTabs
      active={ _.get(props, 'match.params.id') || 'properties' }
      tabs={ getTabs() } />
  </Container>;
}

DataAssetSettings.propTypes = {};

DataAssetSettings.defaultProps = {
  additionalTabs: []
}

export default DataAssetSettings;
