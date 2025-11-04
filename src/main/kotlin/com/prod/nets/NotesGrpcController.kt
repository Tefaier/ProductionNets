package com.prod.nets

import org.springframework.grpc.server.service.GrpcService
import com.prod.nets.grpc.*
import io.grpc.stub.StreamObserver
import java.util.UUID

@GrpcService
class NotesGrpcController(private val notesService: NotesService) : NotesServiceGrpc.NotesServiceImplBase() {
    private companion object {
        private fun noteConversion(note: Note): com.prod.nets.grpc.Note {
            val newNote = com.prod.nets.grpc.Note.newBuilder()
            newNote.id = note.id.toString()
            newNote.title = note.title
            newNote.content = note.content
            return newNote.build()
        }
    }

    override fun getAllNotes(request: GetAllNotesRequest, responseObserver: StreamObserver<GetAllNotesResponse>) {
        val notes = notesService.getAllNotes()
        responseObserver.onNext(
            GetAllNotesResponse
                .newBuilder()
                .setStatus(OperationStatus.SUCCESS)
                .setNotes(NoteList.newBuilder().addAllNotes(notes.map(NotesGrpcController::noteConversion)).build())
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun getNoteById(request: GetNoteByIdRequest, responseObserver: StreamObserver<GetNoteByIdResponse>) {
        val note = notesService.getById(UUID.fromString(request.id))
        if (note != null) {
            responseObserver.onNext(GetNoteByIdResponse.newBuilder().setStatus(OperationStatus.SUCCESS).setNote(note.let(NotesGrpcController::noteConversion)).build())
        } else {
            responseObserver.onError(NoSuchElementException())
        }
        responseObserver.onCompleted()
    }

    override fun createNote(request: CreateNoteRequest, responseObserver: StreamObserver<CreateNoteResponse>) {
        try {
            val noteDTO = NoteDTO(
                title = request.title,
                content = request.content
            )
            val createdNote = notesService.createNote(noteDTO)
            responseObserver.onNext(CreateNoteResponse.newBuilder().setStatus(OperationStatus.CREATED).setNote(createdNote.let(NotesGrpcController::noteConversion)).build())
        } catch (e: IllegalArgumentException) {
            responseObserver.onError(e)
        }
        responseObserver.onCompleted()
    }

    override fun updateNote(request: UpdateNoteRequest, responseObserver: StreamObserver<UpdateNoteResponse>) {
        val noteDTO = NoteDTO(
            title = request.title,
            content = request.content
        )
        val updatedNote = notesService.updateById(UUID.fromString(request.id), noteDTO)
        if (updatedNote != null) {
            responseObserver.onNext(UpdateNoteResponse.newBuilder().setStatus(OperationStatus.UPDATED).setNote(updatedNote.let(NotesGrpcController::noteConversion)).build())
        } else {
            responseObserver.onError(NoSuchElementException())
        }
        responseObserver.onCompleted()
    }

    override fun deleteNote(request: DeleteNoteRequest, responseObserver: StreamObserver<DeleteNoteResponse>) {
        val deletedNote = notesService.deleteById(UUID.fromString(request.id))
        if (deletedNote != null) {
            responseObserver.onNext(DeleteNoteResponse.newBuilder().setStatus(OperationStatus.DELETED).setDeletedNote(deletedNote.let(NotesGrpcController::noteConversion)).build())
        } else {
            responseObserver.onError(NoSuchElementException())
        }
        responseObserver.onCompleted()
    }
}
