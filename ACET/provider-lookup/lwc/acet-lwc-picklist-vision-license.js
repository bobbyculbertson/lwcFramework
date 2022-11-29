
export default class Acet_lwc_picklist_state extends LightningElement{
  @api fieldData;
  licenseTypes = [];
  selectedLicense = "";

  connectedCallback() {
    buildLicenseObject();
  }

  buildLicenseObject(){
    // create following list:

  }

  handleLicensePicklistChange(event){
    this.selectedLicense = event.target.value;
    const visionLicensePickEvent = new CustomEvent("visionLicenseSelected", {
      fieldName = fieldData.name,
      selectedLicense = this.selectedLicense
    });
    this.dispatchEvent(visionLicensePickEvent);
  }
}
