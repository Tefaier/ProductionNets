package com.prod.nets

import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import java.util.UUID

@RedisHash("Note")
class Note(val id: UUID, var title: String, var content: String) : Serializable
