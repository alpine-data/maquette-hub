INSERT INTO maquette.mq__data_access_requests (
    id,
    asset,
    workspace,
    request)
VALUES(
    :id,
    :asset,
    :workspace,
    :request)
ON CONFLICT
DO UPDATE SET request = request;