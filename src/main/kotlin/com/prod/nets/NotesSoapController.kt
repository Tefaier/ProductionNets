package com.prod.nets

import com.prod.nets.soap.*
import org.springframework.ws.server.endpoint.annotation.Endpoint
import org.springframework.ws.server.endpoint.annotation.PayloadRoot
import org.springframework.ws.server.endpoint.annotation.RequestPayload
import org.springframework.ws.server.endpoint.annotation.ResponsePayload
import java.util.*

@Endpoint
class NotesSoapController(private val notesService: NotesService) {
    private companion object {
        const val NAMESPACE_URI = "http://example.local/soap"

        private fun noteConversion(note: Note): com.prod.nets.soap.Note {
            val newNote = Note()
            newNote.id = note.id.toString()
            newNote.title = note.title
            newNote.content = note.content
            return newNote
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllNotesRequest")
    @ResponsePayload
    fun getAllNotes(@RequestPayload request: GetAllNotesRequest): GetAllNotesResponse {
        val response = GetAllNotesResponse()
        val list = NoteList()
        list.note.addAll(notesService.getAllNotes().map(NotesSoapController::noteConversion))
        response.notes = list
        return response
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNoteByIdRequest")
    @ResponsePayload
    fun getNoteById(@RequestPayload request: GetNoteByIdRequest): GetNoteByIdResponse {
        val response = GetNoteByIdResponse()
        val note = notesService.getById(UUID.fromString(request.id))
        if (note != null) {
            response.note = noteConversion(note)
        }
        return response
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createNoteRequest")
    @ResponsePayload
    fun createNote(@RequestPayload request: CreateNoteRequest): CreateNoteResponse {
        val response = CreateNoteResponse()
        return try {
            val noteDTO = NoteDTO(
                title = request.title,
                content = request.content
            )
            val createdNote = notesService.createNote(noteDTO)
            response.note = noteConversion(createdNote)
            response.status = "CREATED"
            response
        } catch (e: IllegalArgumentException) {
            response.status = "ERROR"
            response.errorMessage = e.message
            response
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateNoteRequest")
    @ResponsePayload
    fun updateNote(@RequestPayload request: UpdateNoteRequest): UpdateNoteResponse {
        val response = UpdateNoteResponse()
        val noteDTO = NoteDTO(
            title = request.title,
            content = request.content
        )
        val updatedNote = notesService.updateById(UUID.fromString(request.id), noteDTO)
        if (updatedNote != null) {
            response.note = noteConversion(updatedNote)
            response.status = "UPDATED"
        } else {
            response.status = "NOT_FOUND"
        }
        return response
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteNoteRequest")
    @ResponsePayload
    fun deleteNote(@RequestPayload request: DeleteNoteRequest): DeleteNoteResponse {
        val response = DeleteNoteResponse()
        val deletedNote = notesService.deleteById(UUID.fromString(request.id))
        if (deletedNote != null) {
            response.deletedNote = noteConversion(deletedNote)
            response.status = "DELETED"
            response.message = "Note deleted successfully"
        } else {
            response.status = "NOT_FOUND"
        }
        return response
    }
}