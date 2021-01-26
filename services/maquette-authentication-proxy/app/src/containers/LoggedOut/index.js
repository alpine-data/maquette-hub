import React from 'react';

import { Button, Pane, Text } from 'evergreen-ui'

function LoggedOut() {
    return <Pane width="100%" height="100%" display="flex" justifyContent="center" alignItems="center">
        <Text size={800} textAlign="center">You have been logged out successfully. <br /><Button is="a" href="/" size={ 800 } marginTop={ 20 }>Back to Login</Button></Text>
    </Pane>
}

export default LoggedOut;