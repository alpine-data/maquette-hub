/**
 *
 * CodeExamples
 *
 */

import _ from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';

import { Link } from 'react-router-dom';

import { Light as SyntaxHighlighter } from 'react-syntax-highlighter';
import java from 'react-syntax-highlighter/dist/esm/languages/hljs/java';
import python from 'react-syntax-highlighter/dist/esm/languages/hljs/python';
import docco from 'react-syntax-highlighter/dist/esm/styles/hljs/docco';

SyntaxHighlighter.registerLanguage('java', java);
SyntaxHighlighter.registerLanguage('python', python);

import Tabs from '../Tabs';

function Title({ children }) {
  return <p><b style={{ display: "block", marginTop: "20px" }}>{ children }</b></p>
}

function CodeExamples({ samples, asset = 'some-name' }) {
  const tabs = _.map(samples, language => {
    const snippets = _.map(language.samples, snippet => {
      return <React.Fragment key={ snippet.title }>
        <Title>{ snippet.title }</Title>
        <SyntaxHighlighter showLineNumbers language={ language.language } style={docco}>
          { snippet.code.replace('__ASSET__', asset) }
        </SyntaxHighlighter>
      </React.Fragment>
    });

    return {
      key: language.language,
      label: language.title,
      component: <>
        { snippets }
        {
          language.footnote && <>
            <p style={{ fontSize: "14px" }}>
              { language.footnote }
            </p>
          </>
        }
      </>
    };
  })

  return <>
    <Tabs content={ tabs } />
  </>;
}

CodeExamples.propTypes = {
  samples: PropTypes.arrayOf(PropTypes.shape({
    language: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    footnote: PropTypes.element,
    samples: PropTypes.arrayOf(PropTypes.shape({
      title: PropTypes.string.isRequired,
      code: PropTypes.string.isRequired,
      descriptions: PropTypes.string
    }))
  }))
};

CodeExamples.defaultProps = {
  
}

export default CodeExamples;
