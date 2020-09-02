package com.softcoreinc.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""

//    override fun toString(): String {
//        return """
//            Name: $name
//            Artist: $artist
//            Release Date: $releaseDate
//            summary: $summary
//            Image URL: $imageURL
//        """.trimMargin().trimIndent()
//    }
}

class MainActivity : AppCompatActivity() {
    private val Tag = "MainActivity"
    private var downloadData: DownloadData? = null
    private var feedURL =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10
    private var feedCacheUrl = "INVALIDATED"
    private val stateUrl = "feedURL"
    private val stateLimit = "feedLimit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Log.d(TAG, "onCreate Called")

        if (savedInstanceState != null) {
            feedURL = savedInstanceState.getString(stateUrl).toString()
            feedLimit = savedInstanceState.getInt(stateUrl)
        }
        //val downloadData = DownloadData(this, xmlListView)
        downloadURL(feedURL.format(feedLimit))
//        Log.d(TAG, "onCreate: complete")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        return true
    }

    private fun downloadURL(feedURL: String) {
        if (feedURL != feedCacheUrl) {
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedURL)
            feedCacheUrl = feedURL
            // Log.d(Tag, "Url is downloaded")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnuFree -> feedURL =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid -> feedURL =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs -> feedURL =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.menu10, R.id.menu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    // Log.d(Tag, "it is ${item.title} amount: $feedLimit")
                } else {
                    //Log.d(Tag, "it is ${item.title} amount: default")
                    feedLimit = if (feedLimit == 25 && item.title == "Top 10") 10 else feedLimit
                }
            }
            R.id.mnuRefresh -> feedCacheUrl = "INVALIDATED"
            else -> return super.onOptionsItemSelected(item)
        }

        downloadURL(feedURL.format(feedLimit))
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(stateUrl, feedURL)
        outState.putInt(stateLimit, feedLimit)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context, listView: ListView) :
            AsyncTask<String, Void, String>() {
            // private val TAG = "DownloadData"

            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
//                Log.d(TAG, "onPostExecute: parameter is $result")
                val parseApplications = ParseApplications()
                parseApplications.parse(result)
                //old adapter
//                val arrayAdapter = ArrayAdapter<FeedEntry>(propContext, R.layout.list_item, parseApplications.applications)
//                propListView.adapter = arrayAdapter
                //new adapter
                val feedAdapter =
                    FeedAdapter(propContext, R.layout.list_record, parseApplications.applications)
                propListView.adapter = feedAdapter

            }

            override fun doInBackground(vararg url: String?): String {
//                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    //  Log.d(TAG, "doInBackground: Error downloading")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }


}
// java way of taking input
//            val inputStream = connection.inputStream
//            val inputStreamReader = InputStreamReader(inputStream)
//            val reader = BufferedReader(inputStreamReader)
// Old way of input
//                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
//                    val inputBuffer = CharArray(500)
//                    var charsRead = 0
//
//                    while (charsRead >= 0) {
//                        charsRead= reader.read(inputBuffer)
//                        if (charsRead > 0) {
//                            xmlResult.append(String(inputBuffer, 0, charsRead))
//                        }
//                    }
//
//                    reader.close()
//  Old way of doing catch block for exceptions
//                catch( e: MalformedURLException) {
//                    Log.d(TAG, "downloadXML: Invalid URL ${e.message}")
//                } catch (e: IOException) {
//                    Log.d(TAG, "downloadXML: IO Exception reading data: ${e.message}")
//                } catch(e: SecurityException) {
//                    e.printStackTrace()
//                    Log.d(TAG, "downloadXML: Security Exception.  Needs permissions? ${e.message}")
//                } catch(e: Exception) {
//                    Log.d(TAG, "downloadXML: Unknown Error: ${e.message}")
//                }
// One way of downloading data through XML
//val xmlResult = StringBuilder()
//
//try {
//    val url = URL(urlPath)
//    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//    val response = connection.responseCode
//    Log.d(TAG, "DownloadXML: The response code was $response")
//
//    //functional way of taking an input stream
////                    val stream = connection.inputStream
//    connection.inputStream.buffered().reader().use { xmlResult.append(it.readText())}
//
//
//    Log.d(TAG, "Received: ${xmlResult.length} bytes")
//    return xmlResult.toString()
//
//
//} catch(e: Exception) {
//
//    val errorMessage: String = when (e) {
//        is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
//        is IOException -> "downloadXML: IO Exception: ${e.message}"
//        is SecurityException -> "downloadXML: Security Exception: ${e.message}"
//        else -> "downloadXML: Unknown Exception: ${e.message}"
//
//    }
//
//
//}
//
//return ""
