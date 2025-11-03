package com.prod.nets

import org.springframework.ws.server.endpoint.annotation.Endpoint
import org.springframework.ws.server.endpoint.annotation.PayloadRoot
import org.springframework.ws.server.endpoint.annotation.RequestPayload
import org.springframework.ws.server.endpoint.annotation.ResponsePayload

@Endpoint
class NotesSoapController(private val notesService: NotesService) {

    private companion object {
        const val NAMESPACE_URI = "http://example.com/notes/soap"
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllNotesRequest")
    @ResponsePayload
    fun getAllNotes(@RequestPayload request: GetAllNotesRequest): GetAllNotesResponse {
        val response = GetAllNotesResponse()
        response.notes.notes.addAll(notesService.getAllNotes())
        return response
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNoteByIdRequest")
    @ResponsePayload
    fun getNoteById(@RequestPayload request: GetNoteByIdRequest): GetNoteByIdResponse {
        val response = GetNoteByIdResponse()
        val note = notesService.getById(request.id)
        if (note != null) {
            response.note = note
        } else {
            // In SOAP, we typically return an empty response or use SOAP faults for errors
            // For simplicity, we return empty response, but SOAP faults would be better for production
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
            response.note = createdNote
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
        val updatedNote = notesService.updateById(request.id, noteDTO)
        if (updatedNote != null) {
            response.note = updatedNote
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
        val deletedNote = notesService.deleteById(request.id)
        if (deletedNote != null) {
            response.deletedNote = deletedNote
            response.status = "DELETED"
            response.message = "Note deleted successfully"
        } else {
            response.status = "NOT_FOUND"
        }
        return response
    }
}