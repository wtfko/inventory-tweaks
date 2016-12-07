package invtweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * Icon-size button, which get drawns in a specific way to fit its small size.
 *
 * @author Jimeo Wan
 */
public class InvTweaksGuiIconButton extends InvTweaksGuiTooltipButton {

    @NotNull
    private static ResourceLocation resourceButtonCustom = new ResourceLocation("inventorytweaks",
            "textures/gui/button10px.png");
    @NotNull
    private static ResourceLocation resourceButtonDefault = new ResourceLocation("textures/gui/widgets.png");
    protected InvTweaksConfigManager cfgManager;
    private boolean useCustomTexture;

    public InvTweaksGuiIconButton(InvTweaksConfigManager cfgManager_, int id_, int x, int y, int w, int h,
                                  String displayString_, String tooltip, boolean useCustomTexture_) {
        super(id_, x, y, w, h, displayString_, tooltip);
        cfgManager = cfgManager_;
        useCustomTexture = useCustomTexture_;
    }

    @Override
    public void drawButton(@NotNull Minecraft minecraft, int i, int j) {
        super.drawButton(minecraft, i, j);

        // Draw background (use the 4 corners of the texture to fit best its small size)
        int k = getHoverState(isMouseOverButton(i, j));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if(useCustomTexture) {
            minecraft.getTextureManager().bindTexture(resourceButtonCustom);
            drawTexturedModalRect(xPosition, yPosition, (k - 1) * 10, 0, width, height);
        } else {
            minecraft.getTextureManager().bindTexture(resourceButtonDefault);
            drawTexturedModalRect(xPosition, yPosition, 1, 46 + k * 20 + 1, width / 2,
                    height / 2);
            drawTexturedModalRect(xPosition, yPosition + height / 2, 1,
                    46 + k * 20 + 20 - height / 2 - 1, width / 2, height / 2);
            drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2 - 1,
                    46 + k * 20 + 1, width / 2, height / 2);
            drawTexturedModalRect(xPosition + width / 2, yPosition + height / 2,
                    200 - width / 2 - 1, 46 + k * 20 + 19 - height / 2,
                    width / 2, height / 2);
        }

    }

}