/*
    Pseudo code for CMStepMemberSubjectV1
    - Manages member subject settings for the case.
*/
public class CMStepMemberSubjectV1 implements ICaseMgmtWfStep{

  public CMStepMemberSubjectV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      set the following on case...
      1) Subject_Name__c
      2) Subject_Type__c ('Member')
      3) Relationship__c
      4) SourceCode__c
      5) Subject_Group_ID__c
      6) DOB__c
      7) ContactId (reference to Contact Account record)
      8) AccountId (reference to the subject Account record)
      9) ID__c (subject ID, member ID? EID?)
      10) Surrogate_Key__c (SPIRE uses: SubjectName + Subject DOB + SubjectId + SubjectGroupId)


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
