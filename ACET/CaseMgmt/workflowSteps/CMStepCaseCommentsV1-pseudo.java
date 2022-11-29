/*
    Pseudo code for CMStepCaseCommentsV1.
    - Set case comments.
*/
public class CMStepCaseCommentsV1 implements ICaseMgmtWfStep{

  public CMStepCaseCommentsV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){

      CaseComment comment = new CaseComment();
      comment.ParentId = case.Id;

      Workflow step will do the following:
      1) Determine if case has routed issues.
      2) Create comments from free-form comment entry.

      Question: Do we need additional comments for routed items?

      insert comment;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
