/*
    Represents the Case Management API response (to be serialized into JSON)
*/
public class ACET_CaseMgmtStatusTrackerV1 implements ICaseMgmtStatusTrackerV1{
  public String type;
  public String requestType;
  public List<CaseMgmtCreatedCaseInfo> createdCases;
  public List<CaseMgmtResponseRoutedIssuesInfo> routedIssues;

  public CaseMgmtStatusTrackerV1(apiResponseType, caseMgmtRequest){
    type = apiResponseType;
    requestType = caseMgmtRequest.type;
  }

  public CaseMgmtResponseV1 getStatusResponse(){
    CaseMgmtResponseV1 response = new CaseMgmtResponseV1();

    note: build CaseMgmtResponse using the createdCases & routedIssues data
    captured in this class.
  }
}

public interface ICaseMgmtStatusTrackerV1{
  public List<CaseMgmtCreatedCaseInfo> createdCases;
  public List<CaseMgmtResponseRoutedIssuesInfo> routedIssues;
  public CaseMgmtResponseV1 getStatusResponse();
}
