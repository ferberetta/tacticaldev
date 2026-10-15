package cl.tacticaldev.visitas

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RutVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.length <= 1) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val cuerpo = raw.dropLast(1)
        val dv = raw.last()
        val conPuntos = cuerpo.reversed().chunked(3).joinToString(".").reversed()
        val formatted = "$conPuntos-$dv"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, raw.length)
                if (clampedOffset == 0) return 0
                if (clampedOffset == raw.length) return formatted.length

                var rawCount = 0
                for (i in formatted.indices) {
                    if ((formatted[i] != '.') && (formatted[i] != '-')) {
                        rawCount++
                    }
                    if (rawCount == clampedOffset) {
                        return (i + 1).coerceIn(0, formatted.length)
                    }
                }
                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clampedOffset = offset.coerceIn(0, formatted.length)
                if (clampedOffset == 0) return 0
                if (clampedOffset == formatted.length) return raw.length

                var rawCount = 0
                for (i in 0 until clampedOffset) {
                    if ((formatted[i] != '.') && (formatted[i] != '-')) {
                        rawCount++
                    }
                }
                return rawCount.coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}
