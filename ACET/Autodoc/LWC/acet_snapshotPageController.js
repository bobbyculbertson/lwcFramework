import initAutodocConfig from '@salesforce/resourceUrl/Acet_autodocConfigurationV1'; <-- add this import

export default class Acet_snapshotPageController extends LightningElement{

  @wire(CurrentPageReference)
  getcpr(currentPageReference){
    /*
    Grab the current page reference and save the parameters off into variables. One initial, and one that can be appended by the system
    This allows us to save the initial parameters for possible future use
    */
    const icoStr = currentPageReference.state.c__params;

    if (icoStr != null) {
      this.apiContext['interactionContext'] = JSON.parse(JSON.stringify(icoStr));
      this.pageId = currentPageReference.state.c__pageId;
      upsertIco(this.pageId, this.apiContext);

      // ADD THIS: call to initialize autodoc configuration...
      initAutodocConfig(this.apiContext['interactionContext'], this.pageId);
    }
  }
}
