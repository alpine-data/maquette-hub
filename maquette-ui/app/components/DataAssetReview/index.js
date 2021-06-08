/**
 *
 * DataAssetReview
 *
 */

import _ from 'lodash';
import React from 'react';

import { Button, ButtonToolbar, ControlLabel, FlexboxGrid, Form, FormGroup, HelpBlock, Input, Radio, RadioGroup, Timeline } from 'rsuite';
import { useFormState } from 'utils/hooks';
import { formatTime } from 'utils/helpers';

function DataAssetReview({ asset, logs, permissions, onApprove, onDecline, onRequestReview }) {
  const initialState = {
    decision: 'approve',
    reason: '',
    message: ''
  };

  const [state, setState, onChange] = useFormState(initialState);
  const isValid = state.decision === 'approve' || (state.decision === 'decline' && !_.isEmpty(state.reason))

  return <>
      <Timeline>
        <Timeline.Item>
          {
            _.get(asset, 'properties.state') === 'review-required' && _.get(permissions, 'canReview') && <>
              <p style={{ fontWeight: "bold" }}>Review Required</p>
              <p className="mq--p-leading">
                The configuration of this asset requires a review. Please assess the asset and approve or decline the asset.
              </p>

              <FlexboxGrid>
                <FlexboxGrid.Item colspan={ 12 }>
                  <Form>
                    <FormGroup>
                      <ControlLabel>Decision</ControlLabel>
                      <RadioGroup value="approve" onChange={ onChange('decision') } value={ state.decision }>
                        <Radio value="approve">Approve</Radio>
                        <Radio value="decline">Decline</Radio>
                      </RadioGroup>
                    </FormGroup>

                    {
                      state.decision === 'decline' && <>
                        <FormGroup>
                          <ControlLabel>Reason</ControlLabel>
                          <Input 
                            componentClass="textarea" 
                            rows={ 5 } 
                            value={ state.reason } 
                            onChange={ onChange('reason') } />
                          <HelpBlock>Justify your decision, tell what you need to approve the asset.</HelpBlock>
                        </FormGroup>
                      </>
                    }

                    <FormGroup>
                      <ButtonToolbar>
                        <Button 
                          appearance="primary"
                          onClick={ () => {
                            if (state.decision === 'approve') {
                              onApprove()
                            } else {
                              onDecline(state.reason);
                            }

                            setState(initialState);
                          } }
                          disabled={ !isValid }>{ state.decision === 'approve' && <>Approve</> || <>Decline</> }</Button>
                      </ButtonToolbar>
                    </FormGroup>
                  </Form>
                </FlexboxGrid.Item>
              </FlexboxGrid>
            </>
          }

          {
            _.get(asset, 'properties.state') === 'review-required' && !_.get(permissions, 'canReview') && <>
              <p style={{ fontWeight: "bold" }}>Review Required</p>
              <p className="mq--p-leading">
                The configuration of this asset requires a review. The Data Owner must assess the configuration of this asset.
              </p>
            </>
          }

          {
            _.get(asset, 'properties.state') === 'declined' && <>
              <p style={{ fontWeight: "bold" }}>Declined</p>
              <p className="mq--p-leading">
                The configuration of this asset has been declined, please consider to make changes to the configuration of the asset to gain approval by the Data Owner.
              </p>

              <FlexboxGrid>
                <FlexboxGrid.Item colspan={ 12 }>
                  <Form>
                    <FormGroup>
                      <ControlLabel>Message</ControlLabel>
                      <Input 
                        componentClass="textarea" 
                        rows={ 5 } 
                        value={ state.message } 
                        onChange={ onChange('message') } />
                      <HelpBlock>Describe why you request the review.</HelpBlock>
                    </FormGroup>

                    <FormGroup>
                      <ButtonToolbar>
                        <Button 
                          appearance="primary"
                          onClick={ () => {
                            onRequestReview(state.message);
                            setState(initialState);
                          } }
                          disabled={ _.size(state.message) < 3 }>Request new assessment</Button>
                      </ButtonToolbar>
                    </FormGroup>
                  </Form>
                </FlexboxGrid.Item>
              </FlexboxGrid>
            </>
          }

          {
            _.get(asset, 'properties.state') === 'approved' && <>
              <p style={{ fontWeight: "bold" }}>Approved</p>
              <p className="mq--p-leading">
                The configuration of this asset has been approved.
              </p>
            </>
          }
        </Timeline.Item>
        {
          _
            .chain(logs)
            .filter(l => _.get(l, 'action.category') === 'administration')
            .orderBy(l => new Date(l.logged))
            .reverse()
            .map(l => <Timeline.Item key={ l.logged }>
              <p style={{ fontWeight: "bold" }}>{ _.get(l, 'user.name') } ({ _.get(l, 'user.id') }) <span className="mq--sub">{ formatTime(l.logged) }</span></p>
              <p>{ l.action.message }</p>
            </Timeline.Item>)
            .value()
        }
      </Timeline>
    </>
}

DataAssetReview.propTypes = {};

export default DataAssetReview;
