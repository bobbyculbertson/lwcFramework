/*
    Pseudo code for CMStepCaseStatusV1.
    - Set general case state or status.
*/
public class CMStepCaseStatusV1 implements ICaseMgmtWfStep{

  public CMStepCaseStatusV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      Set the following Case attributes...
      1) Status__c
      2) Onshore_Restriction__c
      3) OwnerId (reference to User/Group case owner)
      return case;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
