var acet = acet || {};
var globalMapJSON = new Map();
var sessionMap = new Map();
var sessionDataMap = new Map();
(function (w) {

    w.acetLwcAutodoc = {
        "initAutodoc": function (sf_id) {
            console.log('INSIDE JSON initAutodoc');
        },

        "saveStateAutodocOnSearch": function () {

        },
        "createSaveTileArgs": function(sessionId, topic, componentName, correlationId, data){
          const args = {
            viewType: "Tile",
            sessionId: sessionId,
            topic: topic,
            componentName: componentName,
            correlationId: correlationId,
            primaryCorrelationId: primaryCorrelationId,
            data: data};

          return args;
        },
        "createSaveTableArgs": function(sessionId, topic, componentName, correlationId, primaryCorrelationId, data, isPrimary, isSecondary, columns){
          const args = {
            viewType: "Table",
            sessionId: sessionId,
            topic: topic,
            componentName: componentName,
            correlationId: correlationId,
            primaryCorrelationId: primaryCorrelationId,
            data: data,
            isPrimary: isPrimary,
            isSecondary: isSecondary,
            columns: columns};

          return args;
        },

        //Grab values for autodoc from topic pages and save to JSON structure
        "saveAutodoc": function (args) {
          switch(args.viewType){
            case "Tile":
              addDataToAutodocMap(args.sessionId, args.topic, args.componentName, args.correlationId, "", args.data, args.viewType, false, false, null);
              break;
            case "Table":
              addDataToAutodocMap(args.sessionId, args.topic, args.componentName, args.correlationId, args.primaryCorrelationId, args.data, args.viewType, args.isPrimary, args.isSecondary, args.columns);
              break;
            default:
          }
        },

        //Return all autodoc for session as JSON string
        "getSerializedAutodoc": function(sessionId){
          return getSessionJSONMap(sessionId, true);
        },

        //after case creation, adds Case Number, Interaction Number and Subject Type to JSON
        "addCaseIntToAutodoc": function(caseId, interactionId, subjectType, highlightInfo){
            var sessionId = highlightInfo.subjectName.replaceAll(' ', '') + highlightInfo.MemberDOB.replaceAll('/', '') + highlightInfo.MemberId + highlightInfo.GroupNumber;
            upsertSessionData(sessionId, 'caseNumber', caseId);
            upsertSessionData(sessionId, 'interactionNumber', interactionId);
            upsertSessionData(sessionId, 'subjectType', subjectType);
            sessionMap = new Map();
            var jsonMap = getJSONMap();
        }
    };
    //creates initial JSON object and adds data to existing object
    function addDataToAutodocMap(sessionId, topic, compName, correlationId, primaryCorrKey, data, viewType, isPrimary, isSecondary, columns) {
        var sessionObj = null;
        var topicObj = null;
        var compObj = null;
        var corObj = null;
        if (sessionMap.has(sessionId)) {
            topicObj = sessionMap.get(sessionId);
            if (topicObj.has(topic)) {
                compObj = topicObj.get(topic);
                if (compObj.has(compName)) {
                    corObj = compObj.get(compName);
                    if (corObj.data.has(correlationId)) {
                        corObj.data.delete(correlationId);
                    }
                    corObj.data.set(correlationId, data);
                } else {
                    compObj.set(compName, createInitialCompData(compName, correlationId, primaryCorrKey, viewType, isPrimary, isSecondary, data, columns));
                }
            } else {
                compObj = createMapAndEntry(compName, createInitialCompData(compName, correlationId, primaryCorrKey, viewType, isPrimary, isSecondary, data, columns));
                topicObj.set(topic, compObj);
            }
        } else {
            compObj = createMapAndEntry(compName, createInitialCompData(compName, correlationId, primaryCorrKey, viewType, isPrimary, isSecondary, data, columns));
            topicObj = createMapAndEntry(topic, compObj);
            sessionMap.set(sessionId, topicObj);
        }
    }
    //creates component data for tables and tiles
    function createInitialCompData(compName, correlationId, primaryCorrKey, viewType, isPrimary, isSecondary, data, columns){
        var metadata = null;
        var corrObj = createMapAndEntry(correlationId, data);
        var compObj = {};
        compObj.component = compName;
        if(viewType == "table"){
            compObj.metadata = createTableMetadata(primaryCorrKey, viewType, isPrimary, isSecondary, columns);
        }else if(viewType == "tile"){
            compObj.metadata = createTileMetadata(viewType, isPrimary, isSecondary);
        }
        compObj.data = corrObj;
        return compObj;
    }
    //generic method to create a map
    function createMapAndEntry(key, data) {
        var map = new Map();
        map.set(key, data);
        return map;
    }
    //constructs object for table metadata
    function createTableMetadata(primaryCorrKey, viewType, isPrimary, isSecondary, columnsArray){
        var obj = {};
        obj.corrKey = primaryCorrKey;
        obj.viewType = "Table";
        obj.isPrimary = isPrimary;
        obj.isSecondary = isSecondary;
        obj.columns = columnsArray;
        return obj;
    }
    //constructs object for tile metadata
    function createTileMetadata(viewType, isPrimary, isSecondary){
        var obj = {};
        obj.viewType = "Tile";
        obj.isPrimary = isPrimary;
        obj.isSecondary = isSecondary
        return obj;
    }
    //constructs object for table columns
    function createTableColumnsArray(columnArray){
        var i = 0;
        var result = [];
        for(const str of columnArray){
            var obj = {};
            obj.fieldName = "Field" + i++;
            obj.label = str;
            result.push(obj);
        }
        return result;
    }
    //constructs object for table data
    function createTableDataObj(correlationId, autodoc, dataArray){
        var obj = {};
        //obj.correlationId = correlationId;
        obj.autodoc = autodoc;
        var i = 0;
        for(const str of dataArray){
            obj["Field"+i++] = str;
        }
        return obj;
    }
    //constructs object for tile data
    function createTileDataObj(name, value, compName, isSelected, isVisible){
        var obj = {};
        obj.name = name;
        obj.value = value;
        obj.autodoc = isSelected;
        obj.visible = isVisible;
        obj.compName = compName;
        return obj;
    }
    //converts the JSON object map into a string to be used for console logs and for storing the entire JSON to contentVersion record
    function getSessionJSONMap(sessionKey, onlyAutodocItems) {
        var json = '{ "sessions":[';
        var it1 = sessionMap.keys();
        if(!sessionMap.has(sessionKey)){
          return "";
        }

        json += '{ "sessionKey": "' + sessionKey + '",';
        var result = "";
        if(sessionDataMap.has(sessionKey)){
            var map = sessionDataMap.get(sessionKey);
            var sdmIter = map.keys();
            for(const sdmKey of sdmIter){
                var value = map.get(sdmKey);
                if(typeof value == "object"){
                    result += '"' + sdmKey + '": ' + JSON.stringify(value) + ',';
                }else{
                    result += '"' + sdmKey + '": "' + value + '",';
                }
            }
        }
        json += result;
        json += '"topics": [';
        var topicMap = sessionMap.get(sessionKey);
        var topicIt = topicMap.keys();
        var topicCount = topicMap.size;
        for (const topicKey of topicIt) {
            json += '{"topicKey": "' + topicKey + '", "comps":[';
            var compMap = topicMap.get(topicKey);
            var compIt = compMap.keys();
            var compCount = compMap.size;
            for (const compKey of compIt) {
                json += '{"compKey": "' + compKey + '",';
                var corrMap = compMap.get(compKey);
                if(corrMap.metadata){
                    //console.log("typeof("+corrMap.metadata+ ") = " + typeof(corrMap.metadata));
                    if(typeof(corrMap.metadata) == "string"){
                        json += '"metadata": ' + corrMap.metadata + ', ';
                    }else{
                        json += '"metadata": ' + JSON.stringify(corrMap.metadata) + ', ';
                    }
                }
                json += '"corrIds":[';
                var corrIt = corrMap.data.keys();
                var corrCount = corrMap.data.size;

                for (const corrKey of corrIt) {
                    json += '{"corrKey": "' + corrKey + '", "data":';
                    var dataObj = corrMap.data.get(corrKey);
                    if(onlyAutodocItems){
                        json += returnOnlyAutodocData(corrMap.metadata.viewType, dataObj);
                    }else{
                        json += JSON.stringify(dataObj);
                    }
                    if(corrCount > 1){
                        json += "},";
                    }
                    else {
                        json += "}";
                    }
                    corrCount--;
                }
                json += "]";
                if(compCount > 1){
                    json += "},";
                }
                else {
                    json += "}";
                }
                compCount--;
            }
            json += "]";
            if(topicCount > 1){
                json += "},";
            }else{
                json += "}";
            }
            topicCount--;
        }
        json += "]";
        if(sessionCount > 1){
            json += "},";
        }else{
            json += "}";
        }

        json += "]}";
        return json;
    }

    function returnOnlyAutodocData(viewType, data){
      // Create a JSON string with only autodoc elements.
      // This can be used to reduce the size of autodoc files.
      // This is expected to only be used when the call is being finalized and autodoc file will be created.
      //console.log("_helperReturnOnlyAutodocData: viewType: " + viewType);
      var obj = {};
      if(viewType == "Tile"){
        for(const [key,value] of Object.entries(data)){
          if(value.autodoc){ obj[key] = value; }
        }
      }else if(viewType == "Table"){
        if(data.autodoc){ obj.data = data; }
      }
      return JSON.stringify(obj);
    }

    //adds metadata about the case to the JSON object
    function upsertSessionData(sessionId, key, value){
        var insertValue = value;

        if(sessionDataMap.has(sessionId)){
            var sessDataMap = sessionDataMap.get(sessionId);
            if(sessDataMap.has(key)){
                sessDataMap.delete(key);
            }
            sessDataMap.set(key, insertValue);
        }else{
            var sessionData = createMapAndEntry(key, insertValue);
            sessionDataMap.set(sessionId, sessionData);
        }
    }

}(window));
