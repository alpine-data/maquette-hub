/**
 *
 * DatasetCodeExamples
 *
 */

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

function DatasetCodeExamples({ project, dataset, version, canProduce = true, canConsume = true }) {

  const pythonConsumer = <>
    <Title>Publish Data</Title>
    <SyntaxHighlighter showLineNumbers language="python" style={docco}>
      {
        `import mq\n\n` +
        `df = pandas.DataFrame(...)\n` +
        `mq.project('${project}').datasets('${dataset}').put(df)`
      }
    </SyntaxHighlighter>
  </>

  const pythonProducer = <>
    <Title>Consume Data</Title>
    <SyntaxHighlighter showLineNumbers language="python" style={docco}>
      {
        `import mq\n\n` +
        `df = mq.project('${project}').datasets('${dataset}').get('${version}')`
      }
    </SyntaxHighlighter>
  </>

  const javaProducer = <>
    <Title>Java Producer</Title>
    <SyntaxHighlighter showLineNumbers language="java" style={docco}>
       {
         `import maquette.sdk.dsl.Maquette;\n\n` +
         `var data = List.of(/* ... */);\n\n` + 
         `Maquette\n   .project("${project}")\n   .datasets("${dataset}")\n   .put("${version}", data);`
       }
     </SyntaxHighlighter>
  </>

  const javaConsumer = <>
    <Title>Consume Data</Title>
    <SyntaxHighlighter showLineNumbers language="java" style={docco}>
       {
         `import maquette.sdk.dsl.Maquette;\n\n` +
         `var data = Maquette\n   .project("${project}")\n   .datasets("${dataset}")\n   .get("${version}");`
       }
     </SyntaxHighlighter>
  </>

  return <>
    { canProduce && canConsume && <h4>Produce &amp; Consume Data</h4> }
    { canProduce && !canConsume && <h4>Produce Data</h4> }
    { !canProduce && canConsume && <h4>Consume Data</h4> }
    <Tabs content={ [
      {
        key: "python",
        label: "Python SDK",
        component: <>
          { canConsume && pythonConsumer }
          { canProduce && pythonProducer }
          <p style={{ fontSize: "14px" }}>
            See more details about the Maquette Python SDK <Link to="/python">here</Link>.
          </p>
        </>
      },
      {
      key: "java",
      label: "Java SDK",
      component: <>
        { canConsume && javaConsumer }
        { canProduce && javaProducer }

        <p style={{ fontSize: "14px" }}>
          See more details about the Maquette Java SDK <Link to="/java">here</Link>.
        </p>
      </>
      } ]
    } />
  </>;
}

DatasetCodeExamples.propTypes = {
  project: PropTypes.string.isRequired,
  dataset: PropTypes.string.isRequired,
  version: PropTypes.string.isRequired,
  canProduce: PropTypes.bool,
  canConsume: PropTypes.bool
};

export default DatasetCodeExamples;
