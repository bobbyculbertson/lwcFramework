/*
    Pseudo code for CaseMgmtFactory
    - performs lookup of case management metadata to identify workflow builder class.
*/
public class CaseMgmtFactory{

  /*
    Lookup workflow class name for case management processing.
    pre-condition:  ICO must identify the case management request type (e.g. caseCase, route) and
                    optionally identify the LOB and product.
    post-condition: Returns instance of workflow builder.
  */
  public static ICaseMgmtWorkflowBuilder getWorkflowBuilder(caseMgmtRequest){
    // use ICO.caseMgmtRequest object to perform lookup of class to instantiate

    // Metadata defines specific class name to instantiate
    ACET_CaseMgmtWorkflowConfig__mdt cmwf = [SELECT Id, ClassName__c
                                            FROM ACET_CaseMgmtWorkflowConfig__mdt
                                            WHERE RequestType__c = caseMgmtRequest.requestType
                                            AND classType = 'workflowBuilder'
                                            AND LOB__c = caseMgmtRequest.lob
                                            AND Product__c = caseMgmtRequest.product];

    Type t = Type.forName(cmwf.ClassName__c);
    ICaseMgmtWorkflowBuilder cmwfBuilder = (ICaseMgmtWorkflowBuilder)t.newInstance();
    return cmwfBuilder;
  }

  public static ICaseMgmtWorkflowRunner getWorkflowRunner(caseMgmtRequest){
    ACET_CaseMgmtWorkflowConfig__mdt cmwf = [SELECT Id, ClassName__c
                                          FROM ACET_CaseMgmtWorkflowConfig__mdt
                                          WHERE RequestType__c = caseMgmtRequest.requestType
                                          AND classType = 'workflowRunner'
                                          AND LOB__c = caseMgmtRequest.lob
                                          AND Product__c = caseMgmtRequest.product];
    Type t = Type.forName(cmwf.ClassName__c);
    ICaseMgmtWorkflowRunner cmwfRunner = (ICaseMgmtWorkflowRunner)t.newInstance();
    return cmwfRunner;
  }
}
