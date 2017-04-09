package invtweaks.forge;

import invtweaks.InvTweaksGuiSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("unused")
public class ModGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @NotNull
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        // TODO: Find out if we can just cache this?
        return new InvTweaksGuiSettings(parentScreen);
    }

    @NotNull
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return InvTweaksGuiSettings.class;
    }

    @Nullable
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Nullable
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
}
