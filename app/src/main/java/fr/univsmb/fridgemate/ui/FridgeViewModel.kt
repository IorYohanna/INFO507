package fr.univsmb.fridgemate.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.univsmb.fridgemate.domain.data.Unite
import fr.univsmb.fridgemate.domain.usecase.*
import fr.univsmb.fridgemate.local.ProduitDatabase
import fr.univsmb.fridgemate.model.ProduitModel
import fr.univsmb.fridgemate.model.calculerStatut
import fr.univsmb.fridgemate.model.toProduitModel
import fr.univsmb.fridgemate.remote.dto.OpenFoodFactsInfo
import fr.univsmb.fridgemate.repository.OpenFoodFactRepository
import fr.univsmb.fridgemate.repository.ProduitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException

data class AddUiState(
    val code: String = "",
    val loading: Boolean = false,
    val info: OpenFoodFactsInfo? = null,
    val message: String? = null
)

class FridgeViewModel(app: Application) : AndroidViewModel(app) {

    private val produitRepo = ProduitRepository(ProduitDatabase.get(app).produitDao())
    private val offRepo = OpenFoodFactRepository()

    private val getAll = GetAllProductUseCase(produitRepo)
    private val addOrIncrement = AddOrIncrementProductUseCase(produitRepo)
    private val consumeOne = ConsumeOneProductUseCase(produitRepo)
    private val deleteByCode = DeleteProductByCodeUseCase(produitRepo)
    private val deleteAll = DeleteAllProductsUseCase(produitRepo)
    private val upsert = UpsertProductUseCase(produitRepo)
    private val fetchInfo = FetchProductInfoUseCase(offRepo)

    val produits: StateFlow<List<ProduitModel>> = getAll()
        .map { liste ->
            liste.map { it.copy(statut = calculerStatut(it.date_expiration)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _add = MutableStateFlow(AddUiState())
    val add: StateFlow<AddUiState> = _add.asStateFlow()

    fun onCodeChange(code: String) = _add.update { it.copy(code = code, message = null) }

    fun rechercher() {
        val code = _add.value.code.trim()
        if (code.isEmpty()) return
        viewModelScope.launch {
            _add.update { it.copy(loading = true, info = null, message = null) }
            fetchInfo(code).fold(
                onSuccess = { info ->
                    _add.update {
                        it.copy(
                            loading = false,
                            info = info,
                            message = if (info == null) "Produit introuvable dans Open Food Facts" else null
                        )
                    }
                },
                onFailure = { e ->
                    _add.update { it.copy(loading = false, message = "Erreur réseau : ${e.message}") }
                }
            )
        }
    }

    fun ajouter(
        nombre: Int,
        dateExpiration: String,
        onSuccess: () -> Unit
    ) {
        val info = _add.value.info ?: return

        if (nombre <= 0) {
            _add.update { it.copy(message = "Le nombre doit être supérieur à 0") }
            return
        }
        val date = try {
            LocalDate.parse(dateExpiration.trim()).toString()
        } catch (e: DateTimeParseException) {
            _add.update { it.copy(message = "Date invalide (format attendu : yyyy-MM-dd)") }
            return
        }

        viewModelScope.launch {
            addOrIncrement(info.toProduitModel(nombre, date))
            _add.value = AddUiState()
            onSuccess()
        }
    }

    fun consommerUn(code: String) = viewModelScope.launch { consumeOne(code) }

    fun supprimer(code: String) = viewModelScope.launch { deleteByCode(code) }

    fun toutSupprimer() = viewModelScope.launch { deleteAll() }

    fun modifier(produit: ProduitModel) = viewModelScope.launch { upsert(produit) }
}