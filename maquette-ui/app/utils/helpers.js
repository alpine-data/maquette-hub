import _ from 'lodash';
import { formatDistance } from 'date-fns';
import plural from 'pluralize';

export function pluralize(count, label) {
    return plural(label, count, true);
}

export function pluralizeWord(word) {
    if (_.isUndefined(word)) { return '' }
    return plural(word);
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
        return new Date(jsonDateTimeString).toLocaleString(
            undefined, { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', secon: '2-digit' });
    } catch (e) {
        return '';
    }
}

export function formatDate(jsonDateTimeString) {
    try {
        return new Date(jsonDateTimeString).toLocaleDateString(
            undefined, { year: 'numeric', month: '2-digit', day: '2-digit' });
    } catch (e) {
        return '';
    }
}