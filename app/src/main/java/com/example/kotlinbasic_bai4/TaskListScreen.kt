package com.example.kotlinbasic_bai4

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinbasic_bai4.ui.theme.KotlinBasicBai4Theme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TaskListActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinBasicBai4Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TaskList()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList() {
    val navController = rememberNavController()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addTask") }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        },
        content = { inner ->
            NavHost(
                navController, startDestination = "main", modifier = Modifier.padding(inner)
            ) {
                composable("main") { TaskListScreen() }
                composable("addTask") { AddTaskScreen(navController) }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskListScreen() {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    var tasks by remember { mutableStateOf(listOf<Task>()) }

    LaunchedEffect(Unit) {
        val cursor = contentResolver.query(TaskProvider.CONTENT_URI, null, null, null, null)
        tasks = cursor?.use {
            val tasksList = mutableListOf<Task>()
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(TaskDatabase.COLUMN_ID))
                val name = it.getString(it.getColumnIndexOrThrow(TaskDatabase.COLUMN_NAME))
                val dateString =
                    it.getString(it.getColumnIndexOrThrow(TaskDatabase.COLUMN_DATETIME))
                val dateTime =
                    LocalDateTime.parse(dateString) // Chuyển đổi String thành LocalDateTime
                tasksList.add(Task(id, name, dateTime))
            }
            tasksList
        } ?: emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Task List",
            modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(task)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
