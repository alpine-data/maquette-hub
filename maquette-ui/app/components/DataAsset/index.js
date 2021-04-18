/**
 *
 * DataAsset
 *
 */

import _ from 'lodash';
import React, { useState, useEffect} from 'react';
import PropTypes from 'prop-types';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import { pluralizeWord } from '../../utils/helpers';
import { useFormState } from '../../utils/hooks';

import DataAccessLogs from '../DataAccessLogs'
import DataAccessRequests from '../DataAccessRequests'
import DataAssetOverview from '../DataAssetOverview';
import DataAssetSettings from '../DataAssetSettings';
import ViewContainer from '../ViewContainer';

import actions from './actions';
import constants from './constants';
import reducer from './reducer';
import saga from './saga';

export const createActions = actions;
export const createConstants = constants;
export const createReducer = reducer;
export const createSaga = saga;


function DataAsset({ 
  container, additionalTabs, additionalSettingTabs, 
  settingsComponentClass, settingsComponentAdditionalProps,
  codeExamples, reducer, saga, ...props }) {

  useInjectReducer({ key: container, reducer });
  useInjectSaga({ key: container, saga });

  const assetName = _.get(props, 'match.params.asset');
  const projectName = _.get(props, 'match.params.project');

  const { fetch, update, dismissError } = actions(container);
  const [ initialized, setInitialized ] = useState(false);
  const view = _.get(props, `${container}.view`);

  useEffect(() => {
    props.dispatch(fetch(`views asset`, { name: assetName }, true));
    setInitialized(true);
  }, []);

  function getBasePath() {
    if (projectName) {
      return `/${projectName}/data/${pluralizeWord(container)}/${assetName}`;
    } else {
      return `/shop/${pluralizeWord(container)}/${assetName}`;
    }
  }

  function getTitles() {
    if (view && projectName) {
      return [
        {
          link: `/${projectName}`,
          label: projectName
        },
        {
          link: `/${projectName}/data`,
          label: 'Data Assets'
        },
        {
          link: getBasePath(),
          label: _.get(view, `asset.properties.metadata.title`)
        }
      ]
    } else if (view) {
      return [
        {
          link: '/shop/browse',
          label: 'Data Shop'
        },
        {
          link: getBasePath(),
          label: _.get(view, `asset.properties.metadata.title`)
        }
      ]
    } else {
      return [ { label: assetName } ]
    }
  }

  function onUpdate(updatedProperties, optionalCommand, merge = true) {
    const currentDefaultProperties = _.pick(
      _.get(view, 'asset.properties.metadata'), 
      'name', 'title', 'summary', 'visibility', 'classification', 'personalInformation', 'zone');

    const mergedProperties = { metadata: _.assign(currentDefaultProperties, updatedProperties) };
    const request = _.assign({ name: assetName }, merge && mergedProperties || updatedProperties);

    const command = optionalCommand || 'update';

    props.dispatch(update(`data-assets ${command}`, request));
  }

  function onLike(liked) {
    props.dispatch(update(`data-assets like`, { liked }))
  }

  const onGrant = (value) => {
    const request = _.assign(value, { name: assetName });
    props.dispatch(update(`data-assets members grant`, request));
  }

  const onRevoke = (value) => {
    const request = _.assign(value, { name: assetName });
    props.dispatch(update(`data-assets members revoke`, request));
  }

  function getTabs() {
    const basePath = getBasePath();

    const commonTabs = [
      {
        order: 0,
        label: 'Overview',
        key: 'overview',
        link: basePath,
        visible: true,
        component: () => view && <>
          {
            <DataAssetOverview 
              view={ view } 
              container={ container }
              codeExamples={ codeExamples } />
          }
          </> || <></>
      },
      {
        order: 10,
        label: 'Access Requests',
        key: 'access-requests',
        link: `${basePath}/access-requests`,
        visible: true,
        component: () => view && <>
            <DataAccessRequests 
              { ...props }
              asset={ _.get(view, 'asset') }
              view={ view }
              onGrant={ request => props.dispatch(update(`data-assets access-requests grant`, request)) }
              onReject={ request => props.dispatch(update(`data-assets access-requests reject`, request)) }
              onRequest={ request => props.dispatch(update(`data-assets access-requests update`, request)) }
              onWithdraw={ request => props.dispatch(update(`data-assets access-requests withdraw`, request)) } />
          </>
      },
      {
        order: 20,
        label: 'Access Logs',
        key: 'logs',
        link: `${basePath}/logs`,
        visible: _.get(view, 'permissions.canReviewLogs'),
        component: () => view && <>
          <DataAccessLogs
            { ...props }
            logs={ _.get(view, 'logs') }
            asset={ _.get(view, 'asset') } />
          </> || <></>
      },
      {
        order: 30,
        label: 'Settings',
        key: 'settings',
        link: `${basePath}/settings`,
        visible: _.get(view, 'permissions.canChangeSettings'),
        component: () => view && <>
            <DataAssetSettings
              { ...props }
              view={ view }
              container={ container }
              basePath={ `${getBasePath()}/settings` }
              onGrant={ onGrant }
              onRevoke={ onRevoke }
              onUpdateSettings={ onUpdate }
              additionalTabs={ additionalSettingTabs }
              settingsComponentClass={ settingsComponentClass }
              settingsComponentAdditionalProps={ settingsComponentAdditionalProps } />
          </> || <></>
      }
    ]

    // Adopt base path of link for additional tabs.
    const addedTabs = _.map(additionalTabs, tab => _.assign({}, tab, { 
      link: `${basePath}${tab.link}`,
      component: () => tab.component({ asset: _.get(view, container) })
    }));

    return _.orderBy(_.concat(commonTabs, addedTabs), 'order');
  }

  return <ViewContainer
    background='data'
    loading={ _.get(props, `asset.properties.metadata.loading`) || !initialized }
    
    likes={ _.get(view, `asset.properties.metadata.likes`) || 0 }
    liked={ _.get(view, `asset.properties.metadata.liked`) }
    id={ _.get(view, 'asset.properties.id') }
    onChangeLike={ onLike }
    
    error={ _.get(props, `${container}.error`) }
    onCloseError={ () => props.dispatch(dismissError()) }
    
    canChangeSummary={ _.get(view, 'permissions.canChangeSettings') }
    summary={ _.get(view, `asset.properties.metadata.summary`) } 
    onChangeSummary={ summary => onUpdate({ summary }) }
    
    titles={ getTitles() }
    tabs={ getTabs() } 
    { ...props } />
}

DataAsset.propTypes = {};

DataAsset.defaultProps = {
  additionalTabs: [],
  additionalSettingTabs: [],
  codeExamples: []
}

export default DataAsset;
