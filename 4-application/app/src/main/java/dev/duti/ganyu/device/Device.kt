package dev.duti.ganyu.device

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.SmsManager

data class Message(val phoneNumber: String, val content: String)

data class Contact(val phoneNumber: String, val name: String)

class Device {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context
    }

    fun getSmsAll(): List<Message> {
        val messages = mutableListOf<Message>()
        val cur =
            appContext.contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null)
                ?: return messages
        while (cur.moveToNext()) {
            val addressColumn = cur.getColumnIndex("address")
            if (addressColumn == -1) {
                continue
            }
            val bodyColumn = cur.getColumnIndex("body")
            if (bodyColumn == -1) {
                continue
            }
            val message = Message(cur.getString(addressColumn), cur.getString(bodyColumn))
            messages.add(message)
        }
        cur.close()
        return messages
    }

    fun getSmsFrom(number: String): List<Message> {
        val messages = mutableListOf<Message>()
        val cur =
            appContext.contentResolver.query(
                Uri.parse("content://sms/inbox"),
                null,
                "address = ?",
                arrayOf(number),
                null
            )
                ?: return messages
        while (cur.moveToNext()) {
            val addressColumn = cur.getColumnIndex("address")
            if (addressColumn == -1) {
                continue
            }
            val bodyColumn = cur.getColumnIndex("body")
            if (bodyColumn == -1) {
                continue
            }
            val message = Message(cur.getString(addressColumn), cur.getString(bodyColumn))
            messages.add(message)
        }
        cur.close()
        return messages
    }

    fun sendSms(message: Message) {
        val smsManager = appContext.getSystemService(SmsManager::class.java)
        smsManager.sendTextMessage(message.phoneNumber, null, message.content, null, null)
    }

    fun getContacts(): List<Contact> {
        try {
            val contacts = mutableListOf<Contact>()
            val cur: Cursor =
                appContext.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf<String>(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )
                    ?: return contacts
            while (cur.moveToNext()) {
                val displayCol =
                    cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                if (displayCol == -1) {
                    continue
                }
                val phoneCol = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (phoneCol == -1) {
                    continue
                }
                val contact = Contact(cur.getString(phoneCol), cur.getString(displayCol))
                contacts.add(contact)
            }
            cur.close()
            return contacts
        } catch (e: Exception) {
            return listOf<Contact>(Contact(e.message.toString(), ""))
        }
    }
}
