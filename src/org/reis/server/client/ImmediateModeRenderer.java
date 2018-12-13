package org.reis.server.client;

import org.joml.Matrix4d;
import org.lwjgl.opengl.EXTBGRA;
import org.lwjgl.opengl.GL;
import org.reis.game.Block;
import org.reis.game.Player;
import org.reis.game.World;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

class ImmediateModeRenderer {
    private double[] eyes = new double[4*4];
    private double[] ui = new double[4*4];

    private class Texture2D {
        int texture, width, height;

        void bind() {
            glBindTexture(GL_TEXTURE_2D, texture);
        }

        Texture2D(String file, int format) throws IOException {
            texture = glGenTextures();
            bind();
            glPixelStorei(GL_PACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            InputStream in = new FileInputStream(file);
            BufferedImage image = ImageIO.read(in);
            in.close();
            width = image.getWidth();
            height = image.getHeight();
            ByteBuffer buffer = memAlloc(width * height * 4);
            buffer.asIntBuffer().put(image.getRGB(0, 0, width, height, new int[width * height], 0, width));
            buffer.flip();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, format, GL_UNSIGNED_BYTE, buffer);
            memFree(buffer);
        }
    }

    private class Atlas extends Texture2D {
        int sub_width, sub_height, padding;

        Atlas(String file, int format, int sub_width, int sub_height, int padding) throws IOException {
            super(file, format);
            this.sub_width = sub_width;
            this.sub_height = sub_height;
            this.padding = padding;
        }
    }

    private Atlas font, face;

    ImmediateModeRenderer() {
        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1.0);

        new Matrix4d()
            .setPerspective(60.0 * Math.PI/180, 640.0/480, 1.0/16, 16*16)
            .get(eyes);

        new Matrix4d()
            .setOrtho(0, 640, 480, 0, 0, 1)
            .get(ui);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        try {
            font = new Atlas("./assets/gohu.bmp", EXTBGRA.GL_BGRA_EXT, 8, 14, 2);
            face = new Atlas("./assets/doku.bmp", EXTBGRA.GL_BGRA_EXT, 16, 16, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    void render(Player player) {
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixd(eyes);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glMultMatrixd( new Matrix4d()
            .translationRotateScaleInvert(
                player.getPosition(),
                player.getOrientation(),
                1.0
            )
            // .add(new Matrix4d().set(DoubleStream.generate(() -> Math.random() / 256.0).limit(16).toArray()))
            .get(new double[4*4])
        );
        render(player.getWorld());
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    void render(World world) {
        render(world.getBlocks());
    }

    private void render(Block[][][] blocks) {
        for (int z = 0; z < blocks.length; z++)
        for (int y = 0; y < blocks[z].length; y++)
        for (int x = 0; x < blocks[z][y].length; x++) {
            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glTranslated(x, y, z);
            if(blocks[z][y][x] != null)
                render(blocks[z][y][x]);
            glMatrixMode(GL_MODELVIEW);
            glPopMatrix();
        }
    }

    private void render(Block block) {

        /*
        glBegin(GL_QUADS);
        for (int f = 0; f < Block.faces.length; f++)
        for (int v = 0; v < Block.faces[f].length; v++) {
            int i = 0x111111 * block.id;
            glColor3ub((byte)(i & 0xFF),(byte)(i & 0xFF00 >> 0x8),(byte)(i & 0xFF0000 >> 0xF));
            glVertex3iv(Block.vertices[Block.faces[f][v]]);
        }
        glEnd();
        */

        double[][] vertices = new double[4][3];
        for (int f = 0; f < Block.faces.length; f++) {
            for(int v=0; v<vertices.length; v++)
            for(int p=0; p<vertices[v].length; p++)
                vertices[v][p] = Block.vertices[Block.faces[f][v]][p];
            render(face, block.id, vertices);
        }

    }

    void render(String text) {
        render(text.getBytes(StandardCharsets.ISO_8859_1), 64);
    }

    private void render(byte[] text, int text_width) {
        glMatrixMode(GL_PROJECTION);
        glLoadMatrixd(ui);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_BLEND);

        glDisable(GL_DEPTH_TEST);

        glColor3d(1, 1, 1);

        for (
            int c=0, x=0, y=0;
            c < text.length;
            c = c+1,
            x = (x+1)%text_width,
            y = y+(x==0?1:0)
        ) {
            int letter = text[c] & 0xFF;
            if (letter == "\n".getBytes(StandardCharsets.ISO_8859_1)[0]) {
                x=text_width-1;
                continue;
            }
            if (letter >= 0x20) letter -= 0x20;
            if (letter >= 0x81) letter -= 0x21;

            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();

            glTranslated(
                x * (font.sub_width + font.padding),
                y * (font.sub_height + font.padding),
                0
            );

            render(font, letter);

            glMatrixMode(GL_MODELVIEW);
            glPopMatrix();
        }

        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glDisable(GL_BLEND);
    }

    private void render(Atlas atlas, int sub_texture) {
        double w = atlas.sub_width;
        double h = atlas.sub_height;
        render(atlas, sub_texture, new double[][]{{0,0,0},{0,h,0},{w,h,0},{w,0,0}});
    }

    private void render(Atlas atlas, int sub_texture, double[][] vertices) {
        int atlas_sub_texture_width = (atlas.width - atlas.padding) / (atlas.sub_width + atlas.padding);
        // int atlas_sub_texture_height = (atlas.height - atlas.padding) / (atlas.sub_height + atlas.padding);

        double x = atlas.padding + (sub_texture % atlas_sub_texture_width) * (atlas.sub_width + atlas.padding);
        double y = atlas.padding + (sub_texture / atlas_sub_texture_width) * (atlas.sub_height + atlas.padding);

        render(atlas, x, y, atlas.sub_width, atlas.sub_height, vertices);
    }

    private void render( Texture2D texture,
        double x, double y,
        double region_width, double region_height,
        double[][] vertices
    ) {
        double width = region_width / texture.width;
        double height = region_height / texture.height;
        glEnable(GL_TEXTURE_2D);
        texture.bind();

        glMatrixMode(GL_TEXTURE);
        glPushMatrix();
        glLoadIdentity();
        glTranslated(x / texture.width, y / texture.height, 0);

        glBegin(GL_QUADS);
        glTexCoord2d(0, 0);
        glVertex3dv(vertices[0]);
        glTexCoord2d(0, height);
        glVertex3dv(vertices[1]);
        glTexCoord2d(width, height);
        glVertex3dv(vertices[2]);
        glTexCoord2d(width, 0);
        glVertex3dv(vertices[3]);
        glEnd();

        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
    }
}