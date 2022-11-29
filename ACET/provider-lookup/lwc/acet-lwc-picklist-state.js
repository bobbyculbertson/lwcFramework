
export default class Acet_lwc_picklist_state extends LightningElement{
  @api fieldData;
  states = [];
  selectedState = "";

  connectedCallback() {
    buildStateObject();
  }

  buildStateObject(){
    this.states.push({"lable": "Alabama", "value":"AL"});
    this.states.push({"lable": "Alaska", "value":"AK"});
    this.states.push({"lable": "Arizona", "value":"AZ"});
    ...
  }

  handleStatePicklistChange(event){
    this.selectedState = event.target.value;
    const statePickEvent = new CustomEvent("stateSelected", {
      fieldName = fieldData.name,
      selectedState = this.selectedState
    });
    this.dispatchEvent(statePickEvent);
  }
}
