/*
    Pseudo code for ACET_CaseMgmtCallDocumentationWorkflowV1
    - represents the workflow for case management call documentation.
*/
public class ACET_CaseMgmtCallDocumentationWorkflowV1 Implements ICaseMgmtWorkflow{
  public List<ICaseMgmtWfStep> workflowSteps;
  public ICaseMgmtStatusTrackerV1 statusTracker;
}

// Interface definition
public interface ICaseMgmtWorkflow{
  List<ICaseMgmtWfStep> workflowSteps;
  ICaseMgmtStatusTrackerV1 statusTracker;
}
