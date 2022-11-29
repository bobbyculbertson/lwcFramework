/*
    Pseudo code for CMStepOrsRoutingV1.
    - Review SAECaseCreationController.CreateORSRecord method for SPIRE logic.
    - Review ACET_ORSRequestWrapper.cls for ORS request body fields
*/
public class CMStepOrsRoutingV1 implements ICaseMgmtWfStep{
  // IMPORTANT: import ACET_ORSRequestWrapper.cls & ACET_ORSWebservice.cls for use in this class.
  public CMStepOrsRoutingV1(ico, autodoc, ICaseMgmtStatusTrackerV1 statusTracker){ ...ctor}
  // dependency inject routing rules...
  public void setRoutingRules(acet_routing_rules__mdt routingRules){ this.routingRules = routingRules }

  public Case execute(Case case){

    // Handle case creation step...
    CaseMgmtCreatedCaseInfo caseInfo;
    if(ico.appState.general.caseId == ""){
      // creating new case
      insert case;
      caseInfo = new CaseMgmtCreatedCaseInfo(CaseCreationStatus.created,CaseType.documentation,ico.interactionId,case.Id);
    }
    else{
      // updating existing case
      upsert case;
      caseInfo = new CaseMgmtCreatedCaseInfo(CaseCreationStatus.updated,CaseType.documentation,ico.interactionId,case.Id);
    }

    // Get unresolved items for Topic component from autodoc...
    List<IAutodocCaseItem> unresolvedItems = CMAutodocInspector.findUnresolvedItems(ico.caseMgmtRequest, autodoc);

    foreach(item in unresolvedItems){
      // Handle ORS Service Request creation...
      CaseMgmtRoutedIssueV1 routedIssue = createOrsServiceRequest(item);
      // if ORS issue was created, create the Case Item record...
      Case_Item__c ci = CMCaseItemFactory.createExternalIssueV1(routedIssue, ico, case.Id);
      insert ci;
      statusTracker.routedIssues.Add(routedIssue);
    }

    statusTracker.createdCases.Add(caseInfo);
    return case;
  }


  private CaseMgmtResponseRoutedIssuesInfo createOrsServiceRequest(UnresolvedItem item){
    // The following is a general outline for the ORS Request creation.
    // Obviously, the provider and member data will be dependent on the subject and originator type.
    ACET_ORSRequestWrapper.Provider provider = createProvider();
    ACET_ORSRequestWrapper.Member member = createMember();
    ACET_ORSRequestWrapper.Issue issue = createIssue();

    TopsCredentialFactoryV1 topsCredentialFactory = new TopsCredentialFactoryV1(TopsCredentialFactory.TOPS_USERID,TopsCredentialFactory.TOPS_BUSINESS_GROUP);
    issue.businessSegmentName = topsCredentialFactory.getBusinessSegmentName();

    ACET_ORSRequestWrapper.controlModifiers controlModifiers = new ACET_ORSRequestWrapper.controlModifiers();
    controlModifiers.orsSourceSystemParameters = topsCredentialFactory.getSysParameters(searchInputParams) ;

    ACET_ORSRequestWrapper.Meta metaInfo = new ACET_ORSRequestWrapper.Meta();
    metaInfo.controlModifiers = controlModifiers;

    ACET_ORSRequestWrapper.Data orsData = new ACET_ORSRequestWrapper.Data();
    orsData.issue = issue;
    orsData.createdByApplicationIndicator = 'H'; // H = ACET

    ACET_ORSRequestWrapper orsRequest = new ACET_ORSRequestWrapper();
    orsRequest.meta = metaInfo;
    orsRequest.data = orsData;

    if (ico.caseMgmtRequest.Type == 'ISSUE_ROUTED') {
      orsRequest.data.issue.IssueHandling.routedTo.officeId = caseWrapper.officeAPI;
      orsRequest.data.issue.IssueHandling.routedTo.departmentCode = caseWrapper.departmentAPI;
      orsRequest.data.issue.IssueHandling.routedTo.teamCode = caseWrapper.teamAPI;
      orsRequest.data.issue.status = 'O';
      orsRequest.data.issue.comments = orsCaseComments;
    }

    HttpResponse response = (HttpResponse) ACET_ORSWebservice.createORSIssue(GSON.serialize(orsRequest));
    // handle HTTP response codes...

    return new CaseMgmtRoutedIssueV1(item, item.componentName, item.correlationId, this.routingRules.RoutingSystem, response.serviceRequestId);
  }
  
  private ACET_ORSRequestWrapper.Provider createProvider(){
    ACET_ORSRequestWrapper.Provider provider = new ACET_ORSRequestWrapper.Provider();
    ACET_ORSRequestWrapper.ProviderName providerName = new ACET_ORSRequestWrapper.ProviderName();
    ACET_ORSRequestWrapper.Address providerAddress = new ACET_ORSRequestWrapper.Address();
    ACET_ORSRequestWrapper.ProviderInfo providerInfo = new ACET_ORSRequestWrapper.ProviderInfo();
    return provider;
  }
  private ACET_ORSRequestWrapper.Member createMember(){
    ACET_ORSRequestWrapper.Member member = new ACET_ORSRequestWrapper.Member();
    ACET_ORSRequestWrapper.Address memberAddress = new ACET_ORSRequestWrapper.Address();
    ACET_ORSRequestWrapper.FaxNumber memberFaxNumber = new ACET_ORSRequestWrapper.FaxNumber();
    ACET_ORSRequestWrapper.ServicesQuoted servicesQuoted = new ACET_ORSRequestWrapper.ServicesQuoted();
    ACET_ORSRequestWrapper.Employer memberEmployer = new ACET_ORSRequestWrapper.Employer();
    return member;
  }
  private ACET_ORSRequestWrapper.Issue createIssue(){
    ACET_ORSRequestWrapper.Issue issue = new ACET_ORSRequestWrapper.Issue();
    ACET_ORSRequestWrapper.Originator originator = new ACET_ORSRequestWrapper.Originator();
    ACET_ORSRequestWrapper.Cti cti = new ACET_ORSRequestWrapper.Cti();
    ACET_ORSRequestWrapper.EveningPhone eveningPhone = new ACET_ORSRequestWrapper.EveningPhone();
    ACET_ORSRequestWrapper.DayPhone dayPhone = new ACET_ORSRequestWrapper.DayPhone();

    ACET_ORSRequestWrapper.CarbonCopies carbonCopies = new ACET_ORSRequestWrapper.CarbonCopies();
    ACET_ORSRequestWrapper.RoutedTo routedTo = new ACET_ORSRequestWrapper.RoutedTo();
    ACET_ORSRequestWrapper.ResolvedBy resolvedBy = new ACET_ORSRequestWrapper.ResolvedBy();
    ACET_ORSRequestWrapper.IssueHandling issueHandling = new ACET_ORSRequestWrapper.IssueHandling();
    ACET_ORSRequestWrapper.Grievance grievance = new ACET_ORSRequestWrapper.Grievance();
    ACET_ORSRequestWrapper.Claim claim = new ACET_ORSRequestWrapper.Claim();
    ACET_ORSRequestWrapper.Drafts drafts = new ACET_ORSRequestWrapper.Drafts();
    ACET_ORSRequestWrapper.Atg atg = new ACET_ORSRequestWrapper.Atg();

    ACET_ORSRequestWrapper.FollowUp followUp = new ACET_ORSRequestWrapper.FollowUp();
    ACET_ORSRequestWrapper.OtherInfo otherInfo = new ACET_ORSRequestWrapper.OtherInfo();
    ACET_ORSRequestWrapper.Employer empInfp = new ACET_ORSRequestWrapper.Employer();
    return issue;
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
