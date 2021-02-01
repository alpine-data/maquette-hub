import _ from 'lodash';
import { formatDistance } from 'date-fns';
import plural from 'pluralize';

export function pluralize(count, label) {
    return plural(label, count, true);
}

export function timeAgo(jsonDateTimeString) {
    try {
        return formatDistance(new Date(jsonDateTimeString), new Date());
    } catch (e) {
        return '';
    }
}

export function formatTime(jsonDateTimeString) {
    try {
        return new Date(jsonDateTimeString).toLocaleString();
    } catch (e) {
        return '';
    }
}