package weather2.weathersystem.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import extendedrenderer.particle.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ICloudRenderHandler;
import weather2.client.shaders.ShaderInstanceExtended;
import weather2.client.shaders.WeatherShaders;

import java.util.Random;

public class CloudRenderHandler implements ICloudRenderHandler {

    private boolean cloudsNeedUpdate = true;
    private VertexBuffer cloudsVBOOld;
    private VertexBuffer cloudsVBO;
    //private VertexBuffer cloudVBO;

    private int sphereIndex = 0;

    private Random rand = new Random();
    private Random rand2 = new Random();

    @Override
    public void render(int ticks, float partialTicks, PoseStack matrixStackIn, ClientLevel world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) {
        //dont use until interface is fixed to add projection matrix
        //this.render(ticks, partialTicks, matrixStackIn, null, world, mc, viewEntityX, viewEntityY, viewEntityZ);
    }
    //PoseStack matrixStackIn, Matrix4f projectionMatrix, float p_172957_, double p_172958_, double viewEntityY, double p_172960_

    //public void render(int ticks, float partialTicks, PoseStack matrixStackIn, Matrix4f projectionMatrix, ClientLevel world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) {

    public void render(PoseStack matrixStackIn, Matrix4f projectionMatrix, float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ) {
        //if (true) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        int ticks = (int) level.getGameTime() % Integer.MAX_VALUE;

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(true);

        RenderSystem.setShader(WeatherShaders.CustomRenderTypes::getCloudShader);

        ShaderInstanceExtended shaderinstance = WeatherShaders.getShaderExtended();
        if (shaderinstance.CUSTOM_TIME != null) {
            float smoothTicks = (ticks + partialTicks) * 0.005F;
            shaderinstance.CUSTOM_TIME.set((float)(smoothTicks % 360));
        }

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        FogRenderer.levelFogColor();
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //do things

        cloudsNeedUpdate = false;

        if (cloudsVBO == null || cloudsNeedUpdate || ticks % 40 == 0) {

            this.cloudsNeedUpdate = false;
            if (this.cloudsVBO != null) {
                this.cloudsVBO.close();
            }

            this.cloudsVBO = new VertexBuffer();

            Player player = Minecraft.getInstance().player;
            double playerX = player.getX();// - ((player.getPosX() - player.prevPosX) * partialTicks);
            //playerX = (MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX()) - player.getPosX());
            double playerY = player.getY();
            double playerZ = player.getZ();
            //playerZ = (MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ()) - player.getPosZ());

            double x = 5911 - (playerX);
            double y = 250 - viewEntityY + 0.001;
            double z = 6972 - (playerZ);

            x = 336 - (playerX);
            y = 128 - viewEntityY + 0.001;
            z = -50 - (playerZ);

            x = 0;
            y = 0;
            z = 0;

            rand = new Random(3333);
            rand2 = new Random(3333);

            double scalecluster = 2.0;

            int randRangeY = 40;
            int randRange = (int) (350 * scalecluster);

            int randRangeY2 = (int) (16 * scalecluster);
            int randRange2 = (int) (50 * scalecluster);

            int clusters = 30;
            int clusterDensity = (int) (75 * scalecluster);

            //bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

            //this.cloudsVBO.bind();
            //DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.setupBufferState();

            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);

            RenderSystem.colorMask(true, true, true, true);

            sphereIndex = 0;

            for (int i = 0; i < clusters; i++) {
                double xx = rand.nextInt(randRange) - rand.nextInt(randRange);
                double yy = rand.nextInt(randRangeY) - rand.nextInt(randRangeY);
                double zz = rand.nextInt(randRange) - rand.nextInt(randRange);

                float index = 0;
                float indexMax = 50;
                float tickShift = (((float)ticks * 0.05F)) % indexMax;
                for (int ii = 0; ii < clusterDensity; ii++) {
                    double xxx = rand.nextInt(randRange2) - rand.nextInt(randRange2);
                    double yyy = rand.nextInt(randRangeY2)/* - rand.nextInt(randRangeY2)*/;
                    //double yyy = rand.nextInt(randRange2) - rand.nextInt(randRange2);
                    double zzz = rand.nextInt(randRange2) - rand.nextInt(randRange2);

                    double subX = xxx+xx;
                    double subY = yyy+yy;
                    double subZ = zzz+zz;

                    double snapResPos = 10;
                    /*subX = Math.round(subX * snapResPos) / snapResPos;
                    subY = Math.round(subY * snapResPos) / snapResPos;
                    subZ = Math.round(subZ * snapResPos) / snapResPos;*/
                    subX = Math.round(subX / snapResPos) * snapResPos;
                    subY = Math.round(subY / snapResPos) * snapResPos;
                    subZ = Math.round(subZ / snapResPos) * snapResPos;

                    double vecX = (subX) - xx;
                    double vecY = (subY) - yy;
                    double vecZ = (subZ) - zz;
                    double dist = Math.sqrt(vecX * vecX + vecY * vecY + vecZ * vecZ);
                    double distPct = Math.max(1D - (dist / randRange2), 0.3D);

                    //cut off the lil stragglers for our current formula
                    if (dist > randRange2 / 1.5) {
                        continue;
                    }

                    //snap the scaling to specific sizes
                    double snapResScale = 4;
                    distPct = Math.round(distPct * snapResScale) / snapResScale;
                    //distPct = Math.round(distPct / snapResScale) * snapResScale;
                    //distPct = 0.3;

                    double r = 0.8 + rand.nextDouble() * 0.2;
                    r = 0.97 + (yyy / (randRangeY2 * 2)) * 0.03;
                    r = 0.9;
                    //r += rand.nextFloat() * 0.03F;
                    r += rand.nextFloat() * 0.08F;
                    r = Math.min(1F, r);
                    //double g = 0.8 + rand.nextDouble() * 0.2;
                    //double b = 0.8 + rand.nextDouble() * 0.2;
                    float scale = (float)((index*1F + tickShift) % indexMax) / (float)indexMax;
                    if (scale > 0.5F) {
                        scale = 0.5F - (scale-0.5F);
                    }
                    scale *= 5F;

                    scale = (float) (20F * (distPct));

                    //z fix
                    scale += rand.nextFloat() * 0.1F;


                    //renderSphere(bufferbuilder, x + xx + xxx, y + 10 + yy + yyy, z + zz + zzz, new Vector3d(r, r, r), scale);

                    //matrixStackIn.pushPose();
                    //matrixStackIn.translate(x + xx + xxx, y + 10 + yy + yyy, z + zz + zzz);
                        /*if (index % 2 == 0) matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((index + tickShift) * 2) % 3600));
                        if (index % 3 == 0) matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(((index + tickShift) * 5) % 3600));
                        if (index % 5 == 0) matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(((index + tickShift) * 7) % 3600));*/
                    //matrixStackIn.scale(scale, scale, scale);

                    //this.cloudsVBO.draw(matrixStackIn.last().pose(), GL11.GL_QUADS);
                    //ShaderInstance shaderinstance = RenderSystem.getShader();

                    //matrixStackIn.popPose();

                    //renderSphere(bufferbuilder, x + subX, y + 10 + subY, z + subZ, new Vec3(r * 0.9F, r * 0.9F, r * 0.9F), 0.4F);
                    //renderSphere(bufferbuilder, x + subX, y + 10 + subY, z + subZ, new Vec3(r, r, r), scale * 0.03F);
                    renderCube(bufferbuilder, x + subX, y + 10 + subY, z + subZ, new Vec3(r, r, r), scale);
                    index++;
                    sphereIndex++;
                }
            }

            bufferbuilder.end();
            this.cloudsVBO.upload(bufferbuilder);
        }

        matrixStackIn.pushPose();/*
        double playerX = player.getX();
        double playerY = player.getY();
        double playerZ = player.getZ();*/
        double x = 336 - (viewEntityX);
        double y = 180 - viewEntityY + 0.001;
        double z = -50 - (viewEntityZ);
        matrixStackIn.translate(x, y, z);
        this.cloudsVBO.drawWithShader(matrixStackIn.last().pose(), projectionMatrix, shaderinstance);
        matrixStackIn.popPose();

        //RenderSystem.disableAlphaTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public void renderOld(PoseStack matrixStackIn, Matrix4f projectionMatrix, float p_172957_, double p_172958_, double viewEntityY, double p_172960_) {

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        int ticks = (int) level.getGameTime() % Integer.MAX_VALUE;

        boolean customShader = true;

        int method = 3;
        if (method == 3) {

            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.depthMask(true);
            //RenderSystem.enableFog();
            BufferBuilder buffer = Tesselator.getInstance().getBuilder();

            //Minecraft.getInstance().getTextureManager().bindForSetup(TextureAtlas.LOCATION_PARTICLES);
            if (!customShader) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            } else {
                RenderSystem.setShader(WeatherShaders.CustomRenderTypes::getCloudShader);
            }

            ShaderInstanceExtended shaderinstance = WeatherShaders.getShaderExtended();
            if (shaderinstance.CUSTOM_TIME != null) {
                shaderinstance.CUSTOM_TIME.set(2F);
            }

            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            //WeatherShaders.clouds(TextureAtlas.LOCATION_PARTICLES);

            FogRenderer.levelFogColor();
            //GlStateManager.disableTexture();

            //this.cloudsNeedUpdate = true;
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

            boolean force = true;

            /*if (this.cloudVBO == null || force) {
                if (this.cloudVBO != null) {
                    this.cloudVBO.close();
                }
                this.cloudVBO = new VertexBuffer();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
                renderSphere(bufferbuilder, 0, 0, 0, new Vec3(1, 1, 1), 0.1F);
                bufferbuilder.end();
                this.cloudVBO.upload(bufferbuilder);
            }*/

            boolean uhh = true;
            this.cloudsNeedUpdate = true;

            if (uhh && this.cloudsNeedUpdate) {
                this.cloudsNeedUpdate = false;
                if (this.cloudsVBOOld != null) {
                    this.cloudsVBOOld.close();
                }

                this.cloudsVBOOld = new VertexBuffer();

                //this.drawClouds(bufferbuilder, d2, d3, d4, vector3d);
                Player player = Minecraft.getInstance().player;
                double playerX = player.getX();// - ((player.getPosX() - player.prevPosX) * partialTicks);
                //playerX = (MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX()) - player.getPosX());
                double playerY = player.getY();
                double playerZ = player.getZ();
                //playerZ = (MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ()) - player.getPosZ());

                double x = 5911 - (playerX);
                double y = 128 - viewEntityY + 0.001;
                double z = 6972 - (playerZ);

                Random rand = new Random(3);

                int randRangeY = 40;
                int randRange = 250;

                int randRangeY2 = 16;
                int randRange2 = 20;

                int clusters = 50;
                int clusterDensity = 50;

                if (!customShader) {
                    RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
                } else {
                    RenderSystem.setShader(WeatherShaders.CustomRenderTypes::getCloudShader);
                }
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);

                for (int i = 0; i < 1; i++) {
                    double xx = rand.nextInt(randRange) - rand.nextInt(randRange);
                    double yy = rand.nextInt(randRangeY) - rand.nextInt(randRangeY);
                    double zz = rand.nextInt(randRange) - rand.nextInt(randRange);

                    float index = 0;
                    float indexMax = clusters;
                    float tickShift = (((float)ticks * 0.05F)) % indexMax;
                    for (int ii = 0; ii < 1; ii++) {
                        double xxx = rand.nextInt(randRange2) - rand.nextInt(randRange2);
                        double yyy = rand.nextInt(randRangeY2) - rand.nextInt(randRangeY2);
                        double zzz = rand.nextInt(randRange2) - rand.nextInt(randRange2);
                        double r = 0.8 + rand.nextDouble() * 0.2;
                        r = 0.7 + (yyy / (randRangeY2 * 2)) * 0.3;
                        r = 0.8;
                        //double g = 0.8 + rand.nextDouble() * 0.2;
                        //double b = 0.8 + rand.nextDouble() * 0.2;
                        float scale = (float)((index*1F + tickShift) % indexMax) / (float)indexMax;
                        //renderSphere(bufferbuilder, x + xx + xxx, y + 10 + yy + yyy, z + zz + zzz, new Vector3d(r, r, r), scale);
                        renderSphere(bufferbuilder, 0, 0, 0, new Vec3(r, r, r), 0.3F);
                        index++;
                    }
                }

                bufferbuilder.end();
                this.cloudsVBOOld.upload(bufferbuilder);
            }

            if (!customShader) {
                RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            } else {
                RenderSystem.setShader(WeatherShaders.CustomRenderTypes::getCloudShader);
            }
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            //WeatherShaders.clouds(TextureAtlas.LOCATION_PARTICLES);
            FogRenderer.levelFogColor();

            /*matrixStackIn.push();*/
            //matrixStackIn.scale(12.0F, 1.0F, 12.0F);
            //matrixStackIn.translate((double)(-f3), (double)f4, (double)(-f5));
            if (this.cloudsVBOOld != null && uhh && false) {
                /*this.cloudsVBO.bind();
                DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.setupBufferState(0L);

                this.cloudsVBO.draw(matrixStackIn.last().pose(), GL11.GL_QUADS);

                VertexBuffer.unbind();
                DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.clearBufferState();*/

                RenderSystem.colorMask(true, true, true, true);

                //ShaderInstance shaderinstance = RenderSystem.getShader();
                this.cloudsVBOOld.drawWithShader(matrixStackIn.last().pose(), projectionMatrix, shaderinstance);
            }

            /*matrixStackIn.pop();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableAlphaTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            RenderSystem.disableFog();*/

            boolean vboEachCloud = true;

            if (vboEachCloud) {
                Player player = Minecraft.getInstance().player;
                double playerX = player.getX();// - ((player.getPosX() - player.prevPosX) * partialTicks);
                //playerX = (MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX()) - player.getPosX());
                double playerY = player.getY();
                double playerZ = player.getZ();
                //playerZ = (MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ()) - player.getPosZ());

                double x = 5911 - (playerX);
                double y = 250 - viewEntityY + 0.001;
                double z = 6972 - (playerZ);

                x = 336 - (playerX);
                y = 128 - viewEntityY + 0.001;
                z = -50 - (playerZ);

                Random rand = new Random(3);

                int randRangeY = 40;
                int randRange = 250;

                int randRangeY2 = 16;
                int randRange2 = 20;

                int clusters = 50;
                int clusterDensity = 50;

                //bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

                //this.cloudsVBO.bind();
                //DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.setupBufferState();

                RenderSystem.colorMask(true, true, true, true);

                for (int i = 0; i < clusters; i++) {
                    double xx = rand.nextInt(randRange) - rand.nextInt(randRange);
                    double yy = rand.nextInt(randRangeY) - rand.nextInt(randRangeY);
                    double zz = rand.nextInt(randRange) - rand.nextInt(randRange);

                    float index = 0;
                    float indexMax = 50;
                    float tickShift = (((float)ticks * 0.05F)) % indexMax;
                    for (int ii = 0; ii < clusterDensity; ii++) {
                        double xxx = rand.nextInt(randRange2) - rand.nextInt(randRange2);
                        double yyy = rand.nextInt(randRangeY2)/* - rand.nextInt(randRangeY2)*/;
                        double zzz = rand.nextInt(randRange2) - rand.nextInt(randRange2);
                        double r = 0.8 + rand.nextDouble() * 0.2;
                        r = 0.7 + (yyy / (randRangeY2 * 2)) * 0.3;
                        //double g = 0.8 + rand.nextDouble() * 0.2;
                        //double b = 0.8 + rand.nextDouble() * 0.2;
                        float scale = (float)((index*1F + tickShift) % indexMax) / (float)indexMax;
                        if (scale > 0.5F) {
                            scale = 0.5F - (scale-0.5F);
                        }
                        scale *= 2F;
                        //renderSphere(bufferbuilder, x + xx + xxx, y + 10 + yy + yyy, z + zz + zzz, new Vector3d(r, r, r), scale);

                        matrixStackIn.pushPose();
                        matrixStackIn.translate(x + xx + xxx, y + 10 + yy + yyy, z + zz + zzz);
                        /*if (index % 2 == 0) matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(((index + tickShift) * 2) % 3600));
                        if (index % 3 == 0) matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(((index + tickShift) * 5) % 3600));
                        if (index % 5 == 0) matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(((index + tickShift) * 7) % 3600));*/
                        matrixStackIn.scale(scale, scale, scale);

                        //this.cloudsVBO.draw(matrixStackIn.last().pose(), GL11.GL_QUADS);
                        //ShaderInstance shaderinstance = RenderSystem.getShader();
                        this.cloudsVBOOld.drawWithShader(matrixStackIn.last().pose(), projectionMatrix, shaderinstance);

                        matrixStackIn.popPose();

                        index++;
                    }
                }

                //VertexBuffer.unbind();
                //DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.clearBufferState();

                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                //RenderSystem.disableAlphaTest();
                RenderSystem.enableCull();
                RenderSystem.disableBlend();
                //RenderSystem.disableFog();
            }
        }
    }

    private void renderCube(BufferBuilder bufferIn, double cloudsX, double cloudsY, double cloudsZ, Vec3 cloudsColor, float scale) {
        Random rand = rand2;

        Quaternion q2 = new Quaternion(0, 0, 0, 1);
        int range = 5;
        range = 180;
        q2.mul(Vector3f.XP.rotationDegrees(rand.nextInt(range)));
        q2.mul(Vector3f.YP.rotationDegrees(rand.nextInt(range)));
        //q2.mul(Vector3f.YP.rotationDegrees(rand.nextInt(45)));
        q2.mul(Vector3f.ZP.rotationDegrees(rand.nextInt(range)));

        boolean randRotate = false;

        for (Direction dir : Direction.values()) {
            Quaternion quaternion = dir.getRotation();

            Vector3f[] avector3f3 = new Vector3f[]{
                    new Vector3f(-1.0F, 0.0F, -1.0F),
                    new Vector3f(-1.0F, 0.0F, 1.0F),
                    new Vector3f(1.0F, 0.0F, 1.0F),
                    new Vector3f(1.0F, 0.0F, -1.0F)};


            Vector3f normal = new Vector3f(dir.getNormal().getX(), dir.getNormal().getY(), dir.getNormal().getZ());
            if (randRotate) normal.transform(q2);
            float normalRange = 0.1F;
            normal.mul(normalRange);
            //normal.add(1-normalRange, 1-normalRange, 1-normalRange);
            float uh = 0.55F;
            normal.add(uh, uh, uh);

            for(int i = 0; i < 4; ++i) {
                Vector3f vector3f = avector3f3[i];
                vector3f.transform(quaternion);
                vector3f.add((float) dir.getStepX(), (float) dir.getStepY(), (float) dir.getStepZ());
                if (randRotate) vector3f.transform(q2);
                vector3f.mul(scale);
                vector3f.add((float) cloudsX + 0.5F, (float) cloudsY, (float) cloudsZ + 0.5F);
            }

            TextureAtlasSprite sprite = ParticleRegistry.cloud_square;

            float f7 = sprite.getU0();
            float f8 = sprite.getU1();
            float f5 = sprite.getV0();
            float f6 = sprite.getV1();


            float particleRed = (float) cloudsColor.x;
            float particleGreen = (float) cloudsColor.y;
            float particleBlue = (float) cloudsColor.z;
            float particleAlpha = 1;

            bufferIn.vertex(avector3f3[0].x(), avector3f3[0].y(), avector3f3[0].z()).uv(f8, f6).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(normal.x(), normal.y(), normal.z()).endVertex();
            bufferIn.vertex(avector3f3[1].x(), avector3f3[1].y(), avector3f3[1].z()).uv(f8, f5).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(normal.x(), normal.y(), normal.z()).endVertex();
            bufferIn.vertex(avector3f3[2].x(), avector3f3[2].y(), avector3f3[2].z()).uv(f7, f5).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(normal.x(), normal.y(), normal.z()).endVertex();
            bufferIn.vertex(avector3f3[3].x(), avector3f3[3].y(), avector3f3[3].z()).uv(f7, f6).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(normal.x(), normal.y(), normal.z()).endVertex();

        }

    }

    private void renderSphere(BufferBuilder bufferIn, double cloudsX, double cloudsY, double cloudsZ, Vec3 cloudsColor, float scale) {
        TextureAtlasSprite sprite = ParticleRegistry.cloud_square;

        float f7 = sprite.getU0();
        float f8 = sprite.getU1();
        float f5 = sprite.getV0();
        float f6 = sprite.getV1();

        float uRange = f8 - f7;
        float yRange = f6 - f5;

        float particleRed = (float) cloudsColor.x;
        float particleGreen = (float) cloudsColor.y;
        float particleBlue = (float) cloudsColor.z;
        float particleAlpha = 1;

        boolean alt = false;
        Random rand = new Random(555);

        int nSegments = 6;
        int nSlices = 6;
        float radius = 20;

        int iter = 0;
        int iter2 = 0;

        float lengthInv = 1.0f / radius;

        rand = new Random(555);

        for (double slice = 1.0; slice <= nSlices/* && iter <= 100*/; slice += 1.0) {
            double lat0 = Math.PI * (((slice - 1) / nSlices) - 0.5);
            double z0 = Math.sin(lat0);
            double zr0 = Math.cos(lat0);

            double lat1 = Math.PI * ((slice / nSlices) - 0.5);
            double z1 = Math.sin(lat1);
            double zr1 = Math.cos(lat1);

            //glBegin(GL_QUADS);
            //bufferIn.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            for (double segment = 0.0; segment < nSegments/* && iter2 <= 408*/; segment += 1.0) {
                double long0 = 2 * Math.PI * ((segment -1 ) / nSegments);
                double x0 = Math.cos(long0);
                double y0 = Math.sin(long0);

                double long1 = 2 * Math.PI * (segment / nSegments);
                double x1 = Math.cos(long1);
                double y1 = Math.sin(long1);

                /*glVertex3f(x0 * zr0, y0 * zr0, z0);
                glVertex3f(x1 * zr1, y1 * zr1, z0);
                glVertex3f(x0 * zr0, y0 * zr0, z1);
                glVertex3f(x1 * zr1, y1 * zr1, z1);*/

                /*bufferIn.pos(x0 * zr0 * radius + cloudsX, y0 * zr0 * radius + cloudsY, z0 * radius + cloudsZ).tex(f8, f6).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(1, 1, 1).endVertex();;
                bufferIn.pos(x1 * zr1 * radius + cloudsX, y1 * zr1 * radius + cloudsY, z0 * radius + cloudsZ).tex(f8, f5).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(1, 1, 1).endVertex();
                bufferIn.pos(x0 * zr0 * radius + cloudsX, y0 * zr0 * radius + cloudsY, z1 * radius + cloudsZ).tex(f7, f5).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(1, 1, 1).endVertex();
                bufferIn.pos(x1 * zr1 * radius + cloudsX, y1 * zr1 * radius + cloudsY, z1 * radius + cloudsZ).tex(f7, f6).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(1, 1, 1).endVertex();*/

                /*particleRed = rand.nextFloat();
                particleGreen = rand.nextFloat();
                particleBlue = rand.nextFloat();*/
                particleAlpha = 1F;
                float radius2 = radius;
                if (scale > 0.5F) {
                    float scale2 = (1F - scale) * 2F;
                    radius2 = radius * scale2;//(1F - scale2);
                    //particleAlpha = scale2;
                } else {
                    radius2 = radius * scale * 2F;
                }

                //particleAlpha = 0.9F;

                float n1 = 1;
                float n2 = 1;
                float n3 = 1;

                double x = x1 * zr0 * radius2;
                double y = y1 * zr0 * radius2;
                double z = z0 * radius2;

                float randAmt = 2F;
                /*double randX = (rand.nextFloat() - rand.nextFloat()) * randAmt;
                double randY = (rand.nextFloat() - rand.nextFloat()) * randAmt;
                double randZ = (rand.nextFloat() - rand.nextFloat()) * randAmt;*/


                float extraLight = 0.3F;

                n1 = (float) (x * lengthInv) * extraLight + (1F-extraLight);
                n2 = (float) (y * lengthInv) * extraLight + (1F-extraLight);
                n3 = (float) (z * lengthInv) * extraLight + (1F-extraLight);

                bufferIn.vertex(x1 * zr0 * radius2 + ((new Random((long) (x1 * zr0 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsX, y1 * zr0 * radius2 + ((new Random((long) (y1 * zr0 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsY, z0 * radius2 + ((new Random((long) (z0 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsZ).uv(f7, f5).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(n1, n2, n3).endVertex();
                bufferIn.vertex(x0 * zr0 * radius2 + ((new Random((long) (x0 * zr0 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsX, y0 * zr0 * radius2 + ((new Random((long) (y0 * zr0 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsY, z0 * radius2 + ((new Random((long) (z0 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsZ).uv(f8, f6).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(n1, n2, n3).endVertex();;
                bufferIn.vertex(x0 * zr1 * radius2 + ((new Random((long) (x0 * zr1 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsX, y0 * zr1 * radius2 + ((new Random((long) (y0 * zr1 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsY, z1 * radius2 + ((new Random((long) (z1 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsZ).uv(f8, f5).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(n1, n2, n3).endVertex();
                bufferIn.vertex(x1 * zr1 * radius2 + ((new Random((long) (x1 * zr1 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsX, y1 * zr1 * radius2 + ((new Random((long) (y1 * zr1 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsY, z1 * radius2 + ((new Random((long) (z1 * radius2) + sphereIndex)).nextFloat() * randAmt - (randAmt / 2F)) + cloudsZ).uv(f7, f6).color(particleRed, particleGreen, particleBlue, particleAlpha).normal(n1, n2, n3).endVertex();

                iter2++;
            }

            /*Tessellator tessellator = Tessellator.getInstance();
            tessellator.draw();*/

            iter++;

            //glEnd();
        }
    }
}
