package pl.products;

import java.util.List;

import products.Products;

public class PolishProducts implements Products {

	@Override
	public List<Products.Product> findAll() {
		return List.of(
			() -> "Pierogi",
			() -> "Żubrówka"
		);
	}
}