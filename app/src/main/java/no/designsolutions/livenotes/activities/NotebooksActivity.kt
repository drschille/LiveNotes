package no.designsolutions.livenotes.activities


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.designsolutions.livenotes.R
import no.designsolutions.livenotes.activities.ui.theme.LiveNotesTheme
import no.designsolutions.livenotes.data.Notebook
import no.designsolutions.livenotes.model.NotebookViewModel


class NotebooksActivity2 : ComponentActivity() {

    //    private val notebookViewModel by lazy {
//        ViewModelProvider(this)
//            .get(NotebookViewModel::class.java)
//    }


    private val dummyList =  mutableStateListOf<String>()


    private val viewModel: NotebookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dummyList.addAll(arrayOf("1", "2"))


        setContent {

            LiveNotesTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column {

                        Greeting("Android")
                        Button(onClick = {
                            val lastVal = dummyList.last().toInt() + 1
                            dummyList.add(lastVal.toString())
                        }

                        ) {
                            Text("Add to list")
                        }

                        LazyColumn {

                            items(dummyList) { item ->
                                Text(text = "Hello $item")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun NotebooksLazyColumn(
    notebooks: List<Notebook>,
) {

    LazyColumn {
        items(notebooks) { notebook ->
            Greeting(name = notebook.name)
        }
    }
}

@Composable
fun StringLazyColumn(
    stringList: List<String>,
) {
    LazyColumn {
        items(stringList) {
            Greeting(name = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview(myList: MutableState<List<String>> = mutableStateOf(listOf("1", "2", "3"))) {
    LiveNotesTheme {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = "Notebooks",
                        color = Color.LightGray
                    )
                },
                backgroundColor = Color(35, 60, 75, 255),
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
            StringLazyColumn(myList.value)
            Greeting("Android")
        }

    }
}