package uk.ac.cf.cm6226.week1to3app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import uk.ac.cf.cm6226.week1to3app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.hello.text = getString(R.string.hello_world)
        var clicked = true
        binding.button.setOnClickListener {
            clicked = !clicked
            blah(if (clicked) getString(R.string.hello_world) else binding.input.text)
        }
        val view = binding.root
        setContentView(view)
    }
    private fun blah(text: CharSequence?) {
        binding.hello.text = text
    }
}
