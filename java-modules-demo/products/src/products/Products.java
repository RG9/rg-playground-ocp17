package products;

import java.util.List;

public interface Products {

	default List<Product> findAll() {
		return List.of(
			() -> "Pizza",
			() -> "Pasta"
		);
	}

	interface Product {
		String getName();
	}
}