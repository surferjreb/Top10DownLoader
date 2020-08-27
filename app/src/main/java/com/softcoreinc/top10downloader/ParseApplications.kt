package com.softcoreinc.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplications {
    private val TAG = "ParseApplications"
    var applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentEntry = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT){
                val tagName = xpp.name?.toLowerCase()
                when (eventType) {
                    XmlPullParser.START_TAG -> {
//                        Log.d(TAG, "parse: Starting tag for $tagName")
                        if (tagName == "entry"){
                            inEntry = true
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
//                        Log.d(TAG, "parse: Ending tag for $tagName")
                        if(inEntry) {
                            when (tagName) {
                                "entry" -> {
                                applications.add(currentEntry)
                                inEntry = false
                                currentEntry = FeedEntry()
                                }
                                "name" -> currentEntry.name = textValue
                                "artist" -> currentEntry.artist = textValue
                                "releasedate" -> currentEntry.releaseDate = textValue
                                "summary" -> currentEntry.summary = textValue
                                "image" -> currentEntry.imageURL = textValue
                            }

                        }
                    }
                }

                //Nothing else to do
                eventType = xpp.next()
            }

//            for (app in applications) {
//                Log.d(TAG, "**********************************")
//                Log.d(TAG, app.toString())
//            }

        } catch(e: Exception){
            e.printStackTrace()
            status = false
        }

        return status
    }
}