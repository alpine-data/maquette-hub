/**
 *
 * StartSearch
 *
 */

import _ from 'lodash';
import React, { useState } from 'react';
import PropTypes from 'prop-types';

import { Link } from 'react-router-dom';
import { ButtonToolbar, Form, FormGroup, Icon, IconButton, Input } from 'rsuite';

function StartSearch({ title, placeholder, searchAllLabel, searchLabel, link }) {
  
  const [value, setValue] = useState('');

  return <>
    <h5>{ title }</h5>
    <Form fluid>
      <FormGroup>
        <Input placeholder={ placeholder } value={ value } onChange={ v => setValue(v) } size="lg" />
      </FormGroup>
      <ButtonToolbar style={{ textAlign: "right" }}>
        {
          _.isEmpty(value) && <IconButton 
            color="green" 
            placement="right" 
            icon={<Icon icon="arrow-circle-right" />} 
            size="lg"
            componentClass={ Link }
            to={ `${link}?browse` } >{ searchAllLabel }</IconButton>
        }

        {
          !_.isEmpty(value) && <IconButton
            type="submit"
            color="green"
            placement="right"
            icon={<Icon icon="arrow-circle-right" />}
            size="lg"
            componentClass={ Link }
            to={ `${link}?q=${encodeURIComponent(value).replace(/%20/g, "+")}`}>{ searchLabel }</IconButton>
        }
      </ButtonToolbar>
    </Form>
  </>;
}

StartSearch.propTypes = {
  title: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  searchAllLabel: PropTypes.string,
  searchLabel: PropTypes.string,
  link: PropTypes.string.isRequired
};

StartSearch.defaultProps = {
  placeholder: 'Keywords, Topics, ...',
  searchAllLabel: 'Browse all',
  searchLabel: 'Search'
}

export default StartSearch;
