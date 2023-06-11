package de.bund.digitalservice.ris.norms.framework.adapter.output.database.repository

import de.bund.digitalservice.ris.norms.framework.adapter.output.database.dto.NormDto
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface NormsRepository : ReactiveCrudRepository<NormDto, UUID> {

    fun findByGuid(norm: UUID): Mono<NormDto>

    @Query(
        """
        SELECT DISTINCT n.guid
        FROM norms n
        LEFT JOIN metadata_sections ms ON n.id = ms.norm_id
        LEFT JOIN metadata m1 ON ms.id = m1.section_id AND ms.name = 'CITATION_DATE'
        LEFT JOIN metadata_sections ms2 ON n.id = ms2.norm_id AND ms2.name = 'PRINT_ANNOUNCEMENT'
        LEFT JOIN metadata m2 ON ms2.id = m2.section_id AND m2.type = 'ANNOUNCEMENT_GAZETTE' AND m2.value = :gazette
        LEFT JOIN metadata m3 ON ms2.id = m3.section_id AND m3.type = 'PAGE' AND m3.value = :page
        LEFT JOIN metadata_sections ms3 ON n.id = ms3.norm_id AND ms3.name = 'DIGITAL_ANNOUNCEMENT'
        LEFT JOIN metadata m4 ON ms3.id = m4.section_id AND m4.type = 'ANNOUNCEMENT_MEDIUM' AND m4.value = :gazette
        LEFT JOIN metadata m5 ON ms3.id = m5.section_id AND ((m5.type = 'EDITION' AND m5.value = :page)
                                                            OR (m5.type = 'PAGE' AND m5.value = :page))
        WHERE (
                ((ms2.name = 'PRINT_ANNOUNCEMENT' AND m2.type = 'ANNOUNCEMENT_GAZETTE' AND m2.value = :gazette)
                AND (ms2.name = 'PRINT_ANNOUNCEMENT' AND m3.type = 'PAGE' AND m3.value = :page))
                OR (
                    n.id NOT IN (SELECT ms5.norm_id
                                 FROM metadata_sections ms5
                                 JOIN metadata m8 ON ms5.id = m8.section_id
                                 JOIN metadata m9 ON ms5.id = m9.section_id
                                 WHERE ms5.name = 'PRINT_ANNOUNCEMENT'
                                 AND m8.type = 'ANNOUNCEMENT_GAZETTE'
                                 AND m9.type = 'PAGE')
                    AND (ms3.name = 'DIGITAL_ANNOUNCEMENT' AND m4.type = 'ANNOUNCEMENT_MEDIUM' AND m4.value = :gazette)
                    AND (ms3.name = 'DIGITAL_ANNOUNCEMENT' AND ((m5.type = 'EDITION' AND m5.value = :page)
                                                                OR (m5.type = 'PAGE' AND m5.value = :page
                                                                    AND n.id NOT IN (SELECT ms4.norm_id
                                                                                     FROM metadata_sections ms4
                                                                                     JOIN metadata m7 ON ms4.id = m7.section_id
                                                                                     WHERE ms4.name = 'DIGITAL_ANNOUNCEMENT'
                                                                                     AND m7.type = 'EDITION')
                                                                    )
                                                                )
                        )
                    )
                )
            AND (
                (EXTRACT(YEAR FROM n.announcement_date) = CAST(:year AS DOUBLE PRECISION))
                OR (n.announcement_date IS NULL AND m1.type = 'DATE' AND SUBSTRING(m1.value, 1, 4) = :year)
                OR (n.announcement_date IS NULL AND m1.type = 'YEAR' AND m1.value = :year AND n.id NOT IN (SELECT DISTINCT ms6.norm_id
                                                                                                           FROM metadata_sections ms6
                                                                                                           JOIN metadata m10 ON ms6.id = m10.section_id
                                                                                                           WHERE ms6.name = 'CITATION_DATE'
                                                                                                           AND m10.type = 'DATE')
                    )
                )
        LIMIT 1
    """,
    )
    fun findNormByEli(gazette: String, year: String, page: String): Mono<String>
}
