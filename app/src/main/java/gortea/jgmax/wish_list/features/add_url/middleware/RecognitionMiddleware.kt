package gortea.jgmax.wish_list.features.add_url.middleware

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class RecognitionMiddleware(
    private val textRecognizer: TextRecognizer,
    private val delayedEvent: DelayedEvent<AddUrlEvent>
) : Middleware<AddUrlEvent> {
    override suspend fun effect(event: AddUrlEvent): AddUrlEvent? {
        val newEvent: AddUrlEvent? = when (event) {
            is AddUrlEvent.RecognizeText -> {
                var isRecognizing = true
                val image = InputImage.fromBitmap(event.bitmap, 0)
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        isRecognizing = false
                        delayedEvent.onEvent(AddUrlEvent.RecognitionSucceed(visionText.text))
                    }
                    .addOnFailureListener { _ ->
                        isRecognizing = false
                        delayedEvent.onEvent(AddUrlEvent.RecognitionFailed)
                    }
                if (isRecognizing) {
                    AddUrlEvent.RecognitionInProcess
                } else {
                    null
                }
            }
            else -> null
        }
        return newEvent
    }
}