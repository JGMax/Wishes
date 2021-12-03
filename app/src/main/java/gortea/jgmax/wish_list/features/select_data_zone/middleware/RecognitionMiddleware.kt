package gortea.jgmax.wish_list.features.select_data_zone.middleware

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import gortea.jgmax.wish_list.features.select_data_zone.event.SelectDataZoneEvent
import gortea.jgmax.wish_list.mvi.domain.DelayedEvent
import gortea.jgmax.wish_list.mvi.domain.Middleware

class RecognitionMiddleware(
    private val textRecognizer: TextRecognizer,
    private val delayedEvent: DelayedEvent<SelectDataZoneEvent>
) : Middleware<SelectDataZoneEvent> {
    override suspend fun effect(event: SelectDataZoneEvent): SelectDataZoneEvent? {
        val newEvent: SelectDataZoneEvent? = when (event) {
            is SelectDataZoneEvent.RecognizeText -> {
                var isRecognizing = true
                val image = InputImage.fromBitmap(event.bitmap, 0)
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        isRecognizing = false
                        delayedEvent.onEvent(SelectDataZoneEvent.RecognitionSucceed(visionText.text))
                    }
                    .addOnFailureListener {
                        isRecognizing = false
                        delayedEvent.onEvent(SelectDataZoneEvent.RecognitionFailed)
                    }
                if (isRecognizing) {
                    SelectDataZoneEvent.RecognitionInProcess
                } else {
                    null
                }
            }
            else -> null
        }
        return newEvent
    }
}