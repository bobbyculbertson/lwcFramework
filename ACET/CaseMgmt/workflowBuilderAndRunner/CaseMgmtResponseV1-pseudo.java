/*
    Represents the Case Management API response (to be serialized into JSON)
*/
public class CaseMgmtResponseV1 {
  public String type;
  public String requestType;
  public CaseMgmtResponseData data;
}

public class CaseMgmtResponseData{
  public List<CaseMgmtCreatedCaseInfo> casesCreated;
  public List<CaseMgmtResponseRoutedIssuesInfo> routedIssues;
}
public Enum CaseType{
  public static final documentation = "documentation";
  public static final internalRoute = "internalRoute";
}
public class CaseCreationStatus{
  public static final created = "created";
  public static final updated = "updated";
}
public class CaseMgmtCreatedCaseInfo{
  public CaseCreationStatus creationStatus
  public CaseType type;
  public String interactionId;
  public String caseId;
}
public class CaseMgmtRoutedIssuesInfo{
  public String topic;
  public String componentName;
  public String correlationId;
  public String routingSystem;
  public String referenceId;
}
