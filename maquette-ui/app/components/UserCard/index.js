/**
 *
 * UserCard
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { Avatar, FlexboxGrid } from 'rsuite';

function UserCard({ user, role }) {
  return <FlexboxGrid align="middle">
    <FlexboxGrid.Item>
      <Avatar src={ user.avatar } />
    </FlexboxGrid.Item>
    <FlexboxGrid.Item style={{ marginLeft: '10px' }}>
      <div className="mq--sub">{ role }</div>
      <Link to={ `/users/${user.id}` } style={{ fontWeight: 'bold', color: '#333' }}>{ user.name }</Link>
    </FlexboxGrid.Item> 
  </FlexboxGrid>;
}

UserCard.propTypes = {};

export default UserCard;
