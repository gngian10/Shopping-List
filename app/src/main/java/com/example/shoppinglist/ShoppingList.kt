package com.example.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ShoppingList(innerPadding: PaddingValues) {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("1") }
    var nextId by remember { mutableIntStateOf(1) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (itemName.isNotBlank()) {
                                val qty = itemQuantity.toIntOrNull() ?: 1
                                val newItem = ShoppingItem(
                                    id = nextId++,
                                    name = itemName,
                                    quantity = qty
                                )

                                sItems = sItems + newItem
                                showDialog = false
                                itemName = ""
                                itemQuantity = "1"
                            }
                        }
                    ) { Text("Add") }

                    Button(onClick = { showDialog = false }) { Text("Cancel") }
                }
            },
            title = { Text("Add Shopping Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = { showDialog = true },
        ) { Text("Add Item") }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = sItems,
                key = { it.id }
            ) { item ->
                if (item.isEditing) {
                    ShoppingItemEditor(
                        item,
                        onSave = { editedName, editedQuantity ->
                            sItems = sItems.map {
                                if (it.id == item.id) {
                                    it.copy(
                                        name = editedName,
                                        quantity = editedQuantity,
                                        isEditing = false
                                    )
                                } else {
                                    it.copy(isEditing = false)
                                }
                            }
                        },
                        onCancel = {
                            sItems = sItems.map { it.copy(isEditing = false) }
                        }
                    )
                } else {
                    ShoppingListItem(
                        item,
                        onEditClick = {
                            sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                        },
                        onDeleteClick = {
                            sItems = sItems.filterNot { it.id == item.id }
                        }
                    )
                }
            }
        }
    }
}