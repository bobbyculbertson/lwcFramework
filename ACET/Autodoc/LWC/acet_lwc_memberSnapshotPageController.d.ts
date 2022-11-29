declare module "@salesforce/apex/ACET_LWC_memberSnapshotPageController.fetchEligibilityData" {
  export default function fetchEligibilityData(param: {ico: any}): Promise<any>;
}
 NOTE: this definition needs to change and only require an Interaction Context Object.
 The ICO should be the standard data object for tracking/passing data within the application.
 Autodoc is the other main data object. See the autodoc schema definition for details.
