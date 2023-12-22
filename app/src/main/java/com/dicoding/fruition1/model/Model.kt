
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.dicoding.fruition1.ui.theme.Fruition1Theme
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Model : ComponentActivity() {

    private lateinit var tflite: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Fruition1Theme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
            }
        }

        // Inisialisasi interpreter TFLite
        tflite = Interpreter(loadModelFile())
        // Load gambar dari penyimpanan internal
        val fileName = "timestamp.jpg"
        val bitmap = loadBitmapFromFile(fileName)

        // Misalnya, untuk melakukan prediksi pada gambar
        val inputBuffer = ByteBuffer.allocateDirect(4 * (bitmap?.width ?: 400) * (bitmap?.height ?: 400)).apply {
            order(ByteOrder.nativeOrder())
        }



// Isi inputBuffer dengan data gambar sesuai dengan format yang diperlukan oleh model Anda
        fillInputBuffer(inputBuffer, bitmap)
        val numClasses = 12
        val bufferSize = numClasses * 4
// Gunakan buffer output yang tidak terkait dengan dimensi kelas
        val outputBuffer = ByteBuffer.allocateDirect(bufferSize).apply {
            order(ByteOrder.nativeOrder())
        }

// Jalankan model untuk mendapatkan output
        tflite.run(inputBuffer, outputBuffer)

    }

    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = assets.openFd("fruition_model.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadBitmapFromFile(fileName: String): Bitmap? {
        val filePath = getFileStreamPath(fileName).absolutePath
        return BitmapFactory.decodeFile(filePath)
    }

    private fun fillInputBuffer(inputBuffer: ByteBuffer, bitmap: Bitmap?) {
        bitmap?.let {
            // Contoh: Konversi gambar ke ByteBuffer
            val intValues = IntArray(it.width * it.height)
            it.getPixels(intValues, 0, it.width, 0, 0, it.width, it.height)

            var pixel = 0
            for (i in 0 until it.width) {
                for (j in 0 until it.height) {
                    val value = intValues[pixel++]

                    // Lakukan normalisasi atau transformasi lainnya sesuai kebutuhan
                    // dan isi inputBuffer
                    // Contoh: Memasukkan nilai pixel ke dalam inputBuffer tanpa normalisasi
                    inputBuffer.putFloat(value.toFloat())
                }
            }
        }
    }

}

