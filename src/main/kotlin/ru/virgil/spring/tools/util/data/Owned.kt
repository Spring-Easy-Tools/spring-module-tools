package ru.virgil.spring_tools.tools.util.data

import org.springframework.security.core.userdetails.UserDetails

interface Owned {

    val createdBy: UserDetails
}
