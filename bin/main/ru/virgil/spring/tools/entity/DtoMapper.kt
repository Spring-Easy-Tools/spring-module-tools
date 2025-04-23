package ru.virgil.spring.tools.entity

interface DtoMapper<Entity, Dto> {

    fun toDto(entity: Entity): Dto

    fun toEntity(dto: Dto): Entity

    fun merge(dto: Dto, entity: Entity): Entity
}
