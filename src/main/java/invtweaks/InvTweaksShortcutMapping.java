package invtweaks;

import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jimeo Wan
 */
public class InvTweaksShortcutMapping {

    private List<Integer> keysToHold = new ArrayList<>();

    public InvTweaksShortcutMapping(int keyCode) {
        keysToHold.add(keyCode);
    }

    public InvTweaksShortcutMapping(String... keyNames) {
        for(String keyName : keyNames) {
            // - Accept both KEY_### and ###, in case someone
            //   takes the LWJGL Javadoc at face value
            // - Accept LALT & RALT instead of LMENU & RMENU
            keyName = keyName.trim().replace("KEY_", "").replace("ALT", "MENU");
            keysToHold.add(Keyboard.getKeyIndex(keyName));
        }
    }

    public boolean isTriggered(Map<Integer, Boolean> pressedKeys) {
        for(Integer keyToHold : keysToHold) {
            if(keyToHold != Keyboard.KEY_LCONTROL) {
                if(!pressedKeys.get(keyToHold)) {
                    return false;
                }
            }
            // AltGr also activates LCtrl, make sure the real LCtrl has been pressed
            else if(!pressedKeys.get(keyToHold) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> getKeyCodes() {
        return this.keysToHold;
    }
}
