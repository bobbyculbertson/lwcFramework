import processCaseMgmtRequest from '@salesforce/apex/ACET_CaseManagementController.processRequest';
import {getInteractionContext} from 'c/acet_interactionDataController';
import {getAutodoc} from 'c/acet_autodocController'; <-- [requires autodoc feature development]

@api
async saveCase(){
  let ico = getInteractionContext(this.pageId);
  let cmRequest = {};
  cmRequest.requestType = "saveCase";
  cmRequest.requestData = {};

  Object.assign(ico,{
      "CaseMgmtRequest": cmRequest
  });
  ico = setCommentsToIco(ico);
  submitCaseMgmtRequest(ico);
}

async routeCase(topic){
  let ico = getInteractionContext(this.pageId);
  let cmRequest = {};
  cmRequest.requestType = "route";
  cmRequest.requestData = {};
  cmRequest.requestData.topic = topic;

  Object.assign(ico,{
      "CaseMgmtRequest": cmRequest
  });
  ico = setCommentsToIco(ico);
  submitCaseMgmtRequest(ico);
}

submitCaseMgmtRequest(ico){
  let autodoc = getAutodoc();
  await processCaseMgmtRequest({
    ico: ico,
    autodoc: autodoc
  }).then(result => {
    //handle CaseMgmtResponse object
    // 1) Display modal with success or failure
    //  a) Display Case Reference Number (if possible)
    // 2) Update InteractionContextObject with created case ID.
    // 3) [FUTURE] update autodoc with saves case & routed item status.
  });
}

setCommentsToIco(ico){
  // get comments and set value in ico;
  let comments = getComments();
  Object.assign(ico,{
      "caseComments": comments
  });
  return ico;
}
