/*
    Pseudo code for ACET_CaseMgmtWorkflowRunnerV1
    - executes workflow steps and provides standard error messages.
*/
public class ACET_CaseMgmtWorkflowRunnerV1 Implements ICaseMgmtWorkflowRunner{

  public static caseMgmtResponse execute(ICaseMgmtWorkflow workflow){
    // execute in try/catch and standardize error messages.

    Case case = null;
    foreach(ICaseMgmtWfStep step in workflow.workflowSteps){
      case = step.execute(case);
    }

    return workflow.statusTracker.getStatusResponse();
  }
}

// Interface definition
public interface ICaseMgmtWorkflowRunner{
  caseMgmtResponse execute(ICaseMgmtWorkflow workflow);
}
