/**
 *
 * UserCard
 *
 */

import React from 'react';
import { Link } from 'react-router-dom';
import { Avatar, FlexboxGrid } from 'rsuite';

function UserCard({ user, role, users = {} }) {
  if (typeof(user) === 'string') {
    user = users[user] || { id: user, name: user }
  }

  return <FlexboxGrid align="middle" style={{ marginBottom: '10px' }}>
    <FlexboxGrid.Item style={{ lineHeight: 0 }}>
      <Avatar src={ user.avatar } />
    </FlexboxGrid.Item>
    <FlexboxGrid.Item style={{ marginLeft: '10px' }}>
      { role && <div className="mq--sub">{ role }</div> }
      <Link to={ `/users/${user.id}` } style={{ fontWeight: 'bold', color: '#333' }}>{ user.name }</Link>
    </FlexboxGrid.Item> 
  </FlexboxGrid>;
}

UserCard.propTypes = {};

export default UserCard;
