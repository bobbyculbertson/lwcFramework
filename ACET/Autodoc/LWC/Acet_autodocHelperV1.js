import {LightningElement, api} from 'lwc';
import {getAutodocSessionId} from '@salesforce/resourceUrl/Acet_autodocConfigurationV1';

export default class Acet_autodocHelperV1 extends LightningElement{

  export handleTileAutodocCheckbox(tileComponentData, event){
    let response = {};
    response.updated = false;
    response.correlationId = "";

    let dataElement = event.currentTarget.getAttribute("data-element");
    const checked = event.currentTarget.getAttribute("checked");

    if(dataElement == "select-all-checkbox"){
      // handle 'select all' checkbox event for all tile card elements...
      for(let element in tileComponentData){
        element.autodoc = checked;
        if(element.name = "correlationId"){
          response.correlationId = element.value;
        }
      }
      updated = true;
    }
    else if(dataElement == "field-checkbox"){
      // handle individual checkbox event
      let dataName = event.currentTarget.getAttribute("data-name");
      for(let element in tileComponentData){
        // loop through component data for matching name and set autodoc to checkbox value...
        if(dataName == element.name){
          element.autodoc = checked;
          updated = true;
        }
        if(element.name = "correlationId"){
          response.correlationId = element.value;
        }
      }
    }

    return response;
  }

  export handleTableAutodocCheckbox(tableComponentData, event){
    let response = {};
    response.updated = false;
    response.rows = [];

    let dataElement = event.currentTarget.getAttribute("data-element");
    const checked = event.currentTarget.getAttribute("checked");

    if(dataElement == "select-all-checkbox"){
      // handle 'select all' checkbox event for all table rows...
      for(let element in tableComponentData){
        // loop through all row objects
        element.data.autodoc = checked;
        response.rows.push(element);  // add table row object reference to updated rows...
      }
      updated = true;
    }
    else if(dataElement == "row-checkbox"){
      // handle individual checkbox event
      let correlationId = event.currentTarget.getAttribute("data-name");
      for(let element in tableComponentData){
        // loop through component data for matching name and set autodoc to checkbox value...
        if(correlationId == element.corrKey){
          element.data.autodoc = checked;
          response.rows.push(element);  // add table row object reference to updated rows...
          updated = true;
          break;
        }
      }
    }

    return response;
  }

  export autodocTileData(componentName, topic, correlationId, data, isPrimary){
    const sessionId = getAutodocSessionId();
    const args = window.acetLwcAutodoc.createSaveTileArgs(sessionId, topic, componentName, correlationId, data);
    window.acetLwcAutodoc.saveAutodoc(args);
  }

  export autodocTableRow(componentName, topic, correlationId, primaryCorrelationId, data, columns, isPrimary, isSecondary){
    const sessionId = getAutodocSessionId();
    const args = window.acetLwcAutodoc.createSaveTileArgs(sessionId, topic, componentName, correlationId, primaryCorrelationId, data, isPrimary, isSecondary, columns);
    window.acetLwcAutodoc.saveAutodoc(args);
  }

  export addAutodocSessionData(sessionId, key, value){
    // insert autodoc data which is a sibling object to the 'topics' array...
    // e.g. case/interaction, subject, originator, etc.
    return upsertSessionData(sessionId);
  }

  export getAutodocSerialized(){
    const sessionId = getAutodocSessionId();
    return window.acetLwcAutodoc.getSessionJSONMap(sessionId, true);
  }

  export clearAutodocSessionData(){
    const sessionId = getAutodocSessionId();
    return clearSessionData(sessionId);
  }

}
