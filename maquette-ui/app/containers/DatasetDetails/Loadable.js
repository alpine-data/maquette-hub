/**
 *
 * Asynchronously loads the component for Dataset
 *
 */

import loadable from 'utils/loadable';

export default loadable(() => import('./index'));
