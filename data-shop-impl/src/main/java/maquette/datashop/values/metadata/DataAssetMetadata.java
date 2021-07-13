package maquette.datashop.values.metadata;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

@With
@Value
@AllArgsConstructor(staticName = "apply")
public class DataAssetMetadata {

   /**
    * A speaking title for the data asset.
    */
   String title;

   /**
    * A technical name for the data asset.
    */
   String name;

   /**
    * A short description of the data asset.
    */
   String summary;

   /**
    * The visibility of the asset.
    */
   DataVisibility visibility;

   /**
    * Classification if the asset.
    */
   DataClassification classification;

   /**
    * Indicator whether personal information is included in the data.
    */
   PersonalInformation personalInformation;

   /**
    * The zone gives an indication how well prepared/ clean the data is.
    */
   DataZone zone;

}
