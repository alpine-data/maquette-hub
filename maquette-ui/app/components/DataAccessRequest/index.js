/**
 *
 * DataAccessRequest
 *
 */
import _, { reject } from 'lodash';
import React, { useState } from 'react';

import produce from 'immer';

import { Badge, Button, ButtonToolbar, Checkbox, ControlLabel, DatePicker, FlexboxGrid, Form, FormControl, FormGroup, HelpBlock, Input, Radio, RadioGroup, Timeline } from 'rsuite';
import FlexboxGridItem from 'rsuite/lib/FlexboxGrid/FlexboxGridItem';

function Respond({ onGrant = console.log, onReject = console.log, request, updating = false, ...props }) {
  const [state, setState] = useState({
    decision: "grant",
    message: "",
    error: false,
    validity_date: new Date()
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  const grant_onClick = () => {
    const args = {
      project: props.project,
      dataset: props.dataset,
      id: request.id,
      until: state.decision == "grant-limited" && state.validity_date.toJSON() || null,
      message: state.message
    } 

    onGrant(args);
  }

  const reject_onClick = () => {
    const args = {
      project: props.project,
      dataset: props.dataset,
      id: request.id,
      reason: state.message
    }

    onReject(args);
  }

  const grantControls = () => {
    return <>
      <FormGroup>
        <ControlLabel>Message</ControlLabel>
        <Input 
          componentClass="textarea" 
          rows={ 5 } 
          value={ state.message } 
          onChange={ onChange('message') }
          disabled={ updating } />
        <HelpBlock>Optional. Provide additional advices or remarks.</HelpBlock>
      </FormGroup>
      {
        state.decision == "grant-limited" && <FormGroup>
          <ControlLabel>Access valid until</ControlLabel>
          <DatePicker 
            format="YYYY-MM-DD HH:mm" 
            value={ state.validity_date } 
            disabled={ updating }
            onChange={ onChange('validity_date') } />
        </FormGroup>
      }
      <FormGroup>
        <ButtonToolbar>
          <Button 
            color="green" 
            onClick={ grant_onClick } 
            loading={ updating }>Grant Access</Button>
        </ButtonToolbar>
      </FormGroup>
    </>;
  }

  const isValid = !_.isEmpty(state.message);

  const rejectControls = () => {
    return <>
      <FormGroup>
        <ControlLabel>Reason</ControlLabel>
        <Input 
          componentClass="textarea" 
          rows={ 5 } 
          value={ state.message } 
          onChange={ onChange('message') } 
          disabled={ updating } />
        <HelpBlock>Justify your decision, tell what you need to allow the request.</HelpBlock>
      </FormGroup>
      <FormGroup>
        <ButtonToolbar>
          <Button 
            color="red" 
            onClick={ reject_onClick } 
            disabled={ !isValid } 
            loading={ updating }>Reject Access</Button>
        </ButtonToolbar>
      </FormGroup>
    </>;
  }

  return <Timeline.Item>
    <p style={{ fontWeight: "bold" }}>Response</p>
    <FormGroup>
      <ControlLabel>Decision</ControlLabel>
      <RadioGroup 
        value={ state.decision } 
        onChange={ onChange('decision') }
        disabled={ updating }>

        <Radio value="grant">Grant data access</Radio>
        <Radio value="grant-limited">Grant data access for limited period</Radio>
        <Radio value="reject">Reject data access or request further information</Radio>
      </RadioGroup>
    </FormGroup>

    { (state.decision == "grant" || state.decision == "grant-limited") && grantControls() }
    { state.decision == "reject" && rejectControls() }
  </Timeline.Item>;
}

function Withdraw({ updating, request, onWithdraw = console.log, ...props }) {
  const [state, setState] = useState({
    message: ""
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  const withdraw_onClick = () => {
    const args = {
      project: props.project,
      dataset: props.dataset,
      id: request.id,
      message: state.message
    }

    onWithdraw(args);
  }

  const isValid = !_.isEmpty(state.message);

  return <Timeline.Item>
    <p style={{ fontWeight: "bold" }}>Withdraw</p>

    <FormGroup>
      <ControlLabel>Reason</ControlLabel>
      <Input 
        componentClass="textarea" 
        rows={ 5 } 
        value={ state.message } 
        onChange={ onChange('message') } 
        disabled={ updating } />
      <HelpBlock>Explain why you want to withdraw the data access.</HelpBlock>
    </FormGroup>
    <FormGroup>
      <ButtonToolbar>
        <Button 
          onClick={ withdraw_onClick }
          loading={ updating }
          disabled={ !isValid }>Withdraw</Button>
      </ButtonToolbar>
    </FormGroup>
  </Timeline.Item>;
}

function Request({ updating, request, onRequest = console.log, ...props }) {
  let label = "Revive Request";

  if (request.status == "rejected") {
    label = "Refine Request";
  }

  const [state, setState] = useState({
    message: ""
  });

  const onChange = (field) => (value) => {
    setState(produce(state, draft => {
      draft[field] = value;
    }));
  }

  const request_onClick = () => {
    const args = {
      project: props.project,
      dataset: props.dataset,
      id: request.id,
      message: state.message
    }

    onRequest(args);
  }

  const isValid = !_.isEmpty(state.message);

  return <Timeline.Item>
    <p style={{ fontWeight: "bold" }}>{ label }</p>

    <FormGroup>
      <ControlLabel>Reason</ControlLabel>
      <Input 
        componentClass="textarea" 
        rows={ 5 } 
        value={ state.message } 
        onChange={ onChange('message') } 
        disabled={ updating } />
      <HelpBlock>Explain why you want to withdraw the data access.</HelpBlock>
    </FormGroup>
    <FormGroup>
      <ButtonToolbar>
        <Button 
          onClick={ request_onClick }
          loading={ updating }
          disabled={ !isValid }>Submit</Button>
      </ButtonToolbar>
    </FormGroup>
  </Timeline.Item>;
}


function Action({ action, ...props }) {
  if (action == "respond") {
    return <Respond { ...props } />;
  } else if (action == "withdraw") {
    return <Withdraw { ...props } />;
  }  else if (action == "request") {
    return <Request { ...props } />;
  }  else {
    return <></>;
  }
}

function TimelineItem({ event }) {
  return <Timeline.Item>
      <p style={{ fontWeight: "bold" }}>{ _.capitalize(event.event)  } <span className="mq--sub">{ new Date(event.created.at).toLocaleString() } by { event.created.by }</span></p>
      <p>{ event.reason || event.message }</p>
    </Timeline.Item>;
}

function DataAccessRequest({ project, dataset, request, updating, onGrant, onReject, onRequest, onWithdraw }) {
  const statusColors = {
    "requested": "orange",
    "rejected": "red",
    "granted": "green",
    "withdrawn": "cyan",
    "expired": "violet"
  }

  const initial = _.last(request.events);

  return <>
    <Form>  
      <FlexboxGrid align="middle">
        <FlexboxGridItem colspan={ 16 }>
          <FormGroup style={{ marginBottom: 0 }}>
            <HelpBlock>Data Access Request from</HelpBlock>
            <h3 style={{ marginBottom: "10px" }}>{ request.origin.title } <span className="mq--sub">#{ request.id }</span></h3>

            <ButtonToolbar>
              <Button color={ statusColors[request.status] }>{ _.capitalize(request.status) }</Button>
              <Button>
                <b>{ initial.created.by }</b> opened this request on { new Date(initial.created.at).toLocaleDateString() } &middot; { request.events.length - 1 } interactions
              </Button>
            </ButtonToolbar>
          </FormGroup>
        </FlexboxGridItem>
        <FlexboxGridItem colspan={ 8 }>
          <ButtonToolbar>
            <Button appearance="ghost">View Access Logs</Button>
            <Button appearance="primary" href={ `/${request.origin.name}` } target="_blank">View Project</Button>
          </ButtonToolbar>
        </FlexboxGridItem>
        <FlexboxGridItem colspan={ 24 }>
          <hr />
          <FormGroup>
            <HelpBlock>Initial reason for accessing the data</HelpBlock>
            <p className="mq--p-leading">
              { initial.reason }
            </p>
          </FormGroup>

          <hr />

          <Timeline>
            { _.map(request.actions, action => <Action 
              key={ action } 
              action={ action } 
              project={ project } 
              dataset={ dataset } 
              request={ request }
              updating={ updating }
              onGrant={ onGrant }
              onReject={ onReject }
              onRequest={ onRequest }
              onWithdraw={ onWithdraw } /> ) }

            { _.map(request.events, e => <TimelineItem key={ e.created.at } event={ e } />) }
          </Timeline>
        </FlexboxGridItem>
      </FlexboxGrid>
    </Form>
  </>;
}

DataAccessRequest.propTypes = {};

export default DataAccessRequest;