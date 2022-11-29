/*
    Pseudo code for ACET_CaseMgmtCallDocumentationWorkflowBuilderV1
    - builds the workflow for case management call documentation
*/
public class ACET_CaseMgmtCallDocumentationWorkflowBuilderV1 Implements ICaseMgmtWorkflowBuilder{

  /*
    Lookup workflow class name for case management processing.
    pre-condition:  Autodoc should NOT contain any unresolved items that have not yet been addressed.
                    ICO will identify the originator, subject, comments (among other info).
    post-condition: Returns a workflow object containing workflow steps and case management status.
  */
  public ICaseMgmtWorkflow buildWorkflow(ico, autodoc){

    if(hasUnresolvedIssues(autodoc)){
      throw error;
    }

    ICaseMgmtStatusTrackerV1 statusTracker = new ACET_CaseMgmtStatusTrackerV1("caseMgmtProcessRequestResponse",ico.caseMgmtRequest);

    List<ICaseMgmtWfStep> steps = new List<ICaseMgmtWfStep>();
    steps.Add(new CMStepInitCaseV1(ico, autodoc));
    steps.Add(new CMStepOriginatorV1(ico, autodoc));
    steps.Add(new CMStepSubjectV1(ico, autodoc));
    steps.Add(new CMStepCaseStatusV1(ico, autodoc));
    steps.Add(new CMStepCreateCaseV1(ico, autodoc, statusTracker));
    steps.Add(new CMStepCaseCommentsV1(ico, autodoc));
    steps.Add(new CMStepAutodocV1(ico, autodoc));

    ICaseMgmtWorkflow workflow = new ACET_CaseMgmtCallDocumentationWorkflowV1();
    workflow.workflowSteps = steps;
    workflow.statusTracker = statusTracker;
    return workflow;
  }
}

// Interface definition
public interface ICaseMgmtWorkflowBuilder{
  ICaseMgmtWorkflow buildWorkflow(ico, autodoc);
}
