/**
 *
 * NewDataAsset
 *
 */

import React from 'react';
import { ButtonToolbar, FlexboxGrid, Icon, IconButton, Link } from 'rsuite';
import styled from 'styled-components';

const DataAssetGrid = styled(FlexboxGrid)`
  margin-bottom: 20px;
`;

export const icons = {
  collection: 'retention',
  dataset: 'table',
  stream: 'realtime',
  source: 'database',
  repository: 'code-fork'
}

function NewDataAsset() {
  return <>
    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['dataset'] } size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Dataset</b>
        <p>
          A dataset contains structured data, like table. A dataset may contain multiple, immutable versions of its data.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to={ `/new/dataset` }>Create a dataset</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['stream'] } size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Streams</b>
        <p>
          A stream publishes data as a stream of events/ updates. Existing data is retained for a specified amount of time. A consumer can `listen` to the data.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to={ `/new/stream` }>Create a stream</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['source'] } size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Data Sources</b>
        <p>
          A data source helps to offer access productive data stores. A consumer can access transactional data stores with near-realtime up-to-date data.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to={ `/new/source` }>Create a data source</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['collection'] } size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Collections</b>
        <p>
          A collection stores versioned file sets. Each set contains a immutable set of files for further processing.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to={ `/new/collection` }>Create a collection</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>

    <DataAssetGrid align="middle">
      <FlexboxGrid.Item colspan={ 3 }>
        <Icon icon={ icons['repository'] } size="2x" className="mq--icon-cycle" />
      </FlexboxGrid.Item>
      <FlexboxGrid.Item colspan={ 21 }>
        <b>Data Repository</b>
        <p>
          A data repository can be used by data scientists to store intermediate data objects, e.g. feature-engineered matrices. A data repository cannot be shared across Maquette projects.
        </p>
        <ButtonToolbar>
          <IconButton 
            color="blue"
            placement="right"
            icon={ <Icon icon="arrow-circle-right" /> }
            componentClass={ Link }
            to={ `/new/repository` }>Create a data repository</IconButton>
        </ButtonToolbar>
      </FlexboxGrid.Item>
    </DataAssetGrid>
  </>
}

NewDataAsset.propTypes = {};

export default NewDataAsset;
