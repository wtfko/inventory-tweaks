package invtweaks;

import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

/**
 * Chest sorting button
 *
 * @author Jimeo Wan
 */
public class InvTweaksGuiSortingButton extends InvTweaksGuiIconButton {

    private final ContainerSection section = ContainerSection.CHEST;

    private SortingMethod algorithm;
    private int rowSize;

    public InvTweaksGuiSortingButton(InvTweaksConfigManager cfgManager_, int id_, int x, int y, int w, int h,
                                     String displayString_, String tooltip, SortingMethod algorithm_, int rowSize_,
                                     boolean useCustomTexture) {
        super(cfgManager_, id_, x, y, w, h, displayString_, tooltip, useCustomTexture);
        algorithm = algorithm_;
        rowSize = rowSize_;
    }

    @Override
    public void drawButton(@NotNull Minecraft minecraft, int i, int j) {
        super.drawButton(minecraft, i, j);

        // Display symbol
        int textColor = getTextColor(i, j);
        switch(displayString) {
            case "h":
                drawRect(xPosition + 3, yPosition + 3, xPosition + width - 3, yPosition + 4,
                        textColor);
                drawRect(xPosition + 3, yPosition + 6, xPosition + width - 3, yPosition + 7,
                        textColor);
                break;
            case "v":
                drawRect(xPosition + 3, yPosition + 3, xPosition + 4, yPosition + height - 3,
                        textColor);
                drawRect(xPosition + 6, yPosition + 3, xPosition + 7, yPosition + height - 3,
                        textColor);
                break;
            default:
                drawRect(xPosition + 3, yPosition + 3, xPosition + width - 3, yPosition + 4,
                        textColor);
                drawRect(xPosition + 5, yPosition + 4, xPosition + 6, yPosition + 5, textColor);
                drawRect(xPosition + 4, yPosition + 5, xPosition + 5, yPosition + 6, textColor);
                drawRect(xPosition + 3, yPosition + 6, xPosition + width - 3, yPosition + 7,
                        textColor);
                break;
        }
    }

    /**
     * Sort container
     */
    @Override
    public boolean mousePressed(Minecraft minecraft, int i, int j) {
        if(super.mousePressed(minecraft, i, j)) {
            try {
                new InvTweaksHandlerSorting(minecraft, cfgManager.getConfig(), section, algorithm, rowSize).sort();
            } catch(Exception e) {
                InvTweaks.logInGameErrorStatic("invtweaks.sort.chest.error", e);
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }

    }

}
