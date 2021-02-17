/**
 *
 * UserProfileOverview
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { Button, FlexboxGrid } from 'rsuite';
import styled from 'styled-components';

import { ProjectSummary } from '../../containers/Dashboard';

import Container from '../Container';
import { Asset } from '../DataAssetBrowser';
import Summary from '../Summary';

const Lines = styled.div`
  margin-bottom: 20px;
`;

export function Avatar({ src }) {
  return <div 
    style={ { maxWidth: '150px', maxHeight: '150px', width: '100%', height: '100%' } } 
    className="rs-avatar rs-avatar-lg rs-avatar-circle">
    <img 
      style={ { width: '100%', height: '100%' } }
      className="rs-avatar-image" src={ `${src}&s=200` } />
  </div>;
}

export function Line({ value, defaultValue }) {
  return (value && <p style={{ margin: '0px' }}>{ value }</p>) || (defaultValue && <p className="mq--sub" style={{ margin: '0px' }}>{ defaultValue }</p> ) || <></>
}


function UserProfileOverview({ view }) {
  return <Container lg>
      <FlexboxGrid justify="space-between">
        <FlexboxGrid.Item colspan={6} style={{ textAlign: 'center' }}>
          <Avatar src={ view.profile.avatar } />
          <Lines>
            <Line value={ view.profile.title } defaultValue="Data Schmock" />
            <Line value={ view.profile.location } defaultValue="Entenhausen" />
          </Lines>

          <Lines>
            <Line value={ view.profile.email } />
            <Line value={ view.profile.phone && <><b>phone</b> { view.profile.phone }</> } />
          </Lines>

          { 
            view.isOwnProfile && <>
              <Lines>
                <Button componentClass={ Link } to="/user/settings" appearance="primary">Edit profile</Button>
              </Lines> 
            </>
          }
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={17}>
          <h4>Projects</h4>

          { 
            _.size(view.projects) == 0 && <>
              <p>The user is not member in any project.</p>
            </>
          }

          { 
            _.size(view.projects) > 0 && <>
              <Summary.Summaries>
                { _.map(view.projects, p => <ProjectSummary project={ p } key={ p.name } />) }
              </Summary.Summaries>
            </>
          }

          <hr />
          
          <h4>Data Assets</h4>

          { 
            _.size(view.dataAssets) == 0 && <>
              <p>The user manages no data asset.</p>
            </>
          }

          {
            _.size(view.dataAssets) > 0 && <>
              <Summary.Summaries>
                { _.map(view.dataAssets, asset=> <Asset asset={ asset } key={ asset.name } />) }
              </Summary.Summaries>
            </>
          }
        </FlexboxGrid.Item>
      </FlexboxGrid>
  </Container>
}

UserProfileOverview.propTypes = {};

export default UserProfileOverview;
