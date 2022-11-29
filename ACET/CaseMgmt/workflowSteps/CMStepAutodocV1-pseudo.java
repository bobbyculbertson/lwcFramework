/*
    Pseudo code for CMStepAutodocV1.
    - Save autodoc file and link to case(s).
*/
public class CMStepAutodocV1 implements ICaseMgmtWfStep{

  public CMStepAutodocV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){

      ContentVersion content = new ContentVersion();
      content.ContentLocation = 'S'; // S = Salesforce, E = External
      content.PathOnClient = 'autodoc' + case.CaseNumber + '.json';
      content.Title = 'autodoc' + case.CaseNumber;
      content.VersionData = blob.valueof(autodoc);
      content.MyExtId__c =
      insert content;

      Id contentId = [SELECT ContentDocumentId FROM ContentVersion WHERE]

      ContentDocumentLink docLink = new ContentDocumentLink();
      docLink.ContentDocumentId = contentId;
      docLink.LinkedEntityId = case.Id;
      docLink.ShareType = 'I';
      insert docLink;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
