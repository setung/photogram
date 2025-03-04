package com.setung.userservice.entity

import jakarta.persistence.*

@Entity
@Table(name = "follow")
class FollowEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    val requester: UserEntity,

    @ManyToOne
    val target: UserEntity,

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