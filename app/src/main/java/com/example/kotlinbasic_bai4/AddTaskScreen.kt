package com.example.kotlinbasic_bai4

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskScreen(navController: NavController) {
    val context = LocalContext.current
    val taskName = remember { mutableStateOf("") }
    val taskDate = remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = taskName.value,
            onValueChange = { taskName.value = it },
            label = { Text("Task Name") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        DatePickerButton(
            date = taskDate.value,
            onDateChange = { date ->
                taskDate.value = date
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val values = ContentValues().apply {
                put(TaskDatabase.COLUMN_NAME, taskName.value)
                put(TaskDatabase.COLUMN_DATE, taskDate.value.toString())
            }
            context.contentResolver.insert(TaskProvider.CONTENT_URI, values)
            navController.popBackStack()
        }) {
            Text("Save Task")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerButton(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    val context = LocalContext.current
    Button(onClick = {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
        datePickerDialog.show()
    }) {
        Text(text = "Pick Date: $date")
    }
}
