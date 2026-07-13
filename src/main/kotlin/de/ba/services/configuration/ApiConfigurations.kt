package de.ba.services.configuration


class Api(
    var url: String = "https://example.com/api/v4",
    var pageSize: Int = 50,
    var authentication: Authentication = Authentication(),
)

class Authentication(var token: String = "<token>")