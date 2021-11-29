package gortea.jgmax.wish_list.features.add_url.middleware

import gortea.jgmax.wish_list.app.data.repository.Repository
import gortea.jgmax.wish_list.features.add_url.event.AddUrlEvent
import gortea.jgmax.wish_list.features.add_url.params.ProductParsingResult
import gortea.jgmax.wish_list.mvi.domain.Middleware
import org.jsoup.Jsoup

class ParseMiddleware(
    private val repository: Repository
) : Middleware<AddUrlEvent> {
    override suspend fun effect(event: AddUrlEvent): AddUrlEvent? {
        val newEvent: AddUrlEvent? = when (event) {
            is AddUrlEvent.ParseUrl -> {
                repository.getSelector(event.url)
                AddUrlEvent.ParseHtml(event.url, event.html, "", "", "")
            }
            is AddUrlEvent.ParseHtml -> {
                val doc = Jsoup.parse(event.html.replace(" ", " ", true))
                val price = doc.select(event.priceSelector)
                    .firstOrNull { it.children().isEmpty() }
                val name = doc.select(event.titleSelector)
                    .firstOrNull { it.children().isEmpty() }
                val imageUrl = doc.select(event.imageSelector)
                    .firstOrNull { it.children().isEmpty() }
                val title = doc.title()
                if (price == null || name == null || imageUrl == null) {
                    AddUrlEvent.ParsingFailed
                } else {
                    AddUrlEvent.ParsingSucceeded(
                        ProductParsingResult(
                            url = event.url,
                            title = name.text(),
                            imageUrl = imageUrl.text(),
                            currentPrice = price.text().replace(" ", ""),
                            groupName = title
                        )
                    )
                }
            }
            else -> null
        }
        return newEvent
    }
}