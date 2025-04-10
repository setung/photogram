package com.setung.service

import com.setung.client.PostDetails
import com.setung.document.PostDocument
import com.setung.repo.PostDocRepository
import org.springframework.stereotype.Service

@Service
class PostDocService(
    private val postDocRepository: PostDocRepository
) {

    fun save(document: PostDocument) {
        postDocRepository.save(document)
    }

    fun searchByTag(tag: String, lastPostId: Long, size: Int) =
        postDocRepository.searchByTags(tag, lastPostId, size).hits().hits().mapNotNull { it.source() }

    fun delete(id: String) {
        postDocRepository.deleteById(id)
    }

    fun update(postDetails: PostDetails) {
        val document = get(postDetails.id.toString())
        document.update(postDetails)
        postDocRepository.save(document)
    }

    fun changeUserStatus(writerId: String, isVisible: Boolean) {
        val postDocuments = postDocRepository.findByWriterId(writerId)
        postDocuments.forEach { it.isVisible = isVisible }
        postDocRepository.saveAll(postDocuments)
    }

    fun get(id: String): PostDocument =
        postDocRepository.findById(id).orElseThrow { RuntimeException("Post not found") }
}
