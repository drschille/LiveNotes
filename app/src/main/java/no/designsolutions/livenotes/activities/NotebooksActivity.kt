package no.designsolutions.livenotes.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Layout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.designsolutions.livenotes.R
import no.designsolutions.livenotes.activities.ui.theme.LiveNotesTheme

class NotebooksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveNotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiveNotesTheme {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = "Notebooks",
                        color = Color.LightGray
                    )
                },
                backgroundColor = Color(35, 60, 75,255),
                navigationIcon = {
                    Image(
                        painterResource(id = R.drawable.ic_baseline_notebook),
                        "file icon",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(12.dp)
                    )
                }

            )
            Greeting("Android")
        }

    }
}