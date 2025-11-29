package com.prod.nets

data class SendRequest(val update: UpdateType, val title: String, val oldTitle: String?, val content: String, val oldContent: String?)

