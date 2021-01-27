/**
 *
 * StreamCodeExamples
 *
 */

import React from 'react';

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

function StreamCodeExamples({ stream = 'stock-price' }) {
  return <>
    <h4>Consume and Produce Data</h4>
    <Tabs content={ [
        {
          key: "java",
          label: "Java SDK",
          component: <>
            <Title>Java Producer</Title>
            <SyntaxHighlighter showLineNumbers language="java" style={docco}>
              {
                `import maquette.sdk.dsl.Maquette;\n\n` +
                `var data = List.of(/* ... */);\n\n` + 
                `Maquette\n   .streams("${stream}")\n   .push(data);\n\n` + 
                `// or with Reactive Streams\n` +
                `Maquette.streams("${stream}").createSink();`
              }
            </SyntaxHighlighter>
            <Title>Java Consumer</Title>
            <SyntaxHighlighter showLineNumbers language="java" style={docco}>
              {
                `import maquette.sdk.dsl.Maquette;\n\n` +
                `var nextRecord = Maquette\n   .streams("${stream}")\n   .get(SomeClass.class);\n\n` + 
                `// or with Reactive Streams\n` +
                `Maquette.streams("${stream}").createSource(SomeClass.class);`
              }
            </SyntaxHighlighter>
            <p style={{ fontSize: "14px" }}>
              See more details about the Maquette Java SDK <Link to="/java">here</Link>.
            </p>
          </>
        }
    ] } />
  </>;
}

StreamCodeExamples.propTypes = {};

export default StreamCodeExamples;
