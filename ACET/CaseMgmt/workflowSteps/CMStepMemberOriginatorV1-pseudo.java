/*
    Pseudo code for CMStepMemberOriginatorV1
    - Manages member originator settings for the case.
*/
public class CMStepMemberOriginatorV1 implements ICaseMgmtWfStep{

  public CMStepMemberOriginatorV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      set the following on case...
      1) Originator_name__c
      2) Originator_Relationship__c
      3) OriginatorPhone__c
      4) OriginatorEmail__c
      5) Caller__c

      Assumptions:
      * Interaction record has been created
      * Originator Account record is linked to Interaction record

      return case;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
