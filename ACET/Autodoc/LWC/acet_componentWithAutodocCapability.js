import {handleTileAutodocCheckbox,autodocTileData,autodocTable} from '@salesforce/resourceUrl/Acet_autodocHelperV1'; <-- add this import

export default class Acet_componentWithAutodocCapability extends LightningElement{
  componentName = "component-name";
  topic = "component-topic";
  isPrimary = false;
  isSecondary = false;
  componentData = {}; // represents the data object uses to render the component UI.

  onloadEvent(){
    // Example of component code which automatically autodocs some data...
    // 1) get your component API data.
    // 2) create component data (binding comp metadata & API)
    // 3) execute custom autodoc (i.e. auto-check some checkboxes)
    componentData["fieldA"].autodoc = true;
    componentData["fieldB"].autodoc = true;
    componentData["fieldC"].autodoc = true;
    autodocTileData(this.componentName, this.topic, correlationId, componentData, false);
  }

  handleTileCheckboxChange(event) {

    let response = handleTileAutodocCheckbox(componentData, event);
    if(!response.updated){
      // add custom checkbox logic for other use cases (e.g. autodoc checkbox that only updates a subset of the componentData values)
    }
    if(response.updated){
      autodocTileData(this.componentName, this.topic, response.correlationId, this.componentData, this.isPrimary)
    }
  }

  handleTableCheckboxChange(event) {

    let response = handleTableAutodocCheckbox(componentData, event);
    if(!response.updated){
      // add custom checkbox logic for other use cases (e.g. autodoc checkbox that only updates a subset of the componentData values)
    }
    if(response.updated){
      for(let row in response.rows){
        autodocTableeData(this.componentName, this.topic, row.correlationId, row, componentData.columns, this.isPrimary, this.isSecondary);
      }
    }
  }
