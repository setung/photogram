package com.setung.entity

import jakarta.persistence.*

@Entity
@Table(name = "tag")
class TagEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String

) : BaseEntity() {

    companion object {
        fun of(name: String) = TagEntity(
            name = name
        )
    }
}