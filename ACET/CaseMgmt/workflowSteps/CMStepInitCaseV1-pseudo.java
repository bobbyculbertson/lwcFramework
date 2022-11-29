/*
    Pseudo code for CMStepInitCaseV1
    - Expected to be the first workflow step.
    - Checks ICO to determine if case exists or if new case will be created (e.g. if previous route occured)
*/
public class CMStepInitCaseV1 implements ICaseMgmtWfStep{

  public CMStepInitCaseV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      // NOTE: this component ignores the Case argument.

      Case initCase = null;
      if(ico.caseId == ""){
        initCase = new Case();
        initCase.RecoreTypeId = setRecordType();
        initCase.Interaction__c = linkInteractionRecord();
        initCase.Created_By_Role__c = setUserRoleName();
      }else{
        initCase = [SELECT Id, Name FROM Case WHERE Id = ico.caseId]
      }

      return initCase;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
