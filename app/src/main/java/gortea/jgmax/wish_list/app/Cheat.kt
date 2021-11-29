package gortea.jgmax.wish_list.app

import android.os.Handler
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

// todo remove

private val uiHandler: Handler = Handler()
fun showHTML(html: String) {
    uiHandler.post(
        Runnable {
            val doc: Document = Jsoup.parse(html.replace(" ", " ", true))
            val elems =
                doc.select("span:contains(1 500)").filter { it.children().isEmpty() }
            Log.e("elems", elems.toString())
            if (elems.isNotEmpty()) {
                val cssSelector = elems.first().cssSelector()
                Log.e("selector", cssSelector)
                elems
                    .firstOrNull()
                    ?.before("<span id=\"target\" style=\"foreground-color:#000000;\">" + elems[0].text() + "</span>")
                    ?.remove()
            }
        }
    )
}