package com.example.mymercado.ui.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskVisualTransformation(private val mask: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val out = StringBuilder()
        var maskIndex = 0
        var textIndex = 0

        while (maskIndex < mask.length && textIndex < text.length) {
            if (mask[maskIndex] == '0') {
                out.append(text[textIndex])
                textIndex++
            } else {
                out.append(mask[maskIndex])
            }
            maskIndex++
        }

        return TransformedText(AnnotatedString(out.toString()), object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var offsetTransformed = 0
                var i = 0
                while (i < offset && offsetTransformed < mask.length) {
                    if (mask[offsetTransformed] == '0') i++
                    offsetTransformed++
                }
                return offsetTransformed
            }
            override fun transformedToOriginal(offset: Int): Int {
                var offsetOriginal = 0
                var i = 0
                while (i < offset && i < mask.length) {
                    if (mask[i] == '0') offsetOriginal++
                    i++
                }
                return offsetOriginal
            }
        })
    }
}