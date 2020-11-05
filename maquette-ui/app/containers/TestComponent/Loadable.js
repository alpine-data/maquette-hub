/**
 *
 * Asynchronously loads the component for TestComponent
 *
 */

import loadable from 'utils/loadable';

export default loadable(() => import('./index'));
