/*
    Pseudo code for ACET_CaseMgmtCallDocumentationWorkflowBuilderV1
    - builds the workflow for case management routing
*/
public class ACET_CaseMgmtRoutingWorkflowBuilderV1 Implements ICaseMgmtWorkflowBuilder{


  /*
    Lookup workflow class name for case management processing.
    pre-condition:  Autodoc will contain unresolved items for the topic defined in the ico request.
                    ICO will identify the originator, subject, comments (among other info).
    post-condition: Returns a workflow object containing workflow steps and case management status.
  */
  public ICaseMgmtWorkflow buildWorkflowSteps(ico, autodoc){

    if(!hasUnresolvedIssues(ico.caseMgmtRequest.topic, autodoc)){
      throw error;
    }

    ICaseMgmtStatusTrackerV1 statusTracker = new ACET_CaseMgmtStatusTrackerV1();

    List<ICaseMgmtWfStep> steps = new List<ICaseMgmtWfStep>();
    steps.Add(new CMStepInitCaseV1(ico, autodoc));
    steps.Add(new CMStepOriginatorV1(ico, autodoc));
    steps.Add(new CMStepSubjectV1(ico, autodoc));
    steps.Add(new CMStepCaseStatusV1(ico, autodoc));
    steps.Add(new CMStepCaseRoutingV1(ico, autodoc, statusTracker));
    steps.Add(new CMStepCaseCommentsV1(ico, autodoc));
    steps.Add(new CMStepAutodocV1(ico, autodoc));

    ICaseMgmtWorkflow workflow = new ACET_CaseMgmtCallRoutingWorkflowV1();
    workflow.workflowSteps = steps;
    workflow.statusTracker = statusTracker;
    return workflow;
  }
}

// Interface definition
public interface ICaseMgmtWorkflowBuilder{

  List<ICaseMgmtWfStep> buildWorkflowSteps(ico, autodoc);
}
