
/*
    Pseudo code for CaseManagementController
    - handles case management requests from LWC front-end.
    - uses strategy & factory patterns to define and execute case management workflow.
*/
public class CaseManagementController {

  /*
    Process case management request
    pre-condition:  LWC front-end passed valid ico (Interaction Context Object) & autodoc data which
                    represents the subject interaction app state (i.e. member or provider subject identified
                    in a snapshot page)
    post-condition: Case, Case Item and Autodoc is created or updated based on the request type.
                    Response object will identify Case or Case Items created from the process and
                    identify the unresolved autodoc items that were addressed (if route was requested).
  */
  public caseMgmtResponse processRequest(ico, autodoc){

    // get case management workflow builder...
    ICaseMgmtWorkflowBuilder cmwfBuilder = CaseMgmtFactory.getWorkflowBuilder(ico.caseMgmtRequest);

    // get workflow steps from builder...
    ICaseMgmtWorkflow workflow = cmwfBuilder.buildWorkflow(ico, autodoc);

    // get workflow runner...
    ICaseMgmtWorkflowRunner runner = CaseMgmtFactory.getWorkflowRunner(ico.caseMgmtRequest);

    // execute steps with workflow runner...
    caseMgmtResponse response = runner.execute(workflow);

    return response;
  }
}
