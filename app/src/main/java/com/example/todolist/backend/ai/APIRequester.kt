package com.example.todolist.backend.ai

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class APIRequester(private val apiKey: String) {
    private val client = OkHttpClient()

    suspend fun suggestTasks(category: String, callback: (String) -> Unit) {
        val json = """
            {
            "model": "command-r-plus",
            "prompt": "Generate an arabic list of 10 tasks related to '$category'. Format them as a numbered list. every task is about 3 or 4 words",
            "max_tokens": 400,
            "temperature": 0.7
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.cohere.ai/v1/generate")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback("Error: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    if (!response.isSuccessful) {
                        println("Error2")
                        callback("API Error: ${body}")
                        return
                    }

                    body?.let {
                        println("done")
                        val reply = JSONObject(it).getJSONArray("generations").getJSONObject(0)
                            .getString("text")
                        callback(reply)
                    }
                }
            })
        }

    }

    private fun parseJsonResponse(jsonResponse: String): String? {
        try {
            val jsonObject = JSONObject(jsonResponse)
            val messageObject = jsonObject.getJSONObject("message")
            val contentArray = messageObject.getJSONArray("content")

            if (contentArray.length() > 0) {
                val text = contentArray.getJSONObject(0).getString("text")
                return text
            } else {
                Log.e("API_Response", "No content found in response")
                return null
            }
        } catch (e: Exception) {
            println(e.message)
            Log.e("API_Response", "Error parsing response: ${e.message}", e)
            return null
        }
    }
}