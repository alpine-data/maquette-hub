/**
 *
 * CollectionOverview
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import CollectionTimeline from '../CollectionTimeline';
import Container from '../Container';
import Background from '../../resources/datashop-background.png';
import FileExplorer from '../FileExplorer';
import { Link } from 'react-router-dom';

import ErrorMessage from '../ErrorMessage';

function Files({ path, tag, blobPrefix, treePrefix, ...props}) {
  console.log(path);
  const collection = _.get(props, 'match.params.collection');
  const query = new URLSearchParams(_.get(props, 'location.search') || '');
  const selectedFile = query.get('file');

  const pathElements = _.chain(path).split('/').filter(s => !_.isEmpty(s)).value();
  const pathElementLinks = _.reduce(
    pathElements, 
    (links, e) => {
      const prev = _.last(links);
      return _.concat(links, { to: prev.to + '/' + e, label: e });
    },
    [{ to: treePrefix, label: collection }])

  const tag_data = _.get(props, 'collection.data.collection.tags')
  const root_data = _.isEqual(tag, 'main') && _.get(props, 'collection.data.collection.files') || _.get(_.find(tag_data, t => _.isEqual(t.name, tag)), 'content');
  const data = _.reduce(
      pathElements, 
      (acc, element) => _.get(acc, `children.${element}`) || {},
      root_data || {});

  console.log(pathElementLinks);

  return <Container xlg background={ Background } className="mq--main-content">
    <CollectionTimeline 
      activeTag={ tag }
      collection={ _.get(props, 'collection.data.collection') }
      onSelect={ tag => props.history.push(`/shop/collections/some-collection/tree/${tag}`) } />
    
    <hr />

    { 
      _.isEmpty(root_data) && <ErrorMessage 
        title={ `Tag not found` } 
        message={ `The tag with name '${tag}' does not exist` }
        dismissLabel="Show main tag"
        onDismiss={ () => props.history.push(`/shop/collections/some-collection/tree/main`) } />
    }

    {
      !_.isEmpty(root_data) && <>
        <h4>Files <span className="mq--sub">({ tag })</span></h4>

        <FileExplorer 
          directory={ data } 
          treeBaseUrl={ treePrefix + path }
          blobBaseUrl={ blobPrefix + path }
          selectedFile={ selectedFile }
          header={
            <>
              <Link to={ _.first(pathElementLinks).to }><b>{ _.first(pathElementLinks).label }</b></Link>
              { _.map(pathElementLinks.slice(1, -1), e => <React.Fragment key={e.to}> / <Link to={ e.to }>{ e.label }</Link></React.Fragment>) }
              { _.size(pathElementLinks) > 1 && <> / { _.last(pathElementLinks).label }</> }
            </>
          } />
      </>
    }
  </Container>;
}

function CollectionOverview(props) {
  const collection = _.get(props, 'match.params.collection');
  const tag = _.get(props, 'match.params.tag') || 'main';
  const url = _.get(props, 'match.url');
  const treePrefix = `/shop/collections/${collection}/tree/${tag}`
  const blobPrefix = `/api/data/collections/${collection}/tags/${tag}`

  if (url.startsWith(treePrefix)) {
    console.log(url);
    let path = url.substring(_.size(treePrefix));
    return <Files path={ path } tag={ tag } treePrefix={ treePrefix } blobPrefix={ blobPrefix } { ...props } />
  } else {
    return <Files path="/" tag={ tag } treePrefix={ treePrefix } blobPrefix={ blobPrefix } { ...props } />
  }
}

CollectionOverview.propTypes = {};

export default CollectionOverview;
