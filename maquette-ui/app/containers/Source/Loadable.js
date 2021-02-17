/**
 *
 * Asynchronously loads the component for Source
 *
 */

import loadable from 'utils/loadable';

export default loadable(() => import('./index'));
