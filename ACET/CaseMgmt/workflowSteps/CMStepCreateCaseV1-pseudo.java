/*
    Pseudo code for CMStepCreateCaseV1.
    - Set general case state or status.
*/
public class CMStepCreateCaseV1 implements ICaseMgmtWfStep{

  public CMStepCreateCaseV1(ico, autodoc, ICaseMgmtStatusTrackerV1 statusTracker){ ...ctor}

  public Case execute(Case case){
      NOTE: this class is only needed for final step of call
      documentation. It would not be used for routing workflow.
      Use the ICaseMgmtStatusTrackerV1 object to capture the
      info of the created case (if one is created).

      CaseMgmtCreatedCaseInfo caseInfo;
      if(ico.id == ""){
        // creating new case
        insert case;
        caseInfo = new CaseMgmtCreatedCaseInfo(CaseCreationStatus.created,CaseType.documentation,ico.interactionId,case.Id);
      }else{
        // updating existing case
        upsert case;
        caseInfo = new CaseMgmtCreatedCaseInfo(CaseCreationStatus.updated,CaseType.documentation,ico.interactionId,case.Id);
      }

      statusTracker.createdCases.Add(caseInfo);
      return case;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
