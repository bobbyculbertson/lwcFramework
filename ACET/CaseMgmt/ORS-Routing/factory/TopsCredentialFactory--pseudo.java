public class TopsCredentialFactoryV1{
  static final String TOPS_USERID = 'local.TOPSProfileInfo.userId';
  static final String TOPS_BUSINESS_GROUP = 'local.TOPSProfileInfo.businessGroups';

  private String userId;
  private String businessGroup;
  private String topsUserId;
  private List<String> businessSegmentList;

  public TopsCredentialFactory(String userId, String topsBusinessGroup){
    this.userId = userId;
    this.topsBusinessGroup = topsBusinessGroup;
    this.topsUserId = (String) Cache.Session.get(userId);
    this.businessSegmentList = (List<String>) Cache.Session.get(topsBusinessGroup);
    if(this.businessSegmentList == null){
      this.businessSegmentList = new List<String>();
    }
  }

  public List<String> getSearchParams(){
    List<String> searchInputParams = new List<String>();

    if (!String.isBlank(this.topsUserId)) {
      searchInputParams = topsUserId.split('/');
    }
    else{
      ACET_LoginFlowCtrl loginflow = new ACET_LoginFlowCtrl();
      loginflow.validateTopsCredentials();
      topsUserId = (String) Cache.Session.get(this.userId);
      if (this.businessSegmentList == null) {
        this.businessSegmentList = (List<String>) Cache.Session.get(this.topsBusinessGroup);
      }
      if (!String.isBlank(topsUserId)) {
        searchInputParams = topsUserId.split('/');
      }
    }

    return searchInputParams;
  }

  public String getBusinessSegmentName (){
    if (this.businessSegmentList.contains('STANDARD')) {
      return  'STANDARD';
    } else {
      return "";
    }
  }
}
