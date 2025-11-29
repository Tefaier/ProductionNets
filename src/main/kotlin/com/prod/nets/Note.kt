package com.prod.nets

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
class Note {
    @Id
    lateinit var id: UUID
    lateinit var title: String
    lateinit var content: String

    constructor()

    constructor(id: UUID, title: String, content: String) {
        this.id = id
        this.title = title
        this.content = content
    }
}
