package com.prod.nets

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotesService(private val notesRepository: NotesRepository) {
    fun getAllNotes(): List<Note> {
        return notesRepository.findAll().map { it }
    }

    fun getById(id: UUID): Note? {
        return notesRepository.findByIdOrNull(id)
    }

    fun createNote(state: NoteDTO): Note {
        if (state.title == null || state.content == null) {
            throw IllegalArgumentException("Requested to create note with either title or content null")
        }
        val note = Note(UUID.randomUUID(), state.title, state.content)
        notesRepository.save(note)
        return note
    }

    fun updateById(id: UUID, newState: NoteDTO): Note? {
        return notesRepository.findByIdOrNull(id)?.let { note: Note ->
            note.title = newState.title ?: note.title
            note.content = newState.content ?: note.content
            notesRepository.save(note)
            note
        }
    }

    fun deleteById(id: UUID): Note? {
        return notesRepository.findByIdOrNull(id)?.apply { notesRepository.deleteById(id) }
    }
}