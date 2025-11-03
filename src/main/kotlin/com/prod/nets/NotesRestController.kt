package com.prod.nets

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/notes")
class NotesRestController(private val notesService: NotesService) {
    @GetMapping
    fun getAllNotes(): ResponseEntity<List<Note>> {
        return ResponseEntity.ok(notesService.getAllNotes())
    }

    @GetMapping("/{id}")
    fun getNoteById(@PathVariable id: UUID): ResponseEntity<Note> {
        return notesService.getById(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createNote(@RequestBody noteDTO: NoteDTO): ResponseEntity<Any> {
        return try {
            val createdNote = notesService.createNote(noteDTO)
            ResponseEntity.status(HttpStatus.CREATED).body(createdNote)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PutMapping("/{id}")
    fun updateNote(@PathVariable id: UUID, @RequestBody noteDTO: NoteDTO): ResponseEntity<Any> {
        return notesService.updateById(id, noteDTO)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteNote(@PathVariable id: UUID): ResponseEntity<Any> {
        return notesService.deleteById(id)?.let {
            ResponseEntity.ok(mapOf("message" to "Note deleted successfully", "deletedNote" to it))
        } ?: ResponseEntity.notFound().build()
    }
}
