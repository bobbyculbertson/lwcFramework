/*
    Pseudo code for CMStepOriginatorV1
    - Manages originator settings for the case.
*/
public class CMStepOriginatorV1 implements ICaseMgmtWfStep{

  public CMStepOriginatorV1(ico, autodoc){ ...ctor}

  public Case execute(Case case){
      ICaseMgmtWfStep step = null;
      switch on ico.originatorType{
        when 'provider'{
          step = new CMStepOriginatorProviderV1(ico, autodoc);
        }
        when 'member'{
          step = new CMStepOriginatorMemberV1(ico, autodoc);
        }
        when 'other'{

        }
      }

      return step.execute(case);
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
