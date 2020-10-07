/**
 *
 * DatasetDetails
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectDatasetDetails from './selectors';
import reducer from './reducer';
import saga from './saga';

import styled from 'styled-components';

import { Container, FlexboxGrid, Icon, IconButton, Nav, Panel, PanelGroup, Sidebar } from 'rsuite';

import ContentContainer from 'components/Container'
import Content from 'components/Content';
import ContentHeader from 'components/ContentHeader';
import Layout from 'components/Layout';

const MainContent = styled.div`
  margin: 20px 20px 20px 0;
`;

const SidebarContent = styled.div`
  margin: 20px 0 20px 20px;
`;

export function DatasetDetails() {
  useInjectReducer({ key: 'datasetDetails', reducer });
  useInjectSaga({ key: 'datasetDetails', saga });

  return (
    <div>
      <Helmet>
        <title>DatasetDetails</title>
        <meta name="description" content="Description of DatasetDetails" />
      </Helmet>

      <Layout>
        <ContentContainer>
          <Content>
            <FlexboxGrid align="middle">
              <FlexboxGrid.Item colspan={16}>
                <ContentHeader>
                  <ContentHeader.Subtitle>
                    <Icon icon="table" /> Data Set
                  </ContentHeader.Subtitle>

                  <ContentHeader.H3>some-prject/some-dataset</ContentHeader.H3>

                  <ContentHeader.Subtitle>
                    Last modified Foo Bar
                  </ContentHeader.Subtitle>
                </ContentHeader>
              </FlexboxGrid.Item>
              
              <FlexboxGrid.Item colspan={8} style={{ textAlign: "right" }}>
                <IconButton icon={<Icon icon="star-o" />} placement="left">42</IconButton>
                &nbsp;&nbsp;
                <IconButton icon={<Icon icon="arrow-circle-o-down" />} placement="left">9</IconButton>
                &nbsp;&nbsp;
                <IconButton icon={<Icon icon="briefcase" />} placement="left">5.4</IconButton>
              </FlexboxGrid.Item>
            </FlexboxGrid>
            
            <Nav activeKey="foo" appearance="subtle">
              <Nav.Item eventKey="foo">Overview</Nav.Item>
              <Nav.Item>Sample</Nav.Item>
              <Nav.Item>Permissions</Nav.Item>
              <Nav.Item>Settings</Nav.Item>
              <Nav.Item>Usage Statistics</Nav.Item>
            </Nav>

            <Container>
              <Container>
                  <Panel bordered header="Summary" style={{ marginTop: "20px", marginRight: "40px" }}>
                    Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.
                  </Panel>
                  
                  <Panel bordered header="Description" style={{ marginTop: "20px", marginRight: "40px" }}>
                    <h4>Foo bar tralala</h4>
                    <p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</p>
                  </Panel>
              </Container>
              <Sidebar>
                <Panel bordered header="Versions" style={{ marginTop: "20px" }}>
                  0.0.1<br />
                  0.0.3<br />
                </Panel>
              </Sidebar>
            </Container>
          </Content>
        </ContentContainer>
      </Layout>
    </div>
  );
}

DatasetDetails.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  datasetDetails: makeSelectDatasetDetails(),
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

export default compose(withConnect)(DatasetDetails);
