package net.fabricmc.example.healthhud.mixin;

import btw.community.example.HealthHudAddon;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class HealthHudMixin {
	@Inject(at = @At("RETURN"), method = "renderEntity")
	private void renderEntityHealth(Entity entity, float partialTicks, CallbackInfo ci) {
		if (!HealthHudAddon.healthBarEnabled) return;
		if (!(entity instanceof EntityLiving)) return;
		EntityLiving living = (EntityLiving) entity;
		float health = living.getHealth();
		float maxHealth = living.getMaxHealth();
		if (health <= 0 || maxHealth <= 0 || Float.isNaN(health) || Float.isNaN(maxHealth) || Float.isInfinite(health) || Float.isInfinite(maxHealth)) return;
		
		RenderManager rm = (RenderManager) (Object) this;
		if (rm.worldObj == null || rm.getFontRenderer() == null) return;
		
		FontRenderer font = rm.getFontRenderer();
		double offsetY = entity.height + 0.5;
		
		double ex = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double ey = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double ez = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		if (Double.isNaN(ex) || Double.isNaN(ey) || Double.isNaN(ez)) return;
		
		GL11.glPushMatrix();
		try {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			
			GL11.glTranslated(ex - rm.viewerPosX, ey + offsetY - rm.viewerPosY, ez - rm.viewerPosZ);
			GL11.glRotatef(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(rm.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-0.03F, -0.03F, 0.03F);
			
			float healthPercent = Math.max(0.0F, Math.min(1.0F, health / maxHealth));
			int barW = 40, barH = 6;
			int barX = -barW / 2, barY = -barH / 2;
			
			Tessellator t = Tessellator.instance;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			float fillW = barW * healthPercent;
			if (fillW > 0) {
				t.startDrawingQuads();
				t.setColorRGBA(85, 255, 85, 220);
				t.addVertex(barX, barY, 0.0);
				t.addVertex(barX + fillW, barY, 0.0);
				t.addVertex(barX + fillW, barY + barH, 0.0);
				t.addVertex(barX, barY + barH, 0.0);
				t.draw();
			}
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			String hStr = (health % 1.0F == 0.0F) ? String.format("%.0f", health) : String.format("%.1f", health);
			String mStr = (maxHealth % 1.0F == 0.0F) ? String.format("%.0f", maxHealth) : String.format("%.1f", maxHealth);
			String text = hStr + "/" + mStr;
			
			int txtW = font.getStringWidth(text);
			int txtX = -txtW / 2;
			int txtY = barY + (barH - 8) / 2;
			
			int outlineColor = 0x000000;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx != 0 || dy != 0) {
						font.drawString(text, txtX + dx, txtY + dy, outlineColor);
					}
				}
			}
			font.drawString(text, txtX, txtY, 0xFFFFFF);
		} finally {
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
	}
}
