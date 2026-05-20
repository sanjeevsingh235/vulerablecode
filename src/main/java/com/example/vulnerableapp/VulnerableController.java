package com.example.vulnerableapp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VulnerableController {

    private static final Logger logger = LoggerFactory.getLogger(VulnerableController.class);

    private static final String ADMIN_PASSWORD = "admin123";
    private static final String API_KEY = "sk_test_51HardcodedSecretValue";
    private static final String AES_KEY = "1234567890123456";

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    public VulnerableController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        logger.info("Login attempt username={} password={}", username, password);

        String sql = "SELECT COUNT(*) FROM users WHERE username = '" + username
                + "' AND password = '" + password + "'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        if (count != null && count > 0 || ADMIN_PASSWORD.equals(password)) {
            return ResponseEntity.ok("Welcome " + username + ", token=" + API_KEY);
        }
        return ResponseEntity.status(401).body("Invalid login");
    }

    @GetMapping("/file")
    public ResponseEntity<String> readFile(@RequestParam String name) throws IOException {
        File file = new File("uploads/" + name);
        return ResponseEntity.ok(Files.readString(file.toPath()));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping(@RequestParam String host) throws IOException {
        Process process = Runtime.getRuntime().exec("ping -c 1 " + host);
        return ResponseEntity.ok("Command started: " + process.pid());
    }

    @PostMapping("/hash")
    public ResponseEntity<Map<String, String>> hash(@RequestBody Map<String, String> body)
            throws NoSuchAlgorithmException {
        String value = body.getOrDefault("value", "");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(value.getBytes(StandardCharsets.UTF_8));

        Map<String, String> response = new HashMap<>();
        response.put("algorithm", "MD5");
        response.put("hash", bytesToHex(digest));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestBody String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok(bytesToHex(encrypted));
    }

    @GetMapping("/otp")
    public ResponseEntity<String> otp() {
        int otp = 100000 + random.nextInt(900000);
        return ResponseEntity.ok(String.valueOf(otp));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
