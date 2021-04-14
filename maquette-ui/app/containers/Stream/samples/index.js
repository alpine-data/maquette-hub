import React from 'react';

import javaConsume from './consume.java';
import javaProduce from './produce.java';

import pythonConsume from './consume.py';
import pythonProduce from './produce.py';
import { Link } from 'react-router-dom';

export default [
    {
        language: 'python',
        title: 'Python SDK',
        footnote: <>Find more details about Maquette Python SDK <Link to="/">here</Link>.</>,
        samples: [
            {
                title: 'Publish data',
                code: pythonProduce,
            },
            {
                title: 'Consume data',
                code: pythonConsume
            }
        ]
    },
    {
        language: 'java',
        title: 'Java SDK',
        footnote: <>Find more details about Maquette Java SDK <Link to="/">here</Link>.</>,
        samples: [
            {
                title: 'Publish data',
                code: javaProduce,
            },
            {
                title: 'Consume data',
                code: javaConsume
            }
        ]
    }
];