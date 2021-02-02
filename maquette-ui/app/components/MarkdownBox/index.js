/**
 *
 * MarkdownBox
 *
 */

import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

import AceEditor from "react-ace";
import ReactMarkdown from 'react-markdown'

import "ace-builds/src-noconflict/mode-markdown";
import "ace-builds/src-noconflict/theme-github";

import { Button, ButtonToolbar, FlexboxGrid, Icon } from 'rsuite';
import { edit } from 'ace-builds';

const Box = styled.div`
  border: 1px solid #ccc;
  background-color: #fff;
`;

const TitleWrapper = styled(FlexboxGrid)`
  font-size: 16px;
  font-weight: bold;
  padding: 20px;

  background-color: #eee;
`;

const ContentWrapper = styled.div`
  margin: 20px;
`

const markdown = `
# Hello, *world*

## Foo
Test foo bar [Some Link](http://foo.bar)

---
### Bar

> Hello World
`;

function MarkdownBox({ title, value, onUpdate }) {
  const [ editing, setEditing ] = useState(false);
  const [ edited, setEdited ] = useState(value);

  return <Box>
    <TitleWrapper align="middle">
      <FlexboxGrid.Item colspan={ 12 }>
        <Icon icon='book' /> { title }
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 12 } align="right">
        {
          editing && <>
            <ButtonToolbar>
              <Button 
                size="sm" 
                color="orange"
                onClick={ () => {
                  setEditing(false);
                  setEdited(value);                   
                }}>
                  
                  Discard changes
              </Button>

              <Button 
                size="sm" 
                appearance="primary" 
                onClick={ () => {
                  onUpdate(edited);
                  setEditing(false);
                } }>
                  
                  Save changes
              </Button>
            </ButtonToolbar>
          </> || <>
            <Button size="sm" appearance="primary" onClick={ () => setEditing(true) }>Edit</Button>
          </>
        }
      </FlexboxGrid.Item>
    </TitleWrapper>

    {
      editing && <>
        <AceEditor
          mode="markdown"
          theme="github"
          value={ edited }
          onChange={ value => setEdited(value) }
          name="schema"
          editorProps={{ $blockScrolling: false }}
          height="500px"
          width="100%" />
      </> || <>
        <ContentWrapper>
          <ReactMarkdown>
            { value }
          </ReactMarkdown>
        </ContentWrapper>
      </>
    }
  </Box>;
}

MarkdownBox.propTypes = {
  title: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  onUpdate: PropTypes.func.isRequired
};

MarkdownBox.defaultProps = {
  title: 'README.md',
  value: markdown,
  onUpdate: console.log
}

export default MarkdownBox;
