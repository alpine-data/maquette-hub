DELETE FROM maquette.mq__members
WHERE type = :type AND parent = :parent AND auth := auth;