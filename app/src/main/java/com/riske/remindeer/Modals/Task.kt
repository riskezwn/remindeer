package com.riske.remindeer.Models

class Task(
    var titulo: String? = null,
    var descripcion: String? = null,
    var categoria: Int? = null,
    var timeStamp: String? = null,
    var done: Boolean = false,
    var objectId: String? = null
)