package com.rc.eav.Controller;


import com.rc.eav.utils.Globals;
import com.rc.eav.utils.KeyValueClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class KVController {

    @Autowired
    Globals globals;

    @PostMapping("/api/set")
    public ResponseEntity<String> set(@RequestParam(required = true) String key, @RequestParam(required = true) String value) {
        try (KeyValueClient client = new KeyValueClient(globals.getKvServerIp(), globals.getKvServerPort())) {
            return ResponseEntity.ok(client.set(key, value));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<String> get(@RequestParam String key) {
        try (KeyValueClient client = new KeyValueClient(globals.getKvServerIp(), globals.getKvServerPort())) {
            return ResponseEntity.ok(client.get(key));
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
