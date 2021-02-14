import { useState } from 'react';
import produce from 'immer';

export const useFormState = (iniitialState) => {
    const [state, setState] = useState(iniitialState);

    const onChange = (field) => value => {
        setState(produce(state, draft => {
            draft[field] = value;
        }));
    }

    const onChangeValues = (values) => {
        setState(produce(state, draft => {
            _.forEach(values, (value, key) => {
            draft[key] = value;
            })
        }));
    }

    return [state, setState, onChange, onChangeValues];
}