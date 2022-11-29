/*
    Pseudo code for CMStepCaseRoutingV1
    - Manages originator settings for the case.
*/
public class CMStepCaseRoutingV1 implements ICaseMgmtWfStep{

  public CMStepCaseRoutingV1(ico, autodoc, ICaseMgmtStatusTrackerV1 statusTracker){ ...ctor}

  public Case execute(Case case){
    // Route Issue workflow...
    // 1) Inspect the ICO.caseMgmtRequest object to identify the Topic, Type, Subtype, Route Type & Route Subtype.
    // 2) Based on step #1, verify there are unresolved autodoc items for the specified topic.
    // 3) Perform SOQL query for routing rules (ACET_CaseRoutingConfig__mdt)
    //  a) [SELECT Id, OfficeCode, DeptCode, TeamCode, RoutingSystem, Category, Reason FROM
    //     ACET_CaseRoutingConfig__mdt WHERE ID = {ID OF Related TTS & Route Type & Route Subtype} AND LOB = 'Specialty']
    // 4) Lookup related Category & Reason code from identifiers in acet_routing_rules__mdt
    // 5) Use RoutingSystem to determine if using ORS or ACET to route issue.


    // Metadata defines specific class name to instantiate
    ACET_CaseRoutingConfig__mdt routingConfig = [SELECT Office_ID, Dept_ID, Team_ID, Routing_System, Case_Queue
                                          FROM ACET_CaseRoutingConfig__mdt
                                          WHERE ID = ico.caseMgmtRequest.routeConfigId // <-- preferrable to use the indexed ID to lookup config data.
                                          //WHERE LOB = ico.lob // <-- less preferred, using multi-variable query.
                                          //AND Route_Type = ico.caseMgmtRequest.routeType
                                          //AND Route_Subtype = ico.caseMgmtRequest.routeSubtype

      ICaseMgmtWfStep step = null;

      switch on routingConfig.Routing_System{
        when 'ORS'{
          step = new CMStepORSRoutingV1(ico, autodoc, statusTracker);
          step.setRoutingRules(routingConfig);
        }
        when 'ACET'{
          step = new CMStepACETRoutingV1(ico, autodoc, statusTracker);
          step.setRoutingRules(routingConfig);
        }
        when 'ENTNOW'{

        }
      }

      return step.execute(case);
  }
}

// Interface definition
public interface ICaseMgmtWfStep{
  caseMgmtResponse execute(Case case);
}
