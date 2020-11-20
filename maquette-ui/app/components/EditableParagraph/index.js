/**
 *
 * EditableParagraph
 *
 */

import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';

import cx from 'classnames';
import { Input } from 'rsuite';

function EditableParagraph({ className, disabled, placeholder, label, value, onChange, ...props }) {
  const [editing, setEditing] = useState(false);
  const [inputValue, setInputValue] = useState(value || "");

  const edit_onClick = (event) => {
    setEditing(true);
    event.preventDefault();
    return false;
  }

  const input_onBlur = () => {
    onChange(inputValue);
    setEditing(false);
  }

  if (editing) {
    return <Input 
      className={ cx('mq--editable-paragraph--input', className) }
      value={ inputValue } 
      onChange={ (v) => setInputValue(v) } 
      onPressEnter={ input_onBlur } 
      onBlur={ input_onBlur } />;
  } else {
    return <p className={ cx('mq--editable-paragraph', className) }>
      { value || placeholder }{ !disabled && <span className="mq--editable-paragraph--edit"> – <a href="#" onClick={ edit_onClick }>{ label }</a></span> }
    </p>;
  }
}

EditableParagraph.propTypes = {
  className: PropTypes.string,
  disabled: PropTypes.bool,
  placeholder: PropTypes.string,
  value: PropTypes.string,
  label: PropTypes.string,
  onChange: PropTypes.func
};

EditableParagraph.defaultProps = {
  disabled: false,
  placeholder: "No content",
  label: 'Edit content',
  onChange: console.log
}

export default EditableParagraph;
