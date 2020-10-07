/**
 *
 * Asynchronously loads the component for DataShop
 *
 */

import loadable from 'utils/loadable';

export default loadable(() => import('./index'));
