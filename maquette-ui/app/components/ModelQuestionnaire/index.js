/**
 *
 * ModelQuestionnaire
 *
 */

import React from 'react';
// import PropTypes from 'prop-types';
// import styled from 'styled-components';

import { Survey } from 'survey-react';

const surveyJSON = { title: "Tell us, what technologies do you use?", pages: [
  { name:"page1", questions: [ 
      { type: "radiogroup", choices: [ "Yes", "No" ], isRequired: true, name: "frameworkUsing",title: "Do you use any front-end framework like Bootstrap?" },
      { type: "checkbox", choices: ["Bootstrap","Foundation"], hasOther: true, isRequired: true, name: "framework", title: "What front-end framework do you use?", visibleIf: "{frameworkUsing} = 'Yes'" }
   ]},
  { name: "page2", questions: [
    { type: "radiogroup", choices: ["Yes","No"],isRequired: true, name: "mvvmUsing", title: "Do you use any MVVM framework?" },
    { type: "checkbox", choices: [ "AngularJS", "KnockoutJS", "React" ], hasOther: true, isRequired: true, name: "mvvm", title: "What MVVM framework do you use?", visibleIf: "{mvvmUsing} = 'Yes'" } ] },
  { name: "page3",questions: [
    { type: "comment", name: "about", title: "Please tell us about your main requirements for Survey library" } ] }
 ]
}

const existingValue = {
  "frameworkUsing": "Yes",
  "framework": [
    "Foundation"
  ],
  "mvvmUsing": "No",
  "about": "asdasd"
}

function ModelQuestionnaire() {
  return <>
    <Survey data={ existingValue } json={ surveyJSON } onComplete={ (sender, options) => {
      console.log(sender.data);
    } } />
  </>;
}

ModelQuestionnaire.propTypes = {};

export default ModelQuestionnaire;
