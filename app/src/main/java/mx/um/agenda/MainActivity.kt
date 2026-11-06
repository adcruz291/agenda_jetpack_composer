package mx.um.agenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.um.agenda.ui.theme.AgendaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgendaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CrudDiccionarioScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

//Modelo simple para los datos almacenados
data class Persona(val nombre: String, val edad: Int)


fun text(nombre: String): String {
    val edad = 20
    return "Hola $nombre, tienes ${edad + 1} el próximo año"
}

@Composable
fun CrudDiccionarioScreen(modifier: Modifier = Modifier) {
    // Diccionario reactivo: clave = ID (Int), valor = Objeto Persona
    val agenda = remember {
        mutableStateMapOf(
            1 to Persona("Daniel", 20),
            2 to Persona("Ana", 22)
        )
    }

    // Estados para los campos de texto
    var idTexto by remember { mutableStateOf("") }
    var nombreTexto by remember { mutableStateOf("") }
    var edadTexto by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var editandoId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "CRUD con Diccionario (Map)",
            style = MaterialTheme.typography.titleLarge
        )

        // Campo ID
        OutlinedTextField(
            value = idTexto,
            onValueChange = { idTexto = it },
            label = { Text("ID numérico") },
            enabled = editandoId == null, // Bloquear edición de clave primaria durante actualización
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Nombre
        OutlinedTextField(
            value = nombreTexto,
            onValueChange = { nombreTexto = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Edad
        OutlinedTextField(
            value = edadTexto,
            onValueChange = { edadTexto = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón de acción: Crear o Actualizar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val id = idTexto.toIntOrNull()
                    val edad = edadTexto.toIntOrNull()

                    if (id == null || nombreTexto.isBlank() || edad == null) {
                        mensajeError = "Llena todos los campos con valores válidos."
                        return@Button
                    }

                    // CREATE / UPDATE: Asignación por clave en el diccionario
                    agenda[id] = Persona(nombre = nombreTexto.trim(), edad = edad)

                    // Limpiar formulario
                    idTexto = ""
                    nombreTexto = ""
                    edadTexto = ""
                    editandoId = null
                    mensajeError = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (editandoId == null) "Agregar (Create)" else "Guardar (Update)")
            }

            if (editandoId != null) {
                OutlinedButton(onClick = {
                    idTexto = ""
                    nombreTexto = ""
                    edadTexto = ""
                    editandoId = null
                }) {
                    Text("Cancelar")
                }
            }
        }

        mensajeError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // READ: Listado de elementos del diccionario
        Text(
            text = "Registros (${agenda.size})",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(agenda.entries.toList(), key = { it.key }) { (id, persona) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ID: $id", style = MaterialTheme.typography.bodySmall)
                            Text(text = persona.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(text = "${persona.edad} años", style = MaterialTheme.typography.bodyMedium)
                        }

                        Row {
                            // Cargar datos en los inputs para UPDATE
                            IconButton(onClick = {
                                idTexto = id.toString()
                                nombreTexto = persona.nombre
                                edadTexto = persona.edad.toString()
                                editandoId = id
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            // DELETE: Remover por clave
                            IconButton(onClick = {
                                agenda.remove(id)
                                if (editandoId == id) {
                                    idTexto = ""
                                    nombreTexto = ""
                                    edadTexto = ""
                                    editandoId = null
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = text(name),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AgendaTheme {
        //Greeting("Daniel")
        CrudDiccionarioScreen()
    }
}