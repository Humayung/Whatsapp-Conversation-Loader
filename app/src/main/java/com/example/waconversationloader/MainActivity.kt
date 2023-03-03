package com.example.waconversationloader

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    private var receiver = ""
    private val myTag : String = "CUSTOM_TAG"
    private lateinit var adapter : ChatListAdapter
    private val list = ArrayList<ChatItem>()
    private lateinit var conversationHistoryUri : Uri
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatList.addItemDecoration(MarginItemDecoration(10))
        loadButton.setOnClickListener {
            showFileChooser()
        }
        backButton.setOnClickListener {
            chatList.visibility = View.GONE
            loadButton.visibility = View.VISIBLE
        }


        adapter = ChatListAdapter(list)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL

        toBottom.setOnClickListener{
            chatList.scrollToPosition(adapter.itemCount-1)
        }
        toTop.setOnClickListener{
            chatList.scrollToPosition(0)
        }
        chatList.layoutManager = llm
        chatList.adapter = adapter

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                conversationHistoryUri = data!!.data!!
                showInputDialog()
            }
        }

    }




    private fun parse(line: String, senderName: String): ChatItem {
        val item = ChatItem()
        try{
            val dateEndStr = ", "
            val dateEnd = line.indexOf(dateEndStr)
            val date = line.substring(0, dateEnd)
            val timeEndStr = " - "
            val timeEnd = line.indexOf(timeEndStr)
            val time = line.substring(dateEnd + dateEndStr.length, timeEnd)
            val nameEndStr = ": "
            val nameEnd = line.indexOf(nameEndStr)
            val name = line.substring(timeEnd + timeEndStr.length, nameEnd)
            val text = line.substring(nameEnd + nameEndStr.length)
            item.date = date
            item.time = time
            item.name = name
            item.text = text
            item.isSender = name.lowercase() != senderName
        } catch(e : java.lang.Exception) {
            item.date = "Unknown"
            item.time = "../.."
            item.name = senderName
            item.text = line
            item.isSender = true
        }
        return item

    }

    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            resultLauncher?.launch(Intent.createChooser(intent, "Select a File to Upload"))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                this, "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun readTextFile(uri: Uri) {
        try {
            adapter.clear()
            val `in`: InputStream? = contentResolver.openInputStream(uri)
            val r = BufferedReader(InputStreamReader(`in`))
            var line: String?
            var prevDate: String
            var parsed = ChatItem()
            while (r.readLine().also { line = it } != null) {
                prevDate = parsed.date
                parsed = parse(line!!, receiver)
                parsed.showDate = prevDate != parsed.date
                list.add(parsed)
            }
        } catch (e: java.lang.Exception) {
            Log.e(myTag, e.stackTrace.toString())
        }
        chatList.scrollToPosition(0)
        receiverName.text = receiver.replaceFirstChar { it.uppercase() }
        chatList.visibility = View.VISIBLE
        loadButton.visibility = View.GONE
    }

    private fun showInputDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Receiver Name")
        val input = EditText(this)

        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            receiver = input.text.toString().lowercase()
            readTextFile(conversationHistoryUri)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

}