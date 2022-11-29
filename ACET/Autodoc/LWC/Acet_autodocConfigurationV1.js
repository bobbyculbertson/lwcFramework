import {LightningElement, api} from 'lwc';

export default class Acet_autodocConfigurationV1 extends LightningElement{

  autodocSessionId;

  export initAutodocConfig(ico, pageId){
    // invoked from the PageController which passes reference to ICO object
    if(ico.autodocSessionId != ""){
      // generate a random autodoc session ID and assign to ICO.
      // note: autodoc session ID could be the randomly generated page ID.

      this.autodocSessionId = autodocSessionId;
      Object.assign(ico,{
          "autodocSessionId": pageId
      });
    }
  }

  export getAutodocSessionId(){
    return autodocSessionId;
  }
}
