package weather2.client.entity.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import com.mojang.blaze3d.vertex.VertexConsumer;

import extendedrenderer.particle.entity.ParticleTexFX;

public class ParticleSandstorm extends ParticleTexFX {

	public double angleToStorm = 0;
	public int heightLayer = 0;
	public double distAdj = 0;
	public boolean lockPosition = false;
	
	public ParticleSandstorm(Level worldIn, double posXIn, double posYIn,
			double posZIn, double mX, double mY, double mZ,
			TextureAtlasSprite par8Item) {
		super((ClientLevel) worldIn, posXIn, posYIn, posZIn, mX, mY, mZ, par8Item);
	}

	/**
	 *TODO: prevent rendering of particle if outside visible area of thick sandstorm fog
	 * based on fps changes between performance settings, I suspect game is taking fps hit trying to render particles player cant see anyways due to sandstorm fog effect
	 */

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		super.render(buffer, renderInfo, partialTicks);
	}
}
