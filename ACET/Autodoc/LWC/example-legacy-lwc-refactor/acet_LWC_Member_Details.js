// ADD following import...
import {autodocTileData,autodocTableRow} from '@salesforce/resourceUrl/Acet_autodocHelperV1';

export default class ACET_LWC_Member_Details extends LightningElement {

  handleChange(event) {
        if (this.responseData != null && this.cardName == 'Policies Card') {
            //let selectedVal = event.currentTarget.dataset.id;
            this.selPolicyIndex = event.currentTarget.dataset.id;
            this.selectedProductId;
            let selectedProdId = event.currentTarget.dataset.value;
            this.tableData.forEach((element, index) => {

                if (element.isPolicySelected === true) {
                    this.oldplanId = element.planId;

                }
                if (index === this.selPolicyIndex) {
                    this.selectedProductId = element.productNumber;
                }
            });
            this.planId = event.currentTarget.getAttribute("data-element");
            this.policyOpenModal = true;
        }
    }

  handleConfirmAutoDoc(event) {
       this.autoDocUniqueKey = this.memberId + this.firstName + this.lName + this.DOB + this.groupId + '~' + this.cardHeader + this.oldplanId;
       let selectedOption = event.currentTarget.getAttribute("data-name");
       if (selectedOption == "noOption") {
           this.disableAutoDocForPlan(this.oldplanId);
           this.oldplanId = '';
       }

       let updatedResponse = handleTableSingleData(true, this.responseData, this.planId, '', this.cardName, this.oldplanId);
       window.setAutodoc(this.autoDocUniqueKey, updatedResponse);
       this.autoDocUniqueKey = this.memberId + this.firstName + this.lName + this.DOB + this.groupId + '~' + this.cardHeader + this.planId;
       window.setAutodoc(this.autoDocUniqueKey, updatedResponse);
       cardRefreshFunctionality(this.eligibiltyJsonResponse, this.tableData, this.accId, this.selPolicyIndex);
       this.policyOpenModal = false;
  }

  async handleResponseSearch(message) {

     this.eligibilityResponse = JSON.stringify(message.recordData);
     this.eligibiltyJsonResponse = message.recordData
     let selPlan;
     if (this.eligibiltyJsonResponse != null && this.eligibiltyJsonResponse != undefined) {

         const obj = JSON.parse(this.eligibiltyJsonResponse);
         let extractedpolicyValue = obj['responseBody']['eligibilityResponse']['subject']['planPolicyInfos'];

         extractedpolicyValue.forEach((elem, index) => {
             if (elem.isPolicySelected == true) {
                 this.planId = elem.id;
                 return false;
             }
         });
     }
     this.autoDocUniqueKey = this.memberId + this.firstName + this.lName + this.DOB + this.groupId + '~' + this.cardHeader + this.planId;

     // TODO: DO NOT repeat data access multiple times (e.g. window.getAutodoc(this.autoDocUniqueKey))
     if (window.getAutodoc(this.autoDocUniqueKey) != null) {
         this.displayResults = true;
         this.responseData = window.getAutodoc(this.autoDocUniqueKey); // remove this getAutodoc call, do it earlier and SAVE RESULTS.
         let headerCheckbox = true;
         if (this.responseData != null && this.cardName == 'House Hold Card') {
             let houseHoldResponse = JSON.parse(JSON.stringify(this.responseData)); // STOP THIS...
             if (houseHoldResponse.data.length > 1) {
                 houseHoldResponse.data.forEach((element, index) => {
                     if (element.autoDoc == false && element.Field0 !== this.originatorField) {
                         headerCheckbox = false;
                     }
                 });
             } else {
                 headerCheckbox = false;
             }
             let checkboxes = this.template.querySelectorAll('[data-element="header-checkbox"]');
             checkboxes.forEach((element, index) => {
                 if (headerCheckbox) {
                     element.checked = true;
                     element.value = true;
                 } else {
                     element.checked = false;
                     element.value = false;
                 }
             });
         }

     }
     else {
       //
         await fetchResultFunctionality({
                 cardName: this.cardName,
                 businessUnitName: this.businessName,
                 extensionName: this.extensionName,
                 eligibilityResponse: this.eligibilityResponse,
                 originatorName: this.originatorField
             })
             .then((result) => {
                 this.displayResults = true;
                 this.responseData = result;  // have retrieved mapped API/Metadata response.

             })
             .catch((error) => {
                 //console.log('log error--' + JSON.stringify(error));
             });
     }

     if (this.cardName == 'Member Detail Card' || this.cardName == 'Policy Details') {
         this.isMemberDetail = true;
         this.isAutoDoc = true;
         this.eeidList = ["UnMask", "Copy"];
         //if(this.responseData!= null && this.responseData!=undefined){
         this.responseData = JSON.parse(JSON.stringify(this.responseData));
         //}

     } else if (this.cardName == 'Highlight Panel') {
         this.isMemberDetail = true;
         this.eeidList = ["UnMask", "Copy"]
     } else if (this.cardName == 'House Hold Card') {
         this.isHousehold = true;
         this.isAutoDoc = true;
     } else if (this.cardName == 'Policies Card') {
         this.isPolicies = true;
         //this.isAutoDoc=true;
     }

     var style = `
             .agefield {
                 display:none;
             }
             .eachItem:hover {
                 background-color: #F1F1F1;
                 cursor: pointer;
             }
             .iconsr{
       cursor: pointer;
       margin-left:5px;
             }
             .policystyle{
                 display:none;
             }

             }`
     var styleSheet = document.createElement("style");
     styleSheet.type = "text/css";
     styleSheet.innerText = style;
     document.head.appendChild(styleSheet);

     if (this.responseData != null && (this.cardName == 'Member Detail Card' || this.cardName == 'Highlight Panel' || this.cardName == 'Policy Details')) {
         let headerCheckBoxValue = true;
         if(this.cardName == 'Highlight Panel'){
             let highlightpanelObj={
                 "response":this.responseData,
                 "originator":this.originatorField
             }
             highlightPanelResponse=highlightpanelObj;
             console.log("HighLight Panel Response in member detail");
             console.log(highlightPanelResponse);
         }
         for (var i = 0; i < this.responseData.tileData.length; i++) {
             for (var j = 0; j < this.responseData.tileData[i].length; j++) {
                 if (this.cardName !== 'Policy Details') {
                     if (this.responseData["tileData"][i][j]["name"] == 'primaryAddress') {
                         //	if (jsonResponse["tileData"][i][j]["value"] != '' || jsonResponse["tileData"][i][j]["value"] != undefined) {
                         try {
                             let responseObj = Object.assign({
                                 isPrimary: true
                             }, this.responseData["tileData"][i][j]);
                             this.responseData["tileData"][i][j] = responseObj;
                             if (this.responseData["tileData"][i][j]["value"] != '' && this.responseData["tileData"][i][j]["value"] != undefined) {
                                 this.completeAddress = this.responseData["tileData"][i][j]["value"];
                                 var arr = this.completeAddress.split('<>');
                                 this.addressLine1 = arr[0];
                                 this.addressLine2 = arr[1];
                                 var letr = this.addressLine2.match(/\d+/g);
                                 var secondaprt = this.addressLine2.split(/([0-9]+)/)
                                 this.addressLine3 = letr[0];
                                 this.addressLine4 = secondaprt[0];
                             }
                         } catch (error) {
                             // console.error("Printing Error catch" + error);
                         }
                     } else if (this.responseData["tileData"][i][j]["name"] == 'mailingAddress') {
                         try {
                             let responseObj = Object.assign({
                                 isPrimary: false
                             }, this.responseData["tileData"][i][j]);
                             this.responseData["tileData"][i][j] = responseObj;
                             if (this.responseData["tileData"][i][j]["value"] != '' && this.responseData["tileData"][i][j]["value"] != undefined) {
                                 var mailAddress = this.responseData["tileData"][i][j]["value"];
                                 var arr = mailAddress.split('<>');
                                 this.mailAddressLine1 = arr[0];
                                 this.mailAddressLine2 = arr[1];
                                 var letr = this.mailAddressLine2.match(/\d+/g);
                                 var secondaprt = this.mailAddressLine2.split(/([0-9]+)/)
                                 this.mailAddressLine3 = letr[0];
                                 this.mailAddressLine4 = secondaprt[0];

                             }
                         } catch (error) {
                             console.error("Printing Error catch" + error);
                         }
                     } else if (this.responseData["tileData"][i][j]["name"] == 'eeid' || this.responseData["tileData"][i][j]["name"] == 'memberId') {
                         if (this.responseData["tileData"][i][j]["value"] != '' && this.responseData["tileData"][i][j]["value"] != undefined) {
                             this.isvalueAvailable = true;
                             this.dupeeid = this.responseData["tileData"][i][j]["value"];
                             var tempId = this.responseData["tileData"][i][j]["value"];
                             this.encryptedValue = encryptionFunctinality(tempId);
                             this.eeIdvalue = this.encryptedValue;
                         } else {
                             this.isvalueAvailable = false;
                         }
                     } else if (this.responseData["tileData"][i][j]["label"] == 'Name' || this.responseData["tileData"][i][j]["label"] == 'DOB') {
                         try {

                             let responseObj = Object.assign({
                                 isDisabled: true
                             }, this.responseData["tileData"][i][j]);
                             this.responseData["tileData"][i][j] = responseObj;

                         } catch (error) {
                             console.error("Dob Eror" + error);
                         }

                     }
                     else if (this.responseData["tileData"][i][j]["name"] == 'cmmIndicator') {
                        console.log("Before Assign the CMM indicator");
                        console.log(this.responseData["tileData"]);
                         cmmindicator=this.responseData["tileData"][i][j]["value"];
                     }
                 }
                 if (this.responseData["tileData"][i][j].autoDoc == false) {
                     headerCheckBoxValue = false;
                 }
             }
         }
         let checkboxes = this.template.querySelectorAll('[data-element="header-checkbox"]');
         checkboxes.forEach((element, index) => {
             if (headerCheckBoxValue) {
                 element.checked = true;
                 element.value = true;
             } else {
                 element.checked = false;
                 element.value = false;
             }
         });
         window.setAutodoc(this.autoDocUniqueKey, this.responseData);
     }

     if (this.responseData != null && this.cardName == 'House Hold Card') {
         console.log("inside the household card  speprate sCheck");
         var houseHoldData = JSON.parse(JSON.stringify(this.responseData));
         for (let j = 0; j < this.responseData.data.length; j++) {
             try {
                 var autoDocAttr = Object.assign({
                     autoDoc: false
                 }, houseHoldData["data"][j]);
                 houseHoldData["data"][j] = autoDocAttr;
             } catch (error) {
                 console.error("Printing Error catch" + error);
             }
         }
         this.responseData = houseHoldData;
         window.setAutodoc(this.autoDocUniqueKey, this.responseData);
         this.metaData = this.responseData.data;
         if (this.fieldLabels.length <= this.responseData.columns.length) {
             this.fieldLabels.push('');
         }

         if (this.responseData.columns.length > 0) {
             this.responseData.columns.forEach((element, index) => {
                 if (this.fieldLabels.length <= this.responseData.columns.length) {

                     this.fieldLabels.push(element.label);
                 }
             });

             this.fieldValues = householdFunctionality(this.metaData, this.firstName, this.lName, this.DOB, this.relationship);
         }
     }

     if (this.responseData != null && this.cardName == 'Policies Card') {

         let policiesData = JSON.parse(JSON.stringify(this.responseData)); // do not do this...
         for (let m = 0; m < this.responseData.data.length; m++) {
             if (policiesData["data"][m].Field10 == "true") { // PROBLEM: hardcoding position of 'autodoc' value?
                 try {
                     let autoDocAttr = Object.assign({
                         autoDoc: true
                     }, policiesData["data"][m]);
                     policiesData["data"][m] = autoDocAttr;

                     // autodoc row item...
                     autodocTableRow(componentName, topic, correlationId, primaryCorrelationId, this.responseData["data"][m], this.responseData["columns"], isPrimary, isSecondary);

                 } catch (error) {
                     console.error("Printing Error catch" + error);
                 }
             } else {
                 try {
                     let autoDocAttr = Object.assign({
                         autoDoc: false
                     }, policiesData["data"][m]);
                     policiesData["data"][m] = autoDocAttr;
                 } catch (error) {
                     console.error("Printing Error catch" + error);
                 }
             }

         }
         this.responseData = policiesData;
         // Remove this line and autodoc the row in-line.
         window.setAutodoc(this.autoDocUniqueKey, this.responseData);


         this.PlanPolicyDetails = this.responseData.columns;
         this.planPolicyMetadata = this.responseData.data;
         this.PlanPolicyFieldLabels = planPolicyTableHeaders(this.PlanPolicyFieldLabels, this.PlanPolicyDetails);
         this.tableData = planPolicyTableData(this.planPolicyMetadata);

         let productNumber = '';
         this.tableData.forEach(element => {
             if (element.isPolicySelected) {
                 productNumber = element.productNumber;
             }
         });

         const productNumValue = {
             "msgType": "selectedProductType",
             "msg": {
                 "topic": "topic",
                 "data": {
                     selectedProductType: productNumber
                 }
             }
         }
         publish(this.messageContext, productIdPublish, productNumValue);
     }
     if (this.responseData != null) {
         if (this.responseData.viewType == 'Table') {
             this.isTable = true;
         } else if (this.responseData.viewType == 'Dropdown') {
             this.isDropdown = true;
         } else if (this.responseData.viewType == 'Tile') {
             this.isTile = true;
         }
     }
 }
}
