package com.prod.nets

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {
    @GetMapping("/ping")
    fun getAllNotes(): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }
}