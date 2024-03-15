package app.qrcodeexam

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidmads.library.qrgenearator.QRGSaver
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import app.qrcodeexam.databinding.ActivityGenearteBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.random.nextUBytes

// https://github.com/androidmads/QRGenerator/blob/master/app/src/main/java/androidmads/example/MainActivity.java
// https://m.blog.naver.com/jcosmoss/221157180721
// https://www.androidmads.info/2016/07/qr-code-generator-library.html

class GenerateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenearteBinding

    private val tagRequestPermission = 0x0000001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_genearte)

        binding.apply {
            generateBtn.setOnClickListener {
                binding.generateQrData.text = "Model name : ${model()} Serial : ${serial()}"
                binding.generateQrImg.setImageBitmap(bitmap())
                binding.generateQrContainer.visibility = View.VISIBLE
            }

            generateRegenerate.setOnClickListener {
                generateQrImg.setImageBitmap(null)
                generateQrImg.setImageBitmap(bitmap())
            }

            generateShare.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.putExtra(Intent.EXTRA_STREAM, bitmapToUri(bitmap()))
                intent.type = "image/jpeg"
                startActivity(Intent.createChooser(intent, "Share QR Code from AS"))
            }

            generateSave.setOnClickListener {
                val serial = serial()
                saveFile(path() +"/", name())
            }
        }
    }

    private fun name(): String {
        return "QR_${model()}_${serial().substring(serial().lastIndex-5, serial().length)}_${LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}"
    }

    private fun path(): String {
        val fileImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return fileImg.path
    }

    private fun bitmap(): Bitmap {
        return generate("${createRandomId()}/${model()}/${serial()}")
    }

    private fun serial(): String {
        return binding.generateDataSerial.text.toString()
    }

    private fun model(): String {
        return binding.generateDataModel.text.toString().replace(" ","_")
    }

    private fun generate(data: String): Bitmap {
        val encoder = BarcodeEncoder()
        return encoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 250,250)
    }

    private fun createRandomId(): ByteArray {
        return Random.nextBytes(15)
    }

    @SuppressLint("SetWorldReadable")
    private fun makeFile(fileName: String): File {
        Log.d("testtest","external cache directory is ${applicationContext.externalCacheDir}")
        val file = File(applicationContext.externalCacheDir, fileName)
        val outputStream = FileOutputStream(file)
        bitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        outputStream.flush()
        outputStream.close()

        file.setReadable(true, false)

        return file
    }

    private fun saveFile(path: String, fileName: String) {
        try {
            val save = QRGSaver().save(path, fileName, bitmap(), QRGContents.ImageType.IMAGE_JPEG)
            Toast.makeText(this, "Save ${if (save) "Success" else "Fail"}", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.stackTraceToString()
            Toast.makeText(this, "Save Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), tagRequestPermission)
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        @Suppress("DEPRECATION") val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, name() , null)
        return Uri.parse(path)
    }
}