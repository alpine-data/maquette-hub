/**
 *
 * UserInputPicker
 *
 */

import React from 'react';
import { Avatar, FlexboxGrid, Icon, InputPicker } from 'rsuite';
import PropTypes from 'prop-types';

function UserInputPicker({ name, value, users, onChange }) {
  return <InputPicker 
    name={ name } 
    value={ value } 
    onChange={ onChange } 
    data={ users }
    style={{ width: "100%" }}
    renderMenuItem={ (_, item) => {
      return <FlexboxGrid align="middle">
        <FlexboxGrid.Item>
          <Avatar src={ item.user.avatar } />
        </FlexboxGrid.Item>
        <FlexboxGrid.Item style={{ marginLeft: '10px' }}>
          <b>{ item.user.name }</b><br />
          <span className="mq--sub">{ item.user.title }</span>
        </FlexboxGrid.Item>
      </FlexboxGrid>
    }}
    renderValue={ (_,item) => {
      return <><Icon icon="user" />&nbsp;&nbsp;{ item.user.name }</>;
    }} />;
}

UserInputPicker.propTypes = {
  name: PropTypes.string,
  value: PropTypes.string,
  users: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    user: PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string.isRequired,
      title: PropTypes.string
    })
  }))
};

export default UserInputPicker;
