import _ from 'lodash';
import React from 'react';
import { Button, Pane, TextInputField } from 'evergreen-ui'
import Logo from './logo.svg';

function Login(props) {
    const query = new URLSearchParams(_.get(props, 'location.search') || '');

    return <Pane width="100%" height="100%" display="flex" justifyContent="center" alignItems="center">
        <Pane width={ 520 } elevation={ 2 } padding={ 20 }>
            <Pane marginBottom={ 20 }>
                <img src={ Logo } width={240} alt="Company Logo" />
            </Pane>

            <form method="post" action="/login">
                <TextInputField label="Username" name="username" hint="Use one of the dummy users `alice`, `bob` or `clair`." />
                <TextInputField label="Password" type="password" name="password" />
                <input type="hidden" value={ query.get('redirect') || '/' } name="redirect" />

                <Button type="submit">Login</Button>
            </form>
        </Pane>
    </Pane>
}

export default Login;