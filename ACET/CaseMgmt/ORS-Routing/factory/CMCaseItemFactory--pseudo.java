

public class CMCaseItemFactory{

  public static Case_Item__c createExternalIssueV1(CaseMgmtRoutedIssueV1 routedIssue, ICO ico, String caseId){
    Case_Item__c ci = new Case_Item__c;

    ci.Case__c = caseId;
    ci.Topic__c = routedIssue.autodocCaseItem.topic;
    ci.Type__c = routedIssue.autodocCaseItem.type;
    ci.Subtype__c = routedIssue.autodocCaseItem.subtype;
    ci.ExternalID__c = CMCaseItem.getExternalIdV1(routedIssue.autodocCaseItem);
    ci.Resolved__c = routedIssue.status;
    ci.Resolved_Reason__c = routedIssue.statusReason;
    ci.Ext_Record_ID__c = routedIssue.routedIssueRefId;
    ci.Ext_Record_Status__c = routedIssue.status;

    if(ico.subjectType == "member"){
      ci.Group_Number__c = ico.subjectData.member.groupId;
    }

    if(ico.originatorType == "provider"){
      ci.tax_id__c = ico.originatorData.provider.TaxId;
    }

    return ci;
  }

}
