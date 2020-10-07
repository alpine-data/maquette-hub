/**
 *
 * SearchBox
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import Content from '../Content';
import Section from '../Section';

import { Animation, Button, Icon, Input, InputGroup, List } from 'rsuite';
import { Link } from 'react-router-dom';

const SearchInputGroup = styled(InputGroup)`
  border-radius: 0 !important;
  border-top-width: 0 !important;
  border-right-width: 0 !important;
  border-left-width: 0 !important;
`;

const SearchInput = styled(Input)`
  border-radius: 0 !important;
`;

const Panel = React.forwardRef(({ ...props }, ref) => (
  <Content { ...props } ref={ref}>
    <Section title="Recent Searches">
      <List size="lg">
        <List.Item><Icon icon="trend" /> <Link to="/">Stock markets</Link></List.Item>
        <List.Item><Icon icon="trend" /> <Link to="/">Foo bar</Link></List.Item>
      </List>
    </Section>

    <Section title="Popular Tags">
      <Button>Foo</Button> <Button>Bar</Button>
    </Section>
  </Content>
));

class SearchBox extends React.Component {

  constructor(props) {
    super(props);
    this.handleToggle = this.handleToggle.bind(this);
    this.state = {
      show: false
    };
  }

  handleToggle() {
    this.setState({
      show: !this.state.show
    });
  }

  render() {
    return <>
      <SearchInputGroup size="lg">
        <InputGroup.Addon>
          <Icon icon="search" />
        </InputGroup.Addon>
        <SearchInput placeholder="Search Maquette Assets" onFocus={ () => this.setState({ show: true }) } onBlur={ () => this.setState({ show: false }) } />
        <InputGroup.Button>Search</InputGroup.Button>
      </SearchInputGroup>

      <Animation.Collapse in={this.state.show}>{(props, ref) => <Panel {...props} ref={ref} />}</Animation.Collapse>
    </>;
  }

}

export default SearchBox;
