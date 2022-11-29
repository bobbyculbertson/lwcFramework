public with sharing class ACET_LWC_MemberCardFramework {
    public Static String eeid;

    @AuraEnabled(cacheable=true)
    public static ACETResult_Functionality fetchResultFunctionality(String cardName,string businessUnitName,string extensionName,string eligibilityResponse,String originatorName){
        List<ACETResult_Functionality.DataColumn> lstDataColumn     = new List<ACETResult_Functionality.DataColumn>();
        List<ACETResultFieldProperties> resultPropTemp = new List<ACETResultFieldProperties>();
        List<ACET_DetailsFrameworkWrapper> lstDetailsFWFields = new List<ACET_DetailsFrameworkWrapper>();
        string viewType = '';

        list<list<ACET_DetailsFrameworkTileWrapper>> lstlstFieldProp = new list<list<ACET_DetailsFrameworkTileWrapper>>();
        ACET_Mapping__mdt acetMappingRec = [Select  id,ACET_ExtensionPermission__c from 	ACET_Mapping__mdt
                                            Where ACET_Business_Unit__c =: businessUnitName AND Search_Type__c =: extensionName];

        List<ACET_Snapshot_Details__mdt> acetsnapshotRecdetail=[Select  id,ACET_Search_Results__c,Acet_Results_View__c,MasterLabel,ACET_Extension_Type__c from ACET_Snapshot_Details__mdt WHERE ACET_Extension_Type__c =: acetMappingRec.ACET_ExtensionPermission__c AND MasterLabel =:cardName   ];

        if(acetsnapshotRecdetail != null && acetsnapshotRecdetail.size()>0){
            viewType = acetsnapshotRecdetail[0].Acet_Results_View__c; // Tile or Table

            if(acetsnapshotRecdetail[0].ACET_Search_Results__c != null){
                resultPropTemp.addAll(ACETResultFieldProperties.parse(acetsnapshotRecdetail[0].ACET_Search_Results__c));
                String results = JSON.serialize(resultPropTemp);

                if(viewType == 'Tile'){
                    processTileData(eligibilityResponse,resultPropTemp, originatorName,lstlstFieldProp);
                }
                if(viewType == 'Table'){
                    ACETEligibilityResponseWrapper eligibWrap = new ACETEligibilityResponseWrapper();
                    string jsn = eligibilityResponse.replaceAll('\\\\','');
                    jsn = jsn.substring(1, jsn.length()-1 );
                    eligibWrap = eligibWrap.parse(jsn);
                    ACETEligibilityResponseWrapper.ResponseBody resBody = eligibWrap.responseBody;
                    ACETEligibilityResponseWrapper.EligibilityResponse eligResObj = resBody.eligibilityResponse;
                    list<ACETEligibilityResponseWrapper.HouseholdMembers> houdHoodWrpa = eligResObj.householdMembers;
                    ACETEligibilityResponseWrapper.Subject subjectWrap = eligResObj.subject;
                    ACETEligibilityResponseWrapper.Summary summaryWrap = subjectWrap.summary;
                    ACETEligibilityResponseWrapper.MemberDetails memberDetailsWrap = subjectWrap.memberDetails;
                    list<ACETEligibilityResponseWrapper.Summary> summaryWraplist=new list<ACETEligibilityResponseWrapper.Summary>();
                    eeid = memberDetailsWrap.eeid;

                    list<ACETEligibilityResponseWrapper.PlanPolicyInfos> policiesList = subjectWrap.planPolicyInfos;
                    system.debug('response policiesList--'+policiesList);

                    CoverageLevelWrapper cvalue=getCovarageValue(policiesList,businessUnitName);
  					summaryWraplist=getSummaryList(summaryWrap, houdHoodWrpa,cvalue);

                    string serializedStr ;

                    lstDataColumn=getHouseHoldData(resultPropTemp,cardName,summaryWraplist,serializedStr, lstDataColumn, lstDetailsFWFields );
                    lstDataColumn=getPlanPolicyData(resultPropTemp,cardName,policiesList,serializedStr, lstDataColumn, lstDetailsFWFields );




                }
            }

        }
        ACETResult_Functionality resultFunctionality = new ACETResult_Functionality(lstDataColumn, lstDetailsFWFields,lstlstFieldProp, viewType);

        return resultFunctionality;
    }
    public static CoverageLevelWrapper getCovarageValue(list<ACETEligibilityResponseWrapper.PlanPolicyInfos> policiesList,String businessUnitName ){
        CoverageLevelWrapper cvalue=new CoverageLevelWrapper();
          if(businessUnitName == 'Vision'){
                       cvalue.policyCheck = true;
               			cvalue.subscriberCheck=false;
              cvalue.subscriberAndSpouse=false;
                    }
            Else{
                 for(ACETEligibilityResponseWrapper.PlanPolicyInfos obj : policiesList){
                            if(obj.isPolicySelected==true){
                                String policyCoverage=obj.coverageLevel;
                                switch on policyCoverage {
                                    when 'Subscriber and Dependents'{
                                        cvalue.policyCheck=true;
                                        cvalue.subscriberCheck=true;
                                         cvalue.subscriberAndSpouse=false;
                                    }
                                    when 'Subscriber Only'{
                                        cvalue.subscriberCheck=true;
                                        cvalue.policyCheck=false;
                                         cvalue.subscriberAndSpouse=false;
                                    }
                                    when 'Subscriber and Spouse'{
                                        cvalue.subscriberCheck=true;
                                        cvalue.policyCheck=true;
                                        cvalue.subscriberAndSpouse=true;
                                    }
                                    when else {
                                        cvalue.policyCheck=true;
                                        cvalue.subscriberCheck=true;
                                         cvalue.subscriberAndSpouse=false;
                                    }
                                }
                            }
                        }
        }



        return cvalue;
    }
    public static list<ACETEligibilityResponseWrapper.Summary>  getSummaryList( ACETEligibilityResponseWrapper.Summary summaryWrap,list<ACETEligibilityResponseWrapper.HouseholdMembers> houdHoodWrpa,  CoverageLevelWrapper cvalue){
        list<ACETEligibilityResponseWrapper.Summary> summaryWraplist=new list<ACETEligibilityResponseWrapper.Summary>();

        if(cvalue.subscriberCheck != null & summaryWrap != null & cvalue.policyCheck != null){


        if(cvalue.subscriberCheck){

                summaryWraplist.add(summaryWrap);
            }
        if(cvalue.policyCheck){
            system.debug('checking the Wrapper'+cvalue.policyCheck);
                 summaryWraplist=getHouseHoldSummaryData(houdHoodWrpa,cvalue,summaryWraplist);

        }
        }
        return summaryWraplist;
    }

    public static list<ACETEligibilityResponseWrapper.Summary> getHouseHoldSummaryData(list<ACETEligibilityResponseWrapper.HouseholdMembers> houdHoodWrpa, CoverageLevelWrapper cvalue,list<ACETEligibilityResponseWrapper.Summary> summaryWraplist){
         for(ACETEligibilityResponseWrapper.HouseholdMembers houseHoldObj : houdHoodWrpa){
                String serializedStr = JSON.serialize(houseHoldObj.summary);
                Map<String, object> dataMap = (Map<String, object>) JSON.deserializeUntyped(serializedStr);

                String relationshipValue=subnodevalue('relationship','description',dataMap);
                if(cvalue.subscriberAndSpouse){
                    // System.debug('Printing relationship'+houseHoldObj.summary);
                    if(relationshipValue=='Wife' ||relationshipValue=='Husband' ||relationshipValue=='Subscriber') {
                        summaryWraplist.add(houseHoldObj.summary);
                    }
                }
                else{
                    summaryWraplist.add(houseHoldObj.summary);
                }




            }
        return summaryWraplist;
    }
    public static List<ACETResult_Functionality.DataColumn> getHouseHoldData(List<ACETResultFieldProperties> resultPropTemp,String cardName,list<ACETEligibilityResponseWrapper.Summary> summaryWraplist,string serializedStr,List<ACETResult_Functionality.DataColumn> lstDataColumn,List<ACET_DetailsFrameworkWrapper> lstDetailsFWFields ){
        if(summaryWraplist != null && !summaryWraplist.isEmpty() && cardName == 'House Hold Card'){
            Integer temp = 0;
            Integer tempCols = 0;
            for(ACETEligibilityResponseWrapper.Summary oneRecord : summaryWraplist){
                serializedStr = JSON.serialize(oneRecord);
                Map<String, object> dataMap = (Map<String, object>) JSON.deserializeUntyped(serializedStr);
                ACET_DetailsFrameworkWrapper tempWrap = New ACET_DetailsFrameworkWrapper();
                TableWrap tw = new TableWrap();
                tw = createTableData(resultPropTemp, dataMap, temp, tempCols);
                tempWrap = tw.tempWrap;
                lstDataColumn = tw.lstDataColumn;
                lstDetailsFWFields.add(tempWrap);
                system.debug('ORIGTEST0: ' + lstDetailsFWFields);
            }
        }
        return lstDataColumn;
    }
    public static List<ACETResult_Functionality.DataColumn> getPlanPolicyData(List<ACETResultFieldProperties> resultPropTemp,String cardName,list<ACETEligibilityResponseWrapper.PlanPolicyInfos> policiesList,string serializedStr,List<ACETResult_Functionality.DataColumn> lstDataColumn,List<ACET_DetailsFrameworkWrapper> lstDetailsFWFields ){
        if(policiesList != null && ! policiesList.isEmpty() && cardName == 'Policies Card'){
            Integer temp = 0;
            Integer tempCols = 0;
            system.debug('policiesList size----'+policiesList.size());
            for(ACETEligibilityResponseWrapper.PlanPolicyInfos onePolicy: policiesList){
                serializedStr = JSON.serialize(onePolicy);
                Map<String, object> dataMap = (Map<String, object>) JSON.deserializeUntyped(serializedStr);

                ACET_DetailsFrameworkWrapper tempWrap = New ACET_DetailsFrameworkWrapper();
                TableWrap tw = new TableWrap();
                tw = createTableData(resultPropTemp, dataMap, temp, tempCols);
                tempWrap = tw.tempWrap;
                lstDataColumn = tw.lstDataColumn;
                lstDetailsFWFields.add(tempWrap);

            }
            system.debug('lstDetailsFWFields size----'+lstDetailsFWFields.size());
        }
        return lstDataColumn;
    }

    public static void processTileData(String eligibilityResponse, List<ACETResultFieldProperties> resultPropTemp,String originatorName,list<list<ACET_DetailsFrameworkTileWrapper>> lstlstFieldProp){
        ACETEligibilityResponseWrapper eligibWrap = new ACETEligibilityResponseWrapper();
        string jsn = eligibilityResponse.replaceAll('\\\\','');
        jsn = jsn.substring(1, jsn.length()-1 );
        eligibWrap = eligibWrap.parse(jsn);
        ACETEligibilityResponseWrapper.ResponseBody resBody = eligibWrap.responseBody;
        ACETEligibilityResponseWrapper.EligibilityResponse eligResObj = resBody.eligibilityResponse;
        ACETEligibilityResponseWrapper.Subject subjectWrap = eligResObj.subject;
        ACETEligibilityResponseWrapper.Summary summaryWrap = subjectWrap.summary;
        ACETEligibilityResponseWrapper.MemberDetails memberDetailsWrap = subjectWrap.memberDetails;
        list<ACETEligibilityResponseWrapper.PlanPolicyInfos> planPolInfWrap = subjectWrap.planPolicyInfos;
        list<ACETEligibilityResponseWrapper.HouseholdMembers> houdHoodWrpa = eligResObj.householdMembers;
        list<ACETEligibilityResponseWrapper.Summary> summaryWraplist=new list<ACETEligibilityResponseWrapper.Summary>();
        for(ACETEligibilityResponseWrapper.HouseholdMembers houseHoldObj : houdHoodWrpa){
            summaryWraplist.add(houseHoldObj.summary);
        }
        list<ACET_DetailsFrameworkTileWrapper> lsTileWrap = new List<ACET_DetailsFrameworkTileWrapper>();
        Map<String, object> mapSummary = new Map<String, object>();
        string serializedStr = '';
        if(summaryWrap != null){
            mapSummary = summaryResults(serializedStr, mapSummary,summaryWrap);
        }
        Map<String, object> mapMemDetails = new Map<String, object>();
        if(memberDetailsWrap != null){

            mapMemDetails = memberDetailsResults(serializedStr, mapMemDetails,memberDetailsWrap);
        }
        Map<String, object> mapPlanPolInfo = new Map<String, object>();

        if( planPolInfWrap != null && ! planPolInfWrap.isEmpty()){

            mapPlanPolInfo=PlanpolicyResults(serializedStr,planPolInfWrap );
        }
        for(ACETResultFieldProperties fieldProp : resultPropTemp){

            string parentNode = fieldProp.parentNode;
            ACET_DetailsFrameworkTileWrapper curProp = new ACET_DetailsFrameworkTileWrapper();
            if(parentNode == 'subject'){
                curProp.value=getSubNodeValues(fieldProp,originatorName,mapMemDetails, mapSummary,mapPlanPolInfo,planPolInfWrap);

                curProp.label = fieldProp.label;
                curProp.cssclass = fieldProp.cssclass;
                curProp.showLabel = fieldProp.showLabel;
                curProp.name = fieldProp.name;
                curProp.isLink =fieldProp.isLink;
                curProp.isText =fieldProp.isText;
                curProp.showCheckBox =fieldProp.showCheckBox;
                curProp.autoDoc =fieldProp.autoDoc;
                curProp.isEncypted=fieldprop.isEncypted;
                curProp.isOriginator = fieldProp.isOriginator != null ? fieldProp.isOriginator : false;
                lsTileWrap.add(curProp);
                system.debug('field name---' +fieldProp.name);
                system.debug('curProp' +curProp);

            }
        }

        lstlstFieldProp.add(lsTileWrap);

    }

    public static String getSubNodeValues(ACETResultFieldProperties fieldProp,String originatorName,Map<String, object> mapMemDetails,Map<String, object> mapSummary,Map<String, object> mapPlanPolInfo,list<ACETEligibilityResponseWrapper.PlanPolicyInfos> planPolInfWrap){

        string node = fieldProp.node;
        String subNode=fieldProp.subNode;
        String name=fieldProp.name;
        String value;
        if(node == 'summary'){
            value=getSummaryValue(mapSummary, subNode, name);

        }
        else if(node == 'memberDetails'){
            value = getMemberDetailValue( mapMemDetails, subNode, name, originatorName, mapSummary );

        }
        else if(node == 'planPolicyInfo'){
            if(mapPlanPolInfo.get(name) instanceof List<Object>){
                value = (planPolInfWrap != null && ! planPolInfWrap.isEmpty())? String.valueOf(mapPlanPolInfo.get(name)).replaceAll('[^a-zA-Z0-9\\s+]', ''):'';
            }else{
                  value = (planPolInfWrap != null && ! planPolInfWrap.isEmpty())? (string)mapPlanPolInfo.get(name):'';
            }
        }
        return value;
    }
    public static Map<String, object> memberDetailsResults (string serializedStr,Map<String, object> mapMemDetails, ACETEligibilityResponseWrapper.MemberDetails memberDetailsWrap){

        serializedStr = JSON.serialize(memberDetailsWrap);
        mapMemDetails = (Map<String, object>) JSON.deserializeUntyped(serializedStr);
        return mapMemDetails;

    }

    public static Map<String, object> summaryResults(string serializedStr,Map<String, object> mapSummary,ACETEligibilityResponseWrapper.Summary summaryWrap){

        serializedStr = JSON.serialize(summaryWrap);
        mapSummary = (Map<String, object>) JSON.deserializeUntyped(serializedStr);
        return mapSummary;

    }
    public static Map<String, object> PlanpolicyResults(string serializedStr,list<ACETEligibilityResponseWrapper.PlanPolicyInfos> planPolInfWrap ){
        if( planPolInfWrap != null && ! planPolInfWrap.isEmpty()){
            // for(Integer i=0;i<planPolInfWrap.size();i++){
            for(ACETEligibilityResponseWrapper.PlanPolicyInfos obj : planPolInfWrap){
                //   if(planPolInfWrap[i].isPolicySelected==true) {
                if(obj.isPolicySelected==true) {
                    serializedStr = JSON.serialize(obj);
                }
            }

        }
        Map<String, object> mapPlanPolInfo=new Map<String, object>();
        system.debug('Correect array Printing '+serializedStr);
        mapPlanPolInfo = (Map<String, object>) JSON.deserializeUntyped(serializedStr);
        system.debug('mapPlanPolInfo sfmd ' + mapPlanPolInfo.get('subscriberFileMaintenanceDate'));
        system.debug('mapPlanPolInfo original ' + mapPlanPolInfo.get('originalEffectiveDate'));
        system.debug('mapPlanPolInfo cobra ' + mapPlanPolInfo.get('cobraIndicator'));
        return mapPlanPolInfo;
    }

    public static String getSummaryValue(Map<String, object> mapSummary,String subNode,String name){
        String value;
        if(mapSummary != null && ! mapSummary.isEmpty()){
            value= subNode!=''?subnodevalue(name,subNode,mapSummary):(string)mapSummary.get(name);

        }
        else{
            value = '';
        }
        return value;
    }
    public static String getMemberDetailValue(Map<String, object> mapMemDetails,String subNode,String name,String originatorName,Map<String, object> mapSummary ){
        String value;
        if(mapMemDetails != null && ! mapMemDetails.isEmpty() ){
            if(subNode!=''){
                system.debug('one '+name+'two '+subNode);
                String tempvalue=JSON.serialize(mapMemDetails.get(name));
                system.debug('Hello'+tempvalue);
                Map<String, Object> m = (Map<String, Object>)JSON.deserializeUntyped(tempvalue);
                value =(String) m.get(subNode);
            }
            else{
                switch on name {
                    when 'dateOfBirth'{
                        value = (string)mapMemDetails.get(name)+' , '+(string)mapSummary.get('age')+' '+'Years';
                        system.debug('Value ERRor1'+value);
                    }
                    when 'suffixCode'{
                        value = string.valueof(mapMemDetails.get(name));
                        system.debug('Value ERRor2'+value);
                    }
                    when 'originator'{
                        value = originatorName;
                    }
                    when else {
                        value = (string)mapMemDetails.get(name);
                        system.debug('Value ERRor3'+value);
                    }
                }

            }
        }
        else{
            value = '';
        }
        return value;
    }
    //resultPropTemp --> field map from metadata
    //dataMap		--> actual data from api response
    public static TableWrap createTableData(List<ACETResultFieldProperties> resultPropTemp, Map<String, object> dataMap, Integer temp, Integer tempCols){
        TableWrap tw = new TableWrap();
        List<ACETResult_Functionality.DataColumn> lstDataColumn     = new List<ACETResult_Functionality.DataColumn>();
        ACET_DetailsFrameworkWrapper tempWrap = New ACET_DetailsFrameworkWrapper();
        String fName = '';
        for(ACETResultFieldProperties prop : resultPropTemp){
            fName = prop.name;
            system.debug('temp: '+temp+ ' fName: '+fName+' dataMapValue: '+dataMap.get(fname));
            if(tempCols == 0){
                ACETResult_Functionality.DataColumn dataCol = new ACETResult_Functionality.DataColumn(prop.label, 'Field'+temp);
                lstDataColumn.add(dataCol);
            }
            switch on temp {
                when 0{
                    tempWrap.Field0 = (string)dataMap.get(fName);
                }
                when 1 {
                    tempWrap.Field1=prop.subNode!=''?subnodevalue(fName,prop.subNode,dataMap):(string)dataMap.get(fName);

                }
                when 2 {

                    tempWrap.Field2=extractSecondColumn(fName, eeid, dataMap);

                }
                when 3 {

                    tempWrap.Field3=prop.subNode!=''?subnodevalue(fName,prop.subNode,dataMap):(string)dataMap.get(fName);

                }
                when 4 {
                    tempWrap.Field4 = (string)dataMap.get(fName);
                }
                when 5 {
                    tempWrap.Field5 = (string)dataMap.get(fName);
                }
                when 6 {
                    tempWrap.Field6 = (string)dataMap.get(fName);
                }
                when 7 {
                    tempWrap.Field7 = (string)dataMap.get(fName);
                }
                when 8 {
                    tempWrap.Field8 = (string)dataMap.get(fName);
                }
                when 9 {
                    tempWrap.Field9 = (string)dataMap.get(fName);
                }
                when 10 {
                    tempWrap.Field10=extractTencolumn(fName, dataMap);

                } when 11 {
                    tempWrap.Field11 = extractIdcolumn(fName, dataMap);
                }
                when 12 {
                    tempWrap.Field12 = (string)dataMap.get(fName);
                }
                when 13 {
                    tempWrap.Field13 = (string)dataMap.get(fName);
                }
                when 14 {
                    tempWrap.Field14 = (string)dataMap.get(fName);
                }
                when 15 {
                    tempWrap.Field15 = (string)dataMap.get(fName);
                }
                when 16 {
                    tempWrap.Field16 = (string)dataMap.get(fName);
                }
                when 17 {
                    tempWrap.Field17 = (string)dataMap.get(fName);
                }
                when 18 {
                    tempWrap.Field18 = (string)dataMap.get(fName);
                }
                when 19 {
                    tempWrap.Field19 = (string)dataMap.get(fName);
                }
                when else {
                    continue;
                }
            }
            temp++;

        }

        tempCols++;
        temp = 0;
        tw.tempWrap = tempWrap;
        tw.lstDataColumn = lstDataColumn;
        return tw;
    }

    public static string extractSecondColumn(String fName,String eeid,Map<String, object> dataMap){
        String value;
        if(fName == 'eeid'){
            value= eeid;
        }else{
            value = (string)dataMap.get(fName);
        }
        return value;
    }
    public static string extractTencolumn(String fName,Map<String, object> dataMap){
        String value;
        if(dataMap.get(fName) instanceof Boolean){
            boolean a= (boolean)dataMap.get(fName);
            value = String.valueOf(a);
        }
        else{
            value = (string)dataMap.get(fName);
        }
        return value;
    }
     public static string extractIdcolumn(String fName,Map<String, object> dataMap){
        String value;
        if(dataMap.get(fName) instanceof Integer){
            Integer a= (Integer)dataMap.get(fName);
            value = String.valueOf(a);
        }
        else{
            value = (string)dataMap.get(fName);
        }
        return value;
    }

    public static String subnodevalue(String nameField,String resultPropTemp, Map<String, object> dataMap){
        String tempvalue=JSON.serialize(dataMap.get(nameField));
        Map<String, Object> m = (Map<String, Object>)JSON.deserializeUntyped(tempvalue);
        String subnodeValue =(String) m.get(resultPropTemp);
        return subnodeValue;
    }
    public class TableWrap {
        public ACET_DetailsFrameworkWrapper tempWrap;
        public List<ACETResult_Functionality.DataColumn> lstDataColumn;
    }
     public class CoverageLevelWrapper {
        public boolean policyCheck;
        public boolean subscriberCheck;
        public boolean subscriberAndSpouse;

    }


}
