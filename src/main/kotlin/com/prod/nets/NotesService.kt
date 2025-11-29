package com.prod.nets

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.UUID

@Service
class NotesService(
    @Autowired private val notesRepository: NotesRepository,
    @Autowired private val requestTemplate: RestTemplate,
    @Value("\${mail.sender.address}") private val mailHost: String
) {
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
        sendInfo(SendRequest(UpdateType.CREATE, note.title, null, note.content, null))
        return note
    }

    fun updateById(id: UUID, newState: NoteDTO): Note? {
        return notesRepository.findByIdOrNull(id)?.let { note: Note ->
            val prevTitle = note.title
            val prevContent = note.content
            note.title = newState.title ?: note.title
            note.content = newState.content ?: note.content
            notesRepository.save(note)
            sendInfo(SendRequest(UpdateType.UPDATE, note.title, prevTitle, note.content, prevContent))
            note
        }
    }

    fun deleteById(id: UUID): Note? {
        return notesRepository.findByIdOrNull(id)?.apply {
            notesRepository.deleteById(id)
            sendInfo(SendRequest(UpdateType.DELETE, this.title, null, this.content, null))
        }
    }

    private fun sendInfo(request: SendRequest) {
        try {
            requestTemplate.postForLocation("http://$mailHost/api/send", request)
        } catch (_: Exception) {

        }
    }
}