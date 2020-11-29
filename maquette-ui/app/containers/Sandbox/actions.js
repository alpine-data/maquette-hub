/*
 *
 * Sandbox actions
 *
 */
 
import { 
  LOAD,
  FAILED,
  FETCHED } from './constants';

export function load(project, sandbox, clear = false) {
  return {
    type: LOAD,
    project,
    sandbox,
    clear
  };
};

export function failed(error) {
  return {
    type: FAILED,
    error
  };
};

export function fetched(response) {
  return {
    type: FETCHED,
    response
  };
};
