package org.xorcipher;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        modifyJsonFileInZip("C:\\Users\\vnaumkin\\Desktop\\Packet.zip");
//        cipherJsonFile(Paths.get("C:\\Users\\vnaumkin\\Desktop\\test.json"));
//        cipherJsonFile(Paths.get("C:\\Users\\vnaumkin\\Downloads\\NaumkinTest (1)\\55b972d3-7f5b-4c75-ae46-b067fa27609b_1.0.0\\workspace\\Packet0.json"));
    }

    public static void cipherJsonFile(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            String s = new String(bytes, StandardCharsets.UTF_8);
            String encrypt = XorCipherService.encrypt(s);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(path), StandardCharsets.UTF_8))) {
                bw.write(encrypt);
                bw.flush();
            }
            String decrypt = XorCipherService.encrypt(encrypt);
            System.out.println(path + " " + s.equals(decrypt));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cipherJsonObject(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof JSONObject json) {
                cipherJsonObject(json);
            } else if (value instanceof JSONArray array) {
                cipherJsonArray(array);
            } else if (value instanceof String valStr){
                XorCipherService.encrypt(valStr);
                jsonObject.put(entry.getKey(), XorCipherService.encrypt(valStr));
            }
        }
    }

    public static void cipherJsonArray(JSONArray array) {
        for (Object o : array) {
            if (o instanceof JSONObject json) {
                cipherJsonObject(json);
            } else if (o instanceof JSONArray jsonArray) {
                cipherJsonArray(jsonArray);
            }
        }
    }

    static void modifyJsonFileInZip(String zipPath) throws IOException {
        Path zipFilePath = Paths.get(zipPath);
        try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, new HashMap<>());
             BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(zipFilePath));
             ZipInputStream zis = new ZipInputStream(bis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (!entry.isDirectory()) {
                    Path path = fs.getPath(name);
                    cipherJsonFile(path);
                }
            }
        }
    }
}