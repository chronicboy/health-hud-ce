package net.fabricmc.example.healthhud.mixin;

import btw.community.example.HealthHudAddon;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	private static boolean wasHealthBarKeyPressed = false;

	@Inject(at = @At("HEAD"), method = "runTick")
	private void onTick(CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;

		if (mc.theWorld != null && mc.thePlayer != null && mc.currentScreen == null) {
			if (HealthHudAddon.toggleHealthBarKey.pressed) {
				if (!wasHealthBarKeyPressed) {
					wasHealthBarKeyPressed = true;
					HealthHudAddon.healthBarEnabled = !HealthHudAddon.healthBarEnabled;
					HealthHudAddon.saveConfig();
				}
			} else {
				wasHealthBarKeyPressed = false;
			}
		}
	}
}
