import _ from 'lodash';

/**
 * Parses the JSON returned by a network request
 *
 * @param  {object} response A response from a network request
 *
 * @return {object}          The parsed JSON from the request
 */
function parseJSON(response) {
  if (response.status === 204 || response.status === 205) {
    return {};
  } 

  return response.json();
}

/**
 * Checks if a network request came back fine, and throws an error if not
 *
 * @param  {object} response   A response from a network request
 *
 * @return {object|undefined} Returns either the response, or throws an error
 */
async function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }

  const error = new Error(response.statusText);
  
  try {
    const json = await response.json();
    error.response = json;
  } catch (e) {
    error.response = response;
  }

  throw error;
}

/**
 * Execute a Maquette Command.
 * 
 * @param {string} command 
 * @param {object} parameters 
 * @param {object} user 
 */
export function command(command, parameters, user) {
  return request('/api/commands', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'x-user-id': user.id,
      'x-user-roles': user.roles
    },
    body: JSON.stringify(_.assign({}, {
      "command": command
    }, parameters))
  })
}

/**
 * Requests a URL, returning a promise
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 *
 * @return {object}           The response data
 */
export default function request(url, options) {
  return fetch(url, options)
    .then(checkStatus)
    .then(parseJSON);
}
  