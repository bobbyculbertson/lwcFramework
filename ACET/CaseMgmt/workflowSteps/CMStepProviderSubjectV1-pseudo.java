/*
    Pseudo code for CMStepProviderSubjectV1-pseudo.txt
    - Manages provider subject settings for the case.
*/
public class CMStepProviderSubjectV1-pseudo.txt implements ICaseMgmtWfStep{

  public CMStepProviderSubjectV1-pseudo.txt(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      set the following on case...
      1) Subject_Name__c
      2) Subject_Type__c  ('Provider')
      3) SourceCode__c
      4) ContactId (reference to Contact Account record)
      5) AccountId (reference to the subject Account record)
      6) ID__c (subject ID, Tax ID?)


      Assumptions:
      * Interaction record has been created
      * Originator Account record is linked to Interaction record

      return step.execute(case);
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
