import styled from 'styled-components';

import React from 'react';
import Logo from './maquette_logo.png';
import { Link } from 'react-router-dom';

import { Button, Container, Header, Nav, Navbar, Dropdown, Icon, Divider } from 'rsuite';

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
                        <Nav.Item><BrandLogo src={ Logo } /> KPMG <b>Maquette</b> <Divider vertical /> Data &amp; Analytics Platform</Nav.Item>
                    </Nav>
                </Navbar.Header>
                <Navbar.Body>
                    <Nav pullRight>
                        <Nav.Item icon={<Icon icon="home" />}>Home</Nav.Item>
                        <Nav.Item>News</Nav.Item>
                        <Nav.Item>Products</Nav.Item>
                        <Dropdown title="About">
                            <Dropdown.Item>Company</Dropdown.Item>
                            <Dropdown.Item>Team</Dropdown.Item>
                            <Dropdown.Item>Contact</Dropdown.Item>
                        </Dropdown>
                        <Nav.Item icon={<Icon icon="cog" />}>Settings</Nav.Item>
                    </Nav>
                </Navbar.Body>
            </Navbar>
        </Header>
        {props.children}
    </Container>;
}

Layout.propTypes = {};

export default Layout;