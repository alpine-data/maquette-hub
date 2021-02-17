/**
 *
 * DataAssetPropertiesForm
 *
 */

import React from 'react';
import PropTypes from 'prop-types';
import { ControlLabel, FlexboxGrid, FormControl, FormGroup, HelpBlock, RadioGroup, Radio } from 'rsuite';

export const initialState = {
  title: '',
  name: '',
  summary: '',
  visibility: 'public',
  classification: 'public',
  personalInformation: 'none',
  zone: 'raw'
}

export const validate = (state) => {
  return _.size(state.title) > 3 && _.size(state.name) > 3;
}

function DataAssetPropertiesForm({ assetType, state, onChangeValues }) {
  const onChange$internal = (field) => value => {
    const values = { [field]: value }

    if (field === 'personalInformation') {
      if (value === 'pi' && (state.classification === 'public' || state.classification === 'internal')) {
        values['classification'] = 'confidential';
      } else if (value === 'spi') {
        values['classification'] = 'restricted';
      }
    }

    if (field === 'title') {
      values['name'] = _.kebabCase(_.lowerCase(value));
    }

    onChangeValues(values);
  }

  return <>
    <FlexboxGrid justify="space-between">
      <FlexboxGrid.Item colspan={ 11 }>
        <FormGroup>
          <ControlLabel>{ _.capitalize(assetType) } Title</ControlLabel>
          <FormControl name="title" onChange={ onChange$internal('title') } value={ state.title } />
          <HelpBlock>Select a speaking, memorable title for the { assetType }.</HelpBlock>
        </FormGroup>
      </FlexboxGrid.Item>

      <FlexboxGrid.Item colspan={ 11 }>
          <FormGroup>
            <ControlLabel>{ _.capitalize(assetType) } Name</ControlLabel>
            <FormControl name="name" value={ state.name } onChange={ onChange$internal('name') } />
            <HelpBlock>The name should only contain small letters (a-z), numbers (0-9) and dashes (-).</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>{ _.capitalize(assetType) } Summary</ControlLabel>
            <FormControl name="summary" onChange={ onChange$internal('summary') } value={ state.summary } />
            <HelpBlock>Describe in a few words which data is contained in your dataset.</HelpBlock>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Visibility</ControlLabel>
            <HelpBlock>The dataset metadata might be visible to allow other users to find the dataset. If the dataset is visible, the data is still not accessible for everyone.</HelpBlock>
            <RadioGroup name="visibility" value={ state.visibility } onChange={ onChange$internal('visibility') } style={{ paddingTop: "10px" }}>
              <Radio value="public"><b>Public</b> <br />The dataset and its metadata can be seen by everyone. The data is not public.</Radio>
              <Radio value="private"><b>Private</b><br />The dataset is entirely hidden from other users.</Radio>
            </RadioGroup>
          </FormGroup>

          <FormGroup>
            <ControlLabel>Data Classification</ControlLabel>
            <HelpBlock>Classify the contained data.</HelpBlock>

            <RadioGroup name="classification" value={ state.classification } onChange={ onChange$internal('classification') } style={{ paddingTop: "10px" }}>
              <Radio value="public" disabled={ state.personalInformation != "none" }><b>Public</b> <br />The data is public information, it can be used by everyone, everywere for any purpose.</Radio>
              <Radio value="internal" disabled={ state.personalInformation != "none" }><b>Internal</b><br />The dataset may contain company internal information. If disclosed, the impact to business is minimal.</Radio>
              <Radio value="confidential" disabled={ state.personalInformation == "spi" }><b>Confidential</b><br />The dataset may contain business critical internal information. If disclosed, business or brand could be negatively effected.</Radio>
              <Radio value="restricted"><b>Restricted</b><br />The dataset may contain highly sensitive information. If disclosed, it will have significant financial or legal impact.</Radio>
            </RadioGroup>
          </FormGroup>

          <FormGroup>
            <ControlLabel>Personal Information</ControlLabel>
            <HelpBlock>Specify whether the data may contain data points which could identify a natural person.</HelpBlock>

            <RadioGroup name="personalInformation" value={ state.personalInformation } onChange={ onChange$internal('personalInformation') } style={{ paddingTop: "10px" }}>
              <Radio value="none"><b>None</b> <br />The data definetely does not contain any personal information.</Radio>
              <Radio value="pi"><b>Personal Information</b><br />The dataset may contain personal information.</Radio>
              <Radio value="spi"><b>Sensitive Personal Information</b><br />The dataset may contain sensitive personal information according to GDPR.</Radio>
            </RadioGroup>
          </FormGroup>
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <hr />
        </FlexboxGrid.Item>

        <FlexboxGrid.Item colspan={ 24 }>
          <FormGroup>
            <ControlLabel>Staging Zone</ControlLabel>
            <HelpBlock>The data staging zone indicates which quality the data of the { assetType } has.</HelpBlock>
            <RadioGroup name="zone" value={ state.zone } onChange={ onChange$internal('zone') } style={{ paddingTop: "10px" }}>
              <Radio value="raw"><b>Raw</b> <br />The data as it is stored in the source system or received from an external provider.</Radio>
              <Radio value="prepared"><b>Prepared</b><br />The data asset has been cleansed and prepared for a specific use case.</Radio>
              <Radio value="gold"><b>Gold</b><br />The data has the highest possible quality and fullfills all regulatory requirements towards linage and provenance.</Radio>
            </RadioGroup>
          </FormGroup>
        </FlexboxGrid.Item>
    </FlexboxGrid>
  </>;
}

DataAssetPropertiesForm.propTypes = {
  assetType: PropTypes.string.isRequired,
  state: PropTypes.object.isRequired, 
  onChange: PropTypes.func.isRequired
};

DataAssetPropertiesForm.defaultProps = {
  state: {},
  onChange: console.log
}

export default DataAssetPropertiesForm;
