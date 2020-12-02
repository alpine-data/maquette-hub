import styled from 'styled-components';

import React from 'react';
import PropTypes from 'prop-types';
import Logo from './maquette-logo.svg';
import LogoText from './maquette-logo-text.svg';
import LogoKPMG from './kpmg-logo.svg';
import { Link } from 'react-router-dom';

import { Container, Header, Nav, Navbar, Dropdown, Icon, Divider, FlexboxGrid, Grid, Row, Col } from 'rsuite';

const BrandLogo = styled.img`
    height: 100%;
    padding: 0;
    margin-right: 15px;
`;

const BrandLogoText = styled.img`
    height: 35px;
`;

function Layout(props) {
    return <>
        <Container className="mq--container-wrap">
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
                                <Dropdown.Item componentClass={ Link } to="/new/project">New project</Dropdown.Item>
                                <Dropdown.Item componentClass={ Link } to="/new/dataset">New dataset</Dropdown.Item>
                                <Dropdown.Item componentClass={ Link } to="/new/stream">New stream</Dropdown.Item>
                                <Dropdown.Item componentClass={ Link } to="/new/datasource">New data source</Dropdown.Item>
                                <Dropdown.Item componentClass={ Link } to="/new/collection">New collection</Dropdown.Item>
                                <Dropdown.Item componentClass={ Link } to="/new/repositoy">New data repository</Dropdown.Item>
                                <Dropdown.Item componentClass={ Link } to="/new/sandbox">New sandbox</Dropdown.Item>
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
        </Container>

        <div className="mq--footer">
            <div className="mq--footer--content">
                <Grid>
                    <Row>
                        <Col xs={ 24 } md={ 6 }>
                            <BrandLogoText src={ LogoText } />
                        </Col>
                        <Col xs={ 24 } md={ 6 } className="mq--main-content">
                            <h4>QUICK LINKS</h4>
                            <p>
                                <Link to="/">Personal Dashboard</Link><br />
                                <Link to="/">My Projects</Link><br />
                                <Link to="/">Notifications</Link><br /><br />
                                <Link to="/">Home</Link><br />
                                <Link to="/">Search</Link>
                            </p>
                        </Col>
                        <Col xs={ 24 } md={ 6 } className="mq--main-content">
                            <h4>SUPPORT</h4>
                            <p>
                                <Link to="/">Getting Started</Link><br />
                                <Link to="/">Java SDK</Link><br />
                                <Link to="/">Python SDK</Link><br /><br />
                                <Link to="/">GitHub Project</Link><br />
                                <Link to="/">Issues</Link>
                            </p>
                        </Col>
                        <Col xs={ 24 } md={ 6 } className="mq--main-content">
                            <h4>&nbsp;</h4>
                            <p>
                                Made with ♥ at KPMG Switzerland.
                            </p>
                            <p className="mq--small">
                                Maquette is an Open Source endeavor published under Apache 2.0 License. Find out more about Maquette at <Link to="#">kpmg.ch</Link>.
                            </p>
                            <img src={ LogoKPMG } style={{ height: "30px "}} />
                        </Col>
                    </Row>
                </Grid>
            </div>
        </div>
    </>;
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