/**
 *
 * DataAccessRequests
 *
 */

import React from 'react';
import styled from 'styled-components';

import Container from '../Container';

import Background from '../../resources/datashop-background.png';
import { Button, ButtonToolbar, FlexboxGrid, Message, Nav, Tag } from 'rsuite';
import { Link } from 'react-router-dom';

const NavTag = styled(Tag)`
  position: absolute;
  right: 15px;
`;

function PublicConsumer(props) {
  const project = _.get(props, 'dataset.data.project.name');
  const asset = _.get(props, 'dataset.data.dataset.name');

  return <Container md background={ Background } className="mq--main-content">
    <h4>Get started with access requests</h4>
    <p className="mq--p-leading">
      This asset contains public data only. Subscribe a project to the asset to use access the data.
    </p>
    <ButtonToolbar style={{ marginTop: '30px' }}>
      <Button 
        color="green" 
        componentClass={ Link } 
        to={ `/new/data-access-request?project=${ project }&asset=${ asset }` }>Subscribe a project</Button>
    </ButtonToolbar>
    <hr />
    <h4>Existing Subscriptions</h4>
    <p className="mq--p-leading">You are member of <b>TODO</b> projects with access to this asset.</p>
    SUMMARIES
  </Container>
}

function GetStartedAsConsumer(props) {
  const project = _.get(props, 'dataset.data.project.name');
  const asset = _.get(props, 'dataset.data.dataset.name');

  return <Container md background={ Background } className="mq--main-content">
    <h4>Get started with acccess requests</h4>
    <p className="mq--p-leading">
      This data asset is classified as <b>{ _.get(props, 'dataset.data.dataset.classification') }</b>. To access the data, you need to send a request to the data owners of this asset. As soon as the owners have granted your access, you can browse it and work with the data.
    </p>
    
    <ButtonToolbar style={{ marginTop: '30px' }}>
      <Button 
        componentClass={ Link }
        color="green"
        to={ `/new/data-access-request?project=${ project }&asset=${ asset }` }>Create new access request</Button>
    </ButtonToolbar>
  </Container>
}

function ConsumerView(props) {
  return <Container xlg background={ Background } className="mq--main-content">
      <h4>Get started with access requests</h4>

      <p className="mq--p-leading">
        This data asset is classified as <b>TODO</b>. To access the data, you need to send a request to the data owners of this asset. As soon as the owners have granted your access, you can browse it and work with the data.
      </p>

      <Message 
        type="info" 
        title="You can access this data asset" 
        description={
          <>
            <p>
              You are member of 3 projects which have access to this dataset.
            </p>
            <p>
              <a href="#">Project 1</a> | <a href="#">Project 2</a> | <a href="#">Project 3</a>
            </p>
            <ButtonToolbar style={{ marginTop: '30px' }}>
              <Button color="green">Create new access request</Button>
            </ButtonToolbar>
          </>
        } 
      />

      <Message
        type="warning"
        title="You have no access to this data asset"
        description={
          <>
            <p>None of your projects has access to this dataset. Create a new access request to gather allowance to access this data.</p>
            <ButtonToolbar style={{ marginTop: '30px' }}>
              <Button color="green">Create access request</Button>
            </ButtonToolbar>
          </>
        }
      />

      <br />

      <h4>Your access requests</h4>

      <FlexboxGrid>
        <FlexboxGrid.Item colspan={ 4 }>
          <Nav vertical activeKey="open" appearance="subtle">
            <Nav.Item eventKey="open">Open <NavTag>0</NavTag></Nav.Item>
            <Nav.Item eventKey="granted">Granted <NavTag>0</NavTag></Nav.Item>
            <Nav.Item eventKey="closed">Closed <NavTag>0</NavTag></Nav.Item>
          </Nav>
        </FlexboxGrid.Item>
        <FlexboxGrid.Item colspan={ 20 }>

        </FlexboxGrid.Item>
      </FlexboxGrid>
    </Container>;
}

function MemberView(props) {
  return <Container xlg background={ Background } className="mq--main-content">
    <h4>Manage access requests</h4>

    <h4>Subscribe a project</h4>
    <p className="mq--p-leading">
      Hallo Freunde!
    </p>
  </Container>
}

function DataAccessRequests(props) {
  const isPublic = _.get(props, 'dataset.data.isOwner.classification') == 'public';
  const isProjectMember = _.get(props, 'dataset.data.isProjectMember');
  const requests = _.size(_.get(props, 'dataset.data.dataset.accessRequests'));

  if (isProjectMember) {
    return <MemberView { ...props } />;
  } else if (isPublic) {
    return <PublicConsumer { ...props } />;
  } else if (requests > 0) {
    return <ConsumerView { ...props } />;
  } else {
    return <GetStartedAsConsumer { ...props } />
  }
}

DataAccessRequests.propTypes = {};

export default DataAccessRequests;
