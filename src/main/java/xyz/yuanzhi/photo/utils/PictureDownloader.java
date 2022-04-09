package xyz.yuanzhi.photo.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class PictureDownloader {

    public static class PictureData {
        public String url;
        public Identifier identifier;
        public Integer width; //px
        public Integer height; //px

        public PictureData(String url) {
            this.url = url;
        }
    }
    static PictureDownloader downloader = new PictureDownloader();

    public static PictureDownloader getInstance() {
        return downloader;
    }

    // Create a service for downloading the picture
    private final ExecutorService service = newFixedThreadPool(4);

    private final Hashtable<String, PictureData> cache = new Hashtable<>();

    private final Object mutex = new Object();

    // Downloads the picture, or returns the cached picture
    public PictureData getPicture(String url) {
        synchronized (mutex) {
            // Try to get the picture from cache
            PictureData data = this.cache.get(url);
            if (data == null) {
                // Download the picture if not in cache
                this.downloadPicture(url);
                return null;
            }

            if (data.identifier == null) {
                return null;
            }

            return data;
        }
    }

    // Download the image and save it in cache
    private void downloadPicture(String url) {
        this.cache.put(url, new PictureData(url));

        service.submit(() -> {
            try {
                BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
                File file = File.createTempFile(".tempphoto", "temp");
                file.deleteOnExit();

                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    out.write(dataBuffer, 0, bytesRead);
                }

                out.close();

                // Convert to png
                BufferedImage bufferedImage = ImageIO.read(file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

                InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

                NativeImage nativeImage = NativeImage.read(inputStream);
                NativeImageBackedTexture nativeImageBackedTexture = new NativeImageBackedTexture(nativeImage);

                Identifier texture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("photo/image",
                        nativeImageBackedTexture);

                // Cache the downloaded picture
                synchronized (mutex) {
                    PictureData data = this.cache.get(url);
                    data.identifier = texture;
                    data.height = bufferedImage.getHeight();
                    data.width = bufferedImage.getWidth();
                }

            } catch (IOException error) {
                error.printStackTrace();
            }
        });
    }
}