/**
 *
 * DataAccessRequests
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import Container from '../Container';

import Background from '../../resources/datashop-background.png';
import { Button, ButtonToolbar, Tag } from 'rsuite';
import { Link } from 'react-router-dom';

import DataAccessRequest from '../DataAccessRequest';
import DataAccessRequestsList from '../DataAccessRequestsList';

const NavTag = styled(Tag)`
  position: absolute;
  right: 15px;
`;

function PublicConsumer(props) {
  const asset = _.get(props, 'asset');
  const requests = _.get(asset, 'accessRequests');

  if (_.isEmpty(requests)) {
    return <Container md background={ Background } className="mq--main-content">
      <h4>Subscribe for data access</h4>
      <p className="mq--p-leading">
        This asset contains <b>public</b> data. Subscribe a project to the asset to use the data. Currently you are not member of any project which has access to the data.
      </p>
      <ButtonToolbar style={{ marginTop: '30px' }}>
        <Button 
          color="green" 
          componentClass={ Link } 
          to={ `/new/data-access-request?asset=${ _.get(asset, 'properties.metadata.name') }&type=${ _.get(asset, 'properties.type') }` }>Subscribe a project</Button>
      </ButtonToolbar>
    </Container>;
  } else {
    return <Container lg background={ Background } className="mq--main-content">
      <h4>Subscribe for data access</h4>
      <p className="mq--p-leading">
        This asset contains <b>public</b> data. Subscribe a project to the asset to use the data.
      </p>
      <ButtonToolbar style={{ marginTop: '30px' }}>
        <Button 
          color="green" 
          componentClass={ Link } 
          to={ `/new/data-access-request?asset=${ _.get(asset, 'properties.metadata.name') }&type=${ _.get(asset, 'properties.type') }` }>Subscribe another project</Button>
      </ButtonToolbar>
      <hr />
      <h4>Existing Subscriptions</h4>
      <p className="mq--p-leading">
        { _.size(requests) == 1 && <><b>One</b> of your projects is subscribed to this data.</> }
        { _.size(requests) > 1 && <>Currently <b>{ _.size(requests) }</b> of your projects are subscrobed to this data.</> }
      </p>
      <DataAccessRequestsList requests={ requests } />
    </Container>;
  }
}

function Consumer(props) {
  const asset = _.get(props, 'asset');
  const requests = _.get(asset, 'accessRequests');

  if (_.isEmpty(requests)) {
    return <Container md background={ Background } className="mq--main-content">
      <h4>Get started with acccess requests</h4>
      <p className="mq--p-leading">
        This data asset is classified as <b>{ _.get(asset, 'properties.metadata.classification') }</b>. To access the data, you need to send a request to the data owners of this asset. As soon as the owners have granted your access, you can browse it and work with the data.
      </p>
      <ButtonToolbar style={{ marginTop: '30px' }}>
        <Button 
          componentClass={ Link }
          color="green"
          to={ `/new/data-access-request?asset=${ _.get(asset, 'properties.metadata.name') }&type=${ _.get(asset, 'properties.type') }` }>Create new access request</Button>
      </ButtonToolbar>
    </Container>;
  } else {
    return <Container lg background={ Background } className="mq--main-content">
      <h4>Manage your access requests</h4>
      <p className="mq--p-leading">
        This data asset is classified as <b>{ _.get(asset, 'properties.metadata.classification') }</b>. To access the data, you need to send a request to the data owners of this asset. You already have projects with access to this data. If you need access to this data for another endeavor, create a new request.
      </p>
      <ButtonToolbar style={{ marginTop: '30px' }}>
        <Button 
          componentClass={ Link }
          color="green"
          to={ `/new/data-access-request?asset=${ asset.name }&type=${ asset.type }` }>Create another access request</Button>
      </ButtonToolbar>
      <hr />
      <h4>Existing Requests</h4>
      <p className="mq--p-leading">
        { _.size(requests) == 1 && <><b>One</b> of your projects has requested access to this data.</> }
        { _.size(requests) > 1 && <>Currently <b>{ _.size(requests) }</b> of your projects have requested access to this data.</> }
      </p>
      <DataAccessRequestsList requests={ requests } />
    </Container>
  }
}

function Owner(props) {
  const asset = _.get(props, 'asset');
  const requests = _.get(asset, 'accessRequests');

  if (_.isEmpty(requests)) {
    return <Container md background={ Background } className="mq--main-content">
      <h4>Manage access requests</h4>
      <p className="mq--p-leading">
        This data asset is classified as <b>{ _.get(asset, 'properties.metadata.classification') }</b>. Users who want to access the data need to send access requests to you. You can review and decide whether the users can access the data for their purpose. Anyhow, you may also directly grant projects to access this dataset.
      </p>

      <p className="mq--p-leading">
        Currently there are no requests to this data asset.
      </p>

      <ButtonToolbar style={{ marginTop: '30px' }}>
        <Button 
          componentClass={ Link }
          color="green"
          to={ `/new/data-access-request?asset=${ _.get(asset, 'properties.metadata.name') }&type=${ _.get(asset, 'properties.type') }` }>Grant access to a project</Button>
      </ButtonToolbar>
    </Container>;
  } else {
    return <Container lg background={ Background } className="mq--main-content">
      <h4>Manage access requests</h4>

      <p className="mq--p-leading">
        This data asset is classified as <b>{ _.get(asset, 'properties.metadata.classification') }</b>. Users who want to access the data need to send access requests to you. You can review and decide whether the users can access the data for their purpose. Anyhow, you may also directly grant projects to access this dataset.
      </p>

      <p className="mq--p-leading">
        { _.size(requests) == 1 && <>There is <b>one</b> request to this asset.</> }
        { _.size(requests) > 1 && <>Currently there are <b>{ _.size(requests) }</b> to this asset.</> }
      </p>

      <ButtonToolbar style={{ marginTop: '30px' }}>
        <Button 
          componentClass={ Link }
          color="green"
          to={ `/new/data-access-request?asset=${ _.get(asset, 'properties.metadata.name') }&type=${ _.get(asset, 'properties.type') }` }>Grant access to a project</Button>
      </ButtonToolbar>

      <hr />

      <h4>Existing Requests</h4>
      <p className="mq--p-leading">
        { _.size(requests) == 1 && <>There is <b>one</b> request to this asset.</> }
        { _.size(requests) > 1 && <>Currently there are <b>{ _.size(requests) }</b> to this asset.</> }
      </p>
      <DataAccessRequestsList requests={ requests } />
    </Container>;
  }
}

function Request(props) {
  const id = _.get(props, 'match.params.id');
  const asset = _.get(props, 'asset');
  const view = _.get(props, 'view');

  const requests = _.get(asset, 'accessRequests');
  const updating = _.get(view, 'updating');
  const request = _.find(requests, r => r.id == id);

  if (request) {
    return <Container lg background={ Background } className="mq--main-content">
        <DataAccessRequest 
          updating={ updating }
          onGrant={ props.onGrant }
          onReject={ props.onReject }
          onRequest={ props.onRequest }
          onWithdraw={ props.onWithdraw }
          request={ request } />
      </Container>;
  } else {
    return <Container md background={ Background } className="mq--main-content">
      <h4>Access Request #{id} not found</h4>
    </Container>;
  }
}

function DataAccessRequests(props) {
  const id = _.get(props, 'match.params.id');
  const asset = _.get(props, 'asset');
  const view = _.get(props, 'view');

  const isPublic = asset.classification == 'public';
  const isOwner = _.get(view, 'data.isOwner');

  if (id) {
    return <Request { ...props } />
  } else if (isOwner) {
    return <Owner { ...props } />;
  } else if (isPublic) {
    return <PublicConsumer { ...props } />;
  } else {
    return <Consumer { ...props } />;
  }
}

DataAccessRequests.propTypes = {
  asset: PropTypes.object.isRequired,
  updating: PropTypes.bool,
  view: PropTypes.object.isRequired,

  onGrant: PropTypes.func,
  onReject: PropTypes.func,
  onRequest: PropTypes.func,
  onWithdraw: PropTypes.func
};

DataAccessRequest.defaultProps = {
  onGrant: console.log,
  onReject: console.log,
  onRequest: console.log,
  onWithdraw: console.log
}

export default DataAccessRequests;
