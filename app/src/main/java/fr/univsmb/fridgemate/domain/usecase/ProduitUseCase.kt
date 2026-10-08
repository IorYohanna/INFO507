    package fr.univsmb.fridgemate.domain.usecase

    import fr.univsmb.fridgemate.model.ProduitModel
    import fr.univsmb.fridgemate.repository.ProduitRepository
    import kotlinx.coroutines.flow.Flow

    class GetAllProductUseCase(
        private val repository: ProduitRepository
    ) {
        operator fun invoke(): Flow<List<ProduitModel>> {
            return repository.getAllProduits()
        }
    }

    class GetProductByCodeUseCase(
        private val repository: ProduitRepository
    ) {
        suspend operator fun invoke(code: String): ProduitModel? {
            return repository.getProduitByCode(code)
        }
    }

    class DeleteProductByCodeUseCase(
        private val repository: ProduitRepository
    ) {
        suspend operator fun invoke(code: String) {
            repository.supprimerProduitByCode(code)
        }
    }

    class DeleteAllProductsUseCase(
        private val repository: ProduitRepository
    ) {
        suspend operator fun invoke() {
            repository.supprimerTousLesProduits()
        }
    }

    class UpsertProductUseCase(
        private val repository: ProduitRepository
    ) {
        suspend operator fun invoke(produit: ProduitModel) {
            repository.upsertProduit(produit)
        }
    }