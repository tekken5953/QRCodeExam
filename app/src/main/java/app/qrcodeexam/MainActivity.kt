package app.qrcodeexam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import app.qrcodeexam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            genearteBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, GenerateActivity::class.java)
                startActivity(intent)
            }

            scanBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, ScanActivity::class.java)
                startActivity(intent)
            }
        }

        val title = binding.mainTitle.text.toString()
        val startIndex = title.indexOf("Q")
        val endIndex = title.indexOf("R") + 1
        val span = SpannableStringBuilder(title)
        span.setSpan(
            ForegroundColorSpan(
                ResourcesCompat.getColor(resources,
            android.R.color.holo_blue_light,null)),startIndex,endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.mainTitle.text = span
    }
}