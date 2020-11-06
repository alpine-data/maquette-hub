import styled from 'styled-components';

import React from 'react';
import PropTypes from 'prop-types';
import Logo from './maquette_logo.png';
import { Link } from 'react-router-dom';

import { Container, Header, Nav, Navbar, Dropdown, Icon, Divider } from 'rsuite';

const BrandLogo = styled.img`
    height: 100%;
    padding: 0;
    margin-right: 15px;
`;

function Layout(props) {
    return <Container>
        <Header>
            <Navbar appearance="default">
                <Navbar.Header>
                    <Nav>
                        <Nav.Item><BrandLogo src={ Logo } /> <b>Maquette</b> Data Science &amp; Machine Learning Platform</Nav.Item>
                    </Nav>
                </Navbar.Header>
            </Navbar>
        </Header>

        <Header style={{ position: "fixed", width: "100%", zIndex: 999 }}>
            <Navbar appearance="default">
                <Navbar.Header>
                    <Nav>
                        <Nav.Item componentClass={ Link } to="/"><BrandLogo src={ Logo } /> <b>Maquette</b> Data Science &amp; Machine Learning Platform</Nav.Item>
                    </Nav>
                </Navbar.Header>
                <Navbar.Body>
                    <Nav pullRight>
                        <Nav.Item icon={ <Icon icon="bell" /> }>0</Nav.Item>
                        <Dropdown icon={<Icon icon="plus" />} placement="bottomEnd">
                            <Dropdown.Item componentClass={ Link } to="/new/project">New Project</Dropdown.Item>
                            <Dropdown.Item componentClass={ Link } to="/new/dataset">New Dataset</Dropdown.Item>
                            <Dropdown.Item onClick={ () => props.onUserChanged("alice") }>New Collection</Dropdown.Item>
                            <Dropdown.Item onClick={ () => props.onUserChanged("alice") }>New Signal</Dropdown.Item>
                            <Dropdown.Item onClick={ () => props.onUserChanged("alice") }>New Source</Dropdown.Item>
                        </Dropdown>
                        <Dropdown icon={<Icon icon="user" />} title={ props.username } placement="bottomEnd">
                            <Dropdown.Item onClick={ () => props.onUserChanged("alice") }>Impersonate Alice</Dropdown.Item>
                            <Dropdown.Item onClick={ () => props.onUserChanged("bob") }>Impersonate Bob</Dropdown.Item>
                            <Dropdown.Item onClick={ () => props.onUserChanged("clair") }>Impersonate Clair</Dropdown.Item>
                        </Dropdown>
                    </Nav>
                </Navbar.Body>
            </Navbar>
        </Header>
        {props.children}
    </Container>;
}

Layout.propTypes = {
    username: PropTypes.string,
    onUserChange: PropTypes.func
};

Layout.defaultProps = {
    onUserChanged: (user) => {
        console.log(user);
    }
}

export default Layout;