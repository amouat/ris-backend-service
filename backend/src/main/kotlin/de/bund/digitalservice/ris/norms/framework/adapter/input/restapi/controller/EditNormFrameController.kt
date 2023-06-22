package de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.controller

import de.bund.digitalservice.ris.norms.application.port.input.EditNormFrameUseCase
import de.bund.digitalservice.ris.norms.domain.entity.MetadataSection
import de.bund.digitalservice.ris.norms.domain.entity.Metadatum
import de.bund.digitalservice.ris.norms.domain.value.MetadataSectionName
import de.bund.digitalservice.ris.norms.domain.value.MetadatumType
import de.bund.digitalservice.ris.norms.domain.value.NormCategory
import de.bund.digitalservice.ris.norms.domain.value.OtherType
import de.bund.digitalservice.ris.norms.domain.value.ProofIndication
import de.bund.digitalservice.ris.norms.domain.value.ProofType
import de.bund.digitalservice.ris.norms.domain.value.UndefinedDate
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.ApiConfiguration
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.decodeGuid
import de.bund.digitalservice.ris.norms.framework.adapter.input.restapi.decodeLocalDate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(ApiConfiguration.API_NORMS_PATH)
class EditNormFrameController(private val editNormFrameService: EditNormFrameUseCase) {

    @PutMapping(path = ["/{guid}"])
    fun editNormFrame(
        @PathVariable guid: String,
        @RequestBody request: NormFramePropertiesRequestSchema,
    ): Mono<ResponseEntity<Void>> {
        val properties = request.toUseCaseData()
        val command = EditNormFrameUseCase.Command(decodeGuid(guid), properties)

        return editNormFrameService
            .editNormFrame(command)
            .map { ResponseEntity.noContent().build<Void>() }
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

    class NormFramePropertiesRequestSchema {
        lateinit var officialLongTitle: String
        lateinit var metadataSections: List<MetadataSectionRequestSchema>
        var risAbbreviation: String? = null
        var documentNumber: String? = null
        var documentCategory: String? = null

        var providerEntity: String? = null
        var providerDecidingBody: String? = null
        var providerIsResolutionMajority: Boolean? = null

        var officialShortTitle: String? = null
        var officialAbbreviation: String? = null

        var announcementDate: String? = null
        var publicationDate: String? = null

        var completeCitation: String? = null

        var statusNote: String? = null
        var statusDescription: String? = null
        var statusDate: String? = null
        var statusReference: String? = null
        var repealNote: String? = null
        var repealArticle: String? = null
        var repealDate: String? = null
        var repealReferences: String? = null
        var reissueNote: String? = null
        var reissueArticle: String? = null
        var reissueDate: String? = null
        var reissueReference: String? = null
        var otherStatusNote: String? = null

        var documentStatusWorkNote: String? = null
        var documentStatusDescription: String? = null
        var documentStatusDate: String? = null
        var documentStatusReference: String? = null
        var documentStatusEntryIntoForceDate: String? = null
        var documentStatusProof: String? = null
        var documentTextProof: String? = null
        var otherDocumentNote: String? = null

        var eli: String? = null

        var celexNumber: String? = null

        var text: String? = null

        fun toUseCaseData(): EditNormFrameUseCase.NormFrameProperties = EditNormFrameUseCase.NormFrameProperties(
            this.officialLongTitle,
            this.metadataSections.map { it.toUseCaseData() },
            this.risAbbreviation,
            this.documentNumber,
            this.documentCategory,
            this.officialShortTitle,
            this.officialAbbreviation,
            decodeLocalDate(this.announcementDate),
            decodeLocalDate(this.publicationDate),
            this.completeCitation,
            this.statusNote,
            this.statusDescription,
            decodeLocalDate(this.statusDate),
            this.statusReference,
            this.repealNote,
            this.repealArticle,
            decodeLocalDate(this.repealDate),
            this.repealReferences,
            this.reissueNote,
            this.reissueArticle,
            decodeLocalDate(this.reissueDate),
            this.reissueReference,
            this.otherStatusNote,
            this.documentStatusWorkNote,
            this.documentStatusDescription,
            decodeLocalDate(this.documentStatusDate),
            this.documentStatusReference,
            decodeLocalDate(this.documentStatusEntryIntoForceDate),
            this.documentStatusProof,
            this.documentTextProof,
            this.otherDocumentNote,
            this.celexNumber,
            this.text,
        )
    }

    class MetadataSectionRequestSchema {
        lateinit var name: MetadataSectionName
        var metadata: List<MetadatumRequestSchema>? = null
        var order: Int = 1
        var sections: List<MetadataSectionRequestSchema>? = null

        fun toUseCaseData(): MetadataSection {
            val metadata = this.metadata?.map { it.toUseCaseData() }
            val childSections = this.sections?.map { it.toUseCaseData() }
            return MetadataSection(name = this.name, order = order, metadata = metadata ?: emptyList(), sections = childSections)
        }
    }

    class MetadatumRequestSchema {
        lateinit var value: String
        lateinit var type: MetadatumType
        var order: Int = 1

        fun toUseCaseData(): Metadatum<*> {
            val value = when (this.type) {
                MetadatumType.DATE -> decodeLocalDate(this.value)
                MetadatumType.RESOLUTION_MAJORITY -> this.value.toBoolean()
                MetadatumType.NORM_CATEGORY -> NormCategory.valueOf(this.value)
                MetadatumType.UNDEFINED_DATE -> UndefinedDate.valueOf(this.value)
                MetadatumType.PROOF_INDICATION -> ProofIndication.valueOf(this.value)
                MetadatumType.PROOF_TYPE -> ProofType.valueOf(this.value)
                MetadatumType.OTHER_TYPE -> OtherType.valueOf(this.value)
                else -> this.value
            }
            return Metadatum(value = value, type = this.type, order = this.order)
        }
    }
}
