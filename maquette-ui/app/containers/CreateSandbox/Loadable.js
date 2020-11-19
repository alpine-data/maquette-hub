/**
 *
 * Asynchronously loads the component for CreateSandbox
 *
 */

import loadable from 'utils/loadable';

export default loadable(() => import('./index'));
