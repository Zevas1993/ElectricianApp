package com.example.electricianapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.electricianapp.domain.model.photodoc.AnnotationType

/**
 * Entity representing a photo document in the database
 */
@Entity(
    tableName = "photo_documents",
    indices = [
        Index("jobId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PhotoDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jobId: Long? = null,
    val title: String,
    val description: String,
    val photoUri: String,
    val thumbnailUri: String?,
    val dateCreated: Long,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val tagsJson: String,  // JSON array of tags
    val beforeAfterPairId: Long?
)

/**
 * Entity representing a photo annotation in the database
 */
@Entity(
    tableName = "photo_annotations",
    indices = [
        Index("photoId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = PhotoDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PhotoAnnotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val photoId: Long,
    val type: String,  // Stored as string representation of AnnotationType
    val x: Float,
    val y: Float,
    val width: Float?,
    val height: Float?,
    val text: String,
    val color: Int
)

/**
 * Entity representing a before/after photo pair in the database
 */
@Entity(
    tableName = "before_after_pairs",
    indices = [
        Index("beforePhotoId"),
        Index("afterPhotoId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = PhotoDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["beforePhotoId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PhotoDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["afterPhotoId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class BeforeAfterPairEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val beforePhotoId: Long,
    val afterPhotoId: Long,
    val title: String,
    val description: String
)
