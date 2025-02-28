package com.setung.entity

import jakarta.persistence.*

@Entity
class Follow(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    val requester: User,

    @ManyToOne
    val target: User,

    @Enumerated(value = EnumType.STRING)
    var status: FollowStatus

) : BaseEntity() {

    fun accept() {
        this.status = FollowStatus.ACCEPTED
    }

    fun reject() {
        this.status = FollowStatus.REJECTED
    }
}