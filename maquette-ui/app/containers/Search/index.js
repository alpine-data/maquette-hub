/**
 *
 * Search
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
import makeSelectSearch from './selectors';
import reducer from './reducer';
import saga from './saga';

import BadgedButton from 'components/BadgedButton';
import Container from 'components/Container';
import Summary from 'components/Summary';

import { ButtonToolbar, Icon, FlexboxGrid, InputGroup, Input, Radio, RadioGroup } from 'rsuite';

export function Search() {
  useInjectReducer({ key: 'search', reducer });
  useInjectSaga({ key: 'search', saga });

  return (
    <div>
      <Helmet>
        <title>Search</title>
        <meta name="description" content="Description of Search" />
      </Helmet>

      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }><h1>Search Assets</h1></FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
      </div>

      <Container md className="mq--main-content">
        <InputGroup size="lg">
          <Input placeholder="Search Maquette Assets" onFocus={ () => this.setState({ show: true }) } onBlur={ () => this.setState({ show: false }) } />
          <InputGroup.Button>Search</InputGroup.Button>
        </InputGroup>
        <RadioGroup inline name="scope" value="all">
          <Radio value="all">All assets</Radio>
          <Radio value="own">My assets</Radio>
        </RadioGroup>

        <hr />

        <ButtonToolbar>
          <BadgedButton icon="table" label="8" size="sm">Sets</BadgedButton>
          <BadgedButton icon="retention" label="8" size="sm">Collections</BadgedButton>
          <BadgedButton icon="realtime" label="8" size="sm">Streams</BadgedButton>
          <BadgedButton icon="database" label="8" size="sm">Sources</BadgedButton>
          <BadgedButton icon="comment-o" label="12">Comments</BadgedButton>
          <BadgedButton icon="comments-o" label="8">Discussions</BadgedButton>
          <BadgedButton icon="book" label="8">Notebooks</BadgedButton>
          <BadgedButton icon="project" label="8">Projects</BadgedButton>
        </ButtonToolbar>

        <Summary.Summaries>
          <Summary to={ `${name}/resources/datasets/some-project` }>
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
        </Summary.Summaries>
      </Container>
    </div>
  );
}

Search.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  search: makeSelectSearch(),
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

export default compose(withConnect)(Search);
