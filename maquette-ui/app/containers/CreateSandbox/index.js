/**
 *
 * CreateSandbox
 *
 */
import _ from 'lodash';
import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectCreateSandbox from './selectors';
import reducer from './reducer';
import saga from './saga';
import { init, submit } from './actions';
import { Message } from 'rsuite';

import Container from '../../components/Container';
import CreateSandboxForm from '../../components/CreateSandboxForm';

import ViewContainer from '../../components/ViewContainer';
import Loader from '../../components/Loader';

export function CreateSandbox(props) {
  useInjectReducer({ key: 'createSandbox', reducer });
  useInjectSaga({ key: 'createSandbox', saga });

  const [initialized, setInitialized] = useState(false);
  const createSandbox = _.get(props, 'createSandbox');

  const stacks = _.get(props, 'createSandbox.data.stacks');
  const project = _.get(props, 'createSandbox.data.project');

  const query = new URLSearchParams(_.get(props, 'location.search') || '');
  const showForm = !_.isUndefined(_.get(props, 'createSandbox.data.project'));

  useEffect(() => {
    if (!initialized) {
      props.dispatch(init(query.get("project")));
      setInitialized(true);
    }
  }, [query.get("project")]);

  return <ViewContainer
    background="sandboxes"
    titles={ [ { label: 'Create a new sandbox' } ] }
    loading={ _.get(props, 'createSandbox.loading') || !initialized }>

      <Container lg>
        <p className="mq--p-leading">
          A sandbox is an isolated infrastructure environment which may contain multiple stacks of technologies to work with data.
        </p>

        <hr /> 

        { 
          createSandbox.error && <>
            <Message type="error" title="Sorry, something went wrong" description={ createSandbox.error } style={{ marginBottom: '20px' }} />
          </>
        }

        {  
          showForm && <>
            <CreateSandboxForm 
              { ...createSandbox.data }
              onSubmit={ request => props.dispatch(submit(request)) } />
          </> || <>
            <Loader />
          </>
        }
      </Container>
    </ViewContainer>
}

CreateSandbox.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  createSandbox: makeSelectCreateSandbox(),
});

function mapDispatchToProps(dispatch) {
  return {
    dispatch,
  };
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);

export default compose(withConnect)(CreateSandbox);
