Remove import items:
  - fetchTTSType
  - fetchTTSSubtype
  - createCase
  - handleNavigation
  - getInteractionContext
  - setInteractionContext

Add:
import {saveCase} from 'c/acet_lwc_caseMgmt';

saveCaseClicked(){
  saveCase();
}
