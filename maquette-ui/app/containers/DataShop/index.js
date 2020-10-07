/**
 *
 * DataShop
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import { connect } from 'react-redux';
import { Helmet } from 'react-helmet';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectDataShop from './selectors';
import reducer from './reducer';
import saga from './saga';

import BadgedButton from 'components/BadgedButton';
import Container from 'components/Container';
import Content from 'components/Content';
import Summary from 'components/Summary';
import Layout from 'components/Layout';
import SearchBox from 'components/SearchBox';
import Section from 'components/Section';

import { Breadcrumb, Button, FlexboxGrid, List, Icon, Panel, PanelGroup } from 'rsuite';
import { Link } from 'react-router-dom';
import { Header } from '../../components/Summary';

export function DataShop() {
  useInjectReducer({ key: 'dataShop', reducer });
  useInjectSaga({ key: 'dataShop', saga });

  return (
    <>
      <Helmet>
        <title>DataShop - Maquette</title>
        <meta name="description" content="Description of DataShop" />
      </Helmet>

      <Layout>
        <SearchBox />

        <Content>
          <Section title='Search results for "foo bar"'>
            <BadgedButton label="1024" active>All</BadgedButton>&nbsp;
            <BadgedButton icon="comment-o" label="12">Comments</BadgedButton>&nbsp;
            <BadgedButton icon="comments-o" label="8">Discussions</BadgedButton>&nbsp;
            <BadgedButton icon="book" label="8">Notebooks</BadgedButton>&nbsp;
            <BadgedButton icon="project" label="8">Projects</BadgedButton>&nbsp;
            <BadgedButton icon="table" label="8">Sets</BadgedButton>&nbsp;
            <BadgedButton icon="retention" label="8">Collections</BadgedButton>&nbsp;
            <BadgedButton icon="realtime" label="8">Streams</BadgedButton>&nbsp;
            <BadgedButton icon="database" label="8">Sources</BadgedButton>&nbsp;
          </Section>

          <Section>
            <FlexboxGrid justify="start">
              <FlexboxGrid.Item colspan={4}>
                <Panel header="Filter Results" bordered>
                  <BadgedButton label="122" size="xs">SomeTag</BadgedButton> <BadgedButton size="xs" label="42">Other Tag</BadgedButton>
                </Panel>
              </FlexboxGrid.Item>
              <FlexboxGrid.Item colspan={1}></FlexboxGrid.Item>
              <FlexboxGrid.Item colspan={11}>
                <PanelGroup>
                  <Summary>
                    <Summary.Header icon="table" category="Data Set">some-dataset/some-project</Summary.Header>
                    <Summary.Body>
                        Lorem ipsum dolor sit amet
                    </Summary.Body>
                    <Summary.Footer>
                      Hello World! &middot; Huhuuu!
                    </Summary.Footer>
                  </Summary>

                  <Summary>
                    <Summary.Header icon="table" category="Data Set">some-dataset/some-project</Summary.Header>
                    <Summary.Body>
                        Lorem ipsum dolor sit amet
                    </Summary.Body>
                    <Summary.Footer>
                      Hello World! &middot; Huhuuu!
                    </Summary.Footer>
                  </Summary>
                </PanelGroup>
              </FlexboxGrid.Item>
            </FlexboxGrid>
          </Section>
        </Content>

        <Content>

        </Content>

        <Breadcrumb>
          <Breadcrumb.Item componentClass={ Link } to="/">Home</Breadcrumb.Item>
          <Breadcrumb.Item componentClass={ Link } to="/">Data Shop</Breadcrumb.Item>
        </Breadcrumb>
        
        <Container>
          
        </Container>
      </Layout>
    </>
  );
}

DataShop.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  dataShop: makeSelectDataShop(),
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

export default compose(withConnect)(DataShop);
