package de.bund.digitalservice.ris.norms.application.port.output

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.createRandomNorm

class EditNormCommandTest {

    @Test
    fun `can create command with a norm`() {
        val norm = createRandomNorm()
        val command = EditNormOutputPort.Command(norm)

        assertThat(command.norm).isEqualTo(norm)
    }
}
