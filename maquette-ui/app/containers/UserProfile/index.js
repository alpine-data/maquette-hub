/**
 *
 * UserProfile
 *
 */

import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { createStructuredSelector } from 'reselect';
import { compose } from 'redux';

import { useInjectSaga } from 'utils/injectSaga';
import { useInjectReducer } from 'utils/injectReducer';
import makeSelectUserProfile from './selectors';

import reducer from './reducer';
import saga from './saga';
import { load } from './actions';

import ViewContainer from '../../components/ViewContainer';

import BioSamples from './bio_samples.json';
import UserProfileOverview from '../../components/UserProfileOverview';


export function UserProfile(props) {
  useInjectReducer({ key: 'userProfile', reducer });
  useInjectSaga({ key: 'userProfile', saga });

  const [initialized, setInitialized] = useState(false);
  const data = _.get(props, 'userProfile.data');
  const userId = _.get(props, 'match.params.id');

  useEffect(() => {
    if (!initialized) {
      props.dispatch(load(userId));
      setInitialized(true);
    }
  }, []);

  return <ViewContainer 
    titles={ [ { label: _.get(data, 'profile.name') && `${_.get(data, 'profile.name')} (${userId})` || userId } ] }
    loading={ _.get(props, 'userProfile.loading') }
    
    summary={ _.get(data, 'profile.bio') || _.sample(BioSamples) }
    changeSummaryLabel='Edit my bio'
    canChangeSummary={ _.get(data, 'isOwnProfile') }
    content={ () => <UserProfileOverview view={ data } /> }>
      
  </ViewContainer>;
}

UserProfile.propTypes = {
  dispatch: PropTypes.func.isRequired,
};

const mapStateToProps = createStructuredSelector({
  userProfile: makeSelectUserProfile(),
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

export default compose(withConnect)(UserProfile);
