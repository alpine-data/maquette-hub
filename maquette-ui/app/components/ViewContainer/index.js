/**
 *
 * ViewContainer
 *
 */
import _ from 'lodash';
import React from 'react';
import PropTypes, { bool } from 'prop-types';
// import styled from 'styled-components';

import { Affix, Button, ButtonToolbar, FlexboxGrid, Icon, Loader, Placeholder, Message, Nav, Whisper, Tooltip } from 'rsuite';
import { Link } from 'react-router-dom';
import { Helmet } from 'react-helmet';

import Container from 'components/Container';
import EditableParagraph from 'components/EditableParagraph';

import ErrorTitles from './error_titles.json';
import LoaderMessages from './loading_messages.json';
import ProjectBackground from '../../resources/projects-background.png';

const backgrounds = {
  data: ProjectBackground,
  projects: ProjectBackground
};

function NoContent() {
  return <Container>
    <h4>Oops!</h4>
    <p className="mq--p-leading">This is awkward... You are looking for something that doesn't actually exist.</p>
  </Container>
}

function ViewContainer({ 
  background, canChangeSummary, loading,
  likes, liked, likeText, likedText, 
  titles, summary, error, tabs, 
  onChangeLike, onChangeSummary, onCloseError, ...props }) {

  const visibleTabs = _.filter(tabs, tab => tab.visible);
  const activeTabKey = _.get(props, 'match.params.tab') || _.get(_.first(visibleTabs), 'key') || 'none';
  const activeTab = _.find(visibleTabs, tab => tab.key === activeTabKey)
  const defaultContentFactory = _.isEmpty(tabs) && (() => props.children) || NoContent
  const childrenFactory = activeTab && activeTab.component || defaultContentFactory;

  const pageTitle = (_.isEmpty(titles) && 'Maquette') || `${ _.last(titles).label } - Maquette`

  let actualBackground = background;
  if (activeTab && activeTab.background === false) {
    actualBackground = false;
  }


  return <>
    <Helmet>
      <title>{ pageTitle }</title>
    </Helmet>

    <Affix top={56}>
      <div className="mq--page-title">
        <Container fluid>
          <FlexboxGrid align="middle">
            <FlexboxGrid.Item colspan={ 20 }>
              <h1>
                { 
                  _.reduce(_.map(titles, title => {
                    if (title.link) {
                      return <Link key={ title.label } to={ title.link }>{ title.label }</Link>
                    } else {
                      return <>{ title.label }</>
                    }
                  }), (result, value) => {
                    return <>{ result } / { value }</>
                  })
                }
              </h1>

              { 
                summary && <>
                  <EditableParagraph 
                    value={ summary } 
                    onChange={ onChangeSummary }
                    disabled={ !canChangeSummary }
                    className="mq--p-leading" /> 
                </>
              }
            </FlexboxGrid.Item>
            <FlexboxGrid.Item colspan={ 4 } className="mq--buttons">
              { 
                likes && <>
                  <ButtonToolbar>
                    {
                      liked && <>
                          <Whisper
                            trigger="hover"
                            placement="left"
                            speaker={ <Tooltip>{ likedText }</Tooltip> }>

                            <Button 
                              size="sm" 
                              active
                              onClick={ () => onChangeLike(false) }>

                                <Icon icon="star" />&nbsp;&nbsp;{ likes }
                            </Button>
                          </Whisper>
                        </> || <>
                          <Whisper
                            trigger="hover"
                            placement="left"
                            speaker={ <Tooltip>{ likeText }</Tooltip> }>

                            <Button 
                              size="sm"
                              onClick={ () => onChangeLike(true) }>
                                
                                <Icon icon="star-o" />&nbsp;&nbsp;{ likes }
                            </Button>
                          </Whisper>
                        </>
                    }
                  </ButtonToolbar>
                </>
              }
            </FlexboxGrid.Item>
          </FlexboxGrid>
        </Container>
        
        { 
          !_.isEmpty(tabs) && <>
            <Nav appearance="subtle" activeKey={ activeTabKey } className="mq--nav-tabs">
              { 
                _.map(visibleTabs, tab => {
                  return <Nav.Item key={ tab.key } eventKey={ tab.key } componentClass={ Link } to={ tab.link }>{ tab.label }</Nav.Item>
                })
              }
            </Nav>
          </>  
        }
      </div>
    </Affix>

    <div className='mq--main-content mq--page-background' style={ (actualBackground && backgrounds[actualBackground] && { backgroundImage: `url(${backgrounds[actualBackground]})` }) || {} }>
      {
        error && <>
          <Container fluid>
            <Message 
              closable 
              showIcon
              type="error" 
              onClose={ onCloseError }
              title={ _.sample(ErrorTitles) }
              description={ <p className="mq--p-leading" style={{ marginBottom: 0 }}>{ error }</p> } />
          </Container>
        </>
      }

      {
        loading && <>
          <Loader vertical size="lg" center content={ _.sample(LoaderMessages) } />
        </> || <>
          { childrenFactory() }
        </>
      }
    </div>
  </>;
}

ViewContainer.propTypes = {
  tabs: PropTypes.arrayOf(PropTypes.shape({
    label: PropTypes.string.isRequired,
    link: PropTypes.string.isRequired,
    key: PropTypes.string.isRequired,
    component: PropTypes.func.isRequired,
    visible: PropTypes.bool,
    background: PropTypes.oneOfType([PropTypes.bool, PropTypes.oneOf(_.keys(backgrounds))])
  })),

  background: PropTypes.oneOf(_.keys(backgrounds)),
  loading: PropTypes.bool,

  likes: PropTypes.number,
  liked: PropTypes.bool,
  likedText: PropTypes.string,
  likeText: PropTypes.string,
  onLike: PropTypes.func,

  titles: PropTypes.arrayOf(PropTypes.shape({
    link: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired
  })),

  canChangeSummary: bool,
  error: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
  summary: PropTypes.string,
  onChangeSummary: PropTypes.func,
  onCloseError: PropTypes.func
};

ViewContainer.defaultProps = {
  tabs: [],
  titles: [],

  loading: true,

  likeText: 'Start starring this',
  likedText: 'You starred this',

  onChangeLike: console.log,
  onChangeSummary: console.log
}

export default ViewContainer;
