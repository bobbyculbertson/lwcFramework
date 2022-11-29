
export default class Acet_interactionContextObject extends LightningElement{

  ico;

  @wire(CurrentPageReference)
  getcpr(currentPageReference){
    /*
    Grab the current page reference and save the parameters off into variables. One initial, and one that can be appended by the system
    This allows us to save the initial parameters for possible future use
    */
    const icoparams = JSON.parse(currentPageReference.state.c__params);
    ico = createIcoInstance(icoparams);
  }

  createIcoInstance(icoparams){
    // builds ICO object from page reference param...
    let obj = {};
    obj.interactionID = icoparams.interactionId;
    obj.interactionName = icoparams.interactionName;
    obj.subjectAccountRecordId = icoparams.subjectAccountRecordId;
    obj.autodocSessionId = (icoparams.subjectAccountRecordId)? icoparams.subjectAccountRecordId : "";
    obj.productType = icoparams.productType;
    obj.searchType = icoparams.searchType;
    obj.subjectType = (icoparams.subjectType)? icoparams.subjectType : "";
    obj.originatorType = (icoparams.originatorType)? icoparams.originatorType : "";
    obj.subjectData = (icoparams.subjectData)? icoparams.subjectData : null;
    obj.originatorData = (icoparams.originatorData)? icoparams.originatorData : null;
    obj.subjectType = "";
  }
}
