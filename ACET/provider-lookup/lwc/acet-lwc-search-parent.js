
export default class Acet_lwc_searchParent extends LightningElement{
  formData = {};
  componentData = {};

  handleStatePicklistChange(msg){
    // formData represents the list of form fields
    this.formData[msg.fieldName] = msg.selectedState.
  }

  // get component metadata and set as 'componentData' to render the UI...
}
