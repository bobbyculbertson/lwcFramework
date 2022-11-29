

public class CMCaseItem{

  // note: below are SPIRE External ID values... as examples
  //private static final String BENFIT_DETAILS = " - Benefit Details";
  //private static final String BENFIT_ACCUMS = " - Benefit Accumulations";
  //private static final String PAYMENT_NOT_ON_FILE = "Payment Not On File";
  //private static final String PROVIDER_NOT_FOUND = "Provider Not Found";
  //private static final String MEMBER_NOT_FOUND = "Member Not Found";
  //private static final String FINANCIALS = "Financials";
  //private static final String PCP_REFERRALS = "PCP Referrals";
  //private static final String AUTHORIZATIONS = "Authorizations";

  // note: below are proposed External ID formats for Specialty. Similar, but different.
  private static final String CLAIM_NUMBER_V1 = "CLAIM-NUMBER: ";
  private static final String BENFIT_DETAILS_V1 = "BENEFIT: ";
  private static final String VIEW_ELIG_V1 = "POLICY-PRODUCT: ";
  private static final String PROVIDER_LOOKUP_V1 = "PROVIDER-LOOKUP: ";

  /*
      Pre-condition: ICO component names replace spaces with dashes (e.g. 'policy-list', 'policy-details')
      Post-condition: returns 'external id' for Case Item records.
  */
  public static String getExternalIdV1(IAutodocCaseItem autodocCaseItem){
    // NOTE: if the business wants to include data other than the correlation ID, then
    // the solution becomes LOB specific.
    String value = "";
    Switch autodocCaseItem.topic
      case "View Claims":
        value = CLAIM_NUMBER_V1 + autodocCaseItem.correlationId;
      case "Plan Benefits":
        value = BENFIT_DETAILS_V1 + autodocCaseItem.correlationId;
      case "View Eligibility":
        value = VIEW_ELIG_V1 + autodocCaseItem.correlationId;
      case "Provider Lookup":
        value = PROVIDER_LOOKUP_V1 + autodocCaseItem.correlationId;
      default:
    return value;
  }

}
