package com.example.jsonbrowser

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    val url = "https://test.webmx.ru/api/client"
    val list = ArrayList<Students>()

    var txt: String = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread {
            sendGet()
            handleJSON(txt)
            val st = Gson().fromJson<ArrayList<Students>>(txt, object : TypeToken<ArrayList<Students>>() {}. type)
            list.forEach{
                Log.d("StdList","ID - ${it.ID}")
                Log.d("StdList","Фамилия - ${it.FirstName}")
                Log.d("StdList","Имя - ${it.LastName}")
                Log.d("StdList","Отчество - ${it.Patronymic}")
                Log.d("StdList","Дата рождения - ${it.Birthday}")
                Log.d("StdList","Дата регестрации - ${it.RegistrationDate}")
                Log.d("StdList","Почта - ${it.Email}")
                Log.d("StdList","Телефон - ${it.Phone}")
                Log.d("StdList","Пол - ${it.GenderCode}")
                Log.d("StdList","Что-то фото - ${it.PhotoPath}")
                Log.d("StdList","----------------------------------")

            }
        }.start()





    }
    @WorkerThread
    fun getSend(){
        val httpURLConnection = URL(url).openConnection() as HttpURLConnection
        httpURLConnection.apply {
            connectTimeout = 10000
            requestMethod = "GET"
            doInput = true
        }

        if(httpURLConnection.responseCode != HttpURLConnection.HTTP_OK){

            return
        }
        val streamReader = InputStreamReader(httpURLConnection.inputStream)
        var text: String = ""
        streamReader.use {
            text = it.readText()
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    @WorkerThread
    fun sendGet(){
        val url = URL(url)

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET

            println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

            inputStream.bufferedReader().use {
                it.lines().forEach{ line ->
                    txt = line
                }

            }
        }
    }

    //Свой метод засовывать в массив
    private fun handleJSON(jsonString: String){
        val jsonArry = JSONArray(jsonString)
        var x = 0
        while (x < jsonArry.length()){
            val jsonObject = jsonArry.getJSONObject(x)
            list.add(Students(
                jsonObject.optInt("ID"),
                jsonObject.optString("FirstName"),
                jsonObject.optString("LastName"),
                jsonObject.optString("Patronymic"),
                jsonObject.optString("Birthday"),
                jsonObject.optString("RegistrationDate"),
                jsonObject.optString("Email"),
                jsonObject.optString("Phone"),
                jsonObject.optString("GenderCode"),
                jsonObject.optString("PhotoPath")))
            x++
        }
    }
}