

public class CMAutodocInspector{

  public static List<UnresolvedItem> findUnresolvedItems(ICO ico, Autodoc autodoc){
    List<UnresolvedItem> list = new List();
    String topic = ico.caseMgmtRequest.data.topic;
    Object obj = autodoc.getPrimaryComponent(topic);
    if(obj.Type == "Table"){
       TableRowData data = ((AutodocTable)obj).getUnresolvedRows();
       TTS tts = new TTS(topic, ico.caseMgmtRequest.data.type, ico.caseMgmtRequest.data.subtype);
       list.Add(new UnresolvedItem(tts, data.correlationId, data));
    }
    return list;
  }
}
