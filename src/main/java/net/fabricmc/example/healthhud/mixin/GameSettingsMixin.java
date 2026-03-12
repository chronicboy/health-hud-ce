package net.fabricmc.example.healthhud.mixin;

import btw.community.example.HealthHudAddon;
import net.minecraft.src.GameSettings;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.Arrays;

@Mixin(GameSettings.class)
public abstract class GameSettingsMixin {
	@Shadow
	public KeyBinding[] keyBindings;

	@Inject(method = "<init>(Lnet/minecraft/src/Minecraft;Ljava/io/File;)V", at = @At(value = "TAIL"))
	private void addHealthHudKeybind(Minecraft par1Minecraft, File par2File, CallbackInfo ci) {
		KeyBinding[] newKeyBindings = Arrays.copyOf(keyBindings, keyBindings.length + 1);
		newKeyBindings[keyBindings.length] = HealthHudAddon.toggleHealthBarKey;
		keyBindings = newKeyBindings;
		KeyBinding.resetKeyBindingArrayAndHash();
	}
}
