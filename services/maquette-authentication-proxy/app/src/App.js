import React from 'react';
import LoggedOut from './containers/LoggedOut';
import Login from './containers/Login';

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';

function App() {
  return <Router>
    <Switch>
      <Route exact path='/byebye' component={ LoggedOut } />
      <Route path='/' component={ Login } />
    </Switch>
  </Router>;
}

export default App;
