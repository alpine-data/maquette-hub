import { createSelector } from 'reselect';

export default function createMakeSelectCreateSource(container) {
    return () => createSelector(
        state => state[container] || {},
        substate => substate);
}
