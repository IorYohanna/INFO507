
package fr.univsmb.fridgemate.ui.features

import android.app.DatePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import fr.univsmb.fridgemate.ui.FridgeViewModel
import fr.univsmb.fridgemate.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FieldShape = RoundedCornerShape(17.dp)
private val BorderColor = Color(0xFFE1E9E3)

@Composable
fun AddProductScreen(
    vm: FridgeViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val add by vm.add.collectAsState()
    val context = LocalContext.current

    var quantity by remember { mutableIntStateOf(1) }
    var expiration by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var showManual by remember {
        mutableStateOf(false)
    }
    var scannerError by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        vm.resetAjout()
    }

    // Scanner Google Code Scanner
    val scanner = remember(context) {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E
            )
            .enableAutoZoom()
            .build()

        GmsBarcodeScanning.getClient(context, options)
    }

    val scan: () -> Unit = {
        scannerError = null

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val code = barcode.rawValue.orEmpty()

                if (code.isNotBlank()) {
                    vm.onCodeChange(code)
                    vm.rechercher()
                    showManual = false
                } else {
                    scannerError = "Code-barres non reconnu"
                }
            }
            .addOnFailureListener {
                scannerError =
                    "Impossible d'ouvrir le scanner : ${it.localizedMessage}"
            }
    }

    val chooseDate: () -> Unit = {
        val date = expiration ?: LocalDate.now()

        DatePickerDialog(
            context,
            { _, year, month, day ->
                expiration = LocalDate.of(
                    year,
                    month + 1,
                    day
                )
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        ).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhite)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
    ) {
        Spacer(Modifier.height(22.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDone,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White,
                        RoundedCornerShape(13.dp)
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = DeepGreen
                )
            }

            Spacer(Modifier.width(14.dp))

            Text(
                "Ajouter au frigo",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = DeepGreen
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Un produit de plus,",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            color = DeepGreen
        )

        Text(
            "moins de gaspillage.",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Scanne le code-barres pour retrouver ton produit automatiquement.",
            color = Color(0xFF7D8D83),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(25.dp))

        // Carte du scanner
        Card(
            colors = CardDefaults.cardColors(
                containerColor = SoftGreen
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(37.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "Scanner un produit",
                    fontSize = 19.sp,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "La caméra détecte le code-barres automatiquement",
                    color = Color(0xFF73867B),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = scan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp),
                    shape = FieldShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen
                    )
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        if (add.info == null)
                            "Ouvrir la caméra"
                        else
                            "Scanner un autre produit",
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { showManual = !showManual }
                ) {
                    Text(
                        if (showManual)
                            "Masquer la saisie manuelle"
                        else
                            "Saisir un code manuellement",
                        color = ForestGreen
                    )
                }
            }
        }

        // Recherche manuelle optionnelle
        if (showManual) {
            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = add.code,
                onValueChange = vm::onCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Code-barres EAN / UPC") },
                singleLine = true,
                shape = FieldShape
            )

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = vm::rechercher,
                enabled = add.code.isNotBlank() && !add.loading,
                modifier = Modifier.fillMaxWidth(),
                shape = FieldShape
            ) {
                Text("Rechercher sur Open Food Facts")
            }
        }

        scannerError?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        add.message?.let {
            Spacer(Modifier.height(10.dp))
            Text(
                it,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (add.loading) {
            Spacer(Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = ForestGreen
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    "Recherche sur Open Food Facts...",
                    color = ForestGreen
                )
            }
        }

        // Formulaire après reconnaissance du produit
        add.info?.let { info ->
            Spacer(Modifier.height(24.dp))

            Text(
                "PRODUIT IDENTIFIÉ",
                color = ForestGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = info.imageUrl,
                        contentDescription = info.nom,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            info.nom,
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen,
                            fontSize = 17.sp
                        )

                        Text(
                            info.marque ?: "Marque inconnue",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )

                        Text(
                            info.quantity ?: "Contenance inconnue",
                            color = ForestGreen,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ForestGreen
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "QUANTITÉ",
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, FieldShape)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Nombre de produits",
                        color = DeepGreen,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Combien souhaites-tu ajouter ?",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = {
                        if (quantity > 1) quantity--
                    },
                    enabled = quantity > 1
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Diminuer",
                        tint = ForestGreen
                    )
                }

                Text(
                    "$quantity",
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = DeepGreen
                )

                IconButton(
                    onClick = { quantity++ }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Augmenter",
                        tint = ForestGreen
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "DATE D'EXPIRATION",
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FieldShape)
                    .background(Color.White)
                    .clickable(onClick = chooseDate)
                    .padding(17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ForestGreen
                )

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        expiration?.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy",
                                Locale.FRENCH
                            )
                        ) ?: "Choisir une date",
                        fontWeight = FontWeight.SemiBold,
                        color = DeepGreen
                    )

                    Text(
                        "Nous te préviendrons 7 jours avant",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ForestGreen
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Cream, FieldShape)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = Amber
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    "Rappel automatique une semaine avant la date d'expiration.",
                    color = Color(0xFF8C5A16),
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    expiration?.let {
                        vm.ajouter(
                            quantity,
                            it.toString(),
                            onDone
                        )
                    }
                },
                enabled = expiration != null && !add.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = FieldShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen
                )
            ) {
                Icon(
                    Icons.Default.Kitchen,
                    contentDescription = null
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    "Ajouter $quantity produit${if (quantity > 1) "s" else ""} au frigo",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(34.dp))
    }
}
