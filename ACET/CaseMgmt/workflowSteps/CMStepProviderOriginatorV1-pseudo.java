/*
    Pseudo code for CMStepProviderOriginatorV1
    - Manages provider originator settings for the case.
*/
public class CMStepProviderOriginatorV1 implements ICaseMgmtWfStep{

  public CMStepProviderOriginatorV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      set the following on case...
      1) Originator_name__c
      2) OriginatorPhone__c
      3) OriginatorEmail__c
      4) Caller__c
      5) TaxID__c

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
