package com.prod.nets

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface NotesRepository: CrudRepository<Note, UUID> {}