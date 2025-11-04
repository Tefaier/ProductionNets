package com.prod.nets

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotesRepository : CrudRepository<Note, UUID>
