package app;

import java.util.ServiceLoader;

import products.Products;

public class App {
	public static void main(String[] args) {
		System.out.println("Hello app!");

		System.out.println("====");
		System.out.println("Products:");
		getProductsInstance().findAll()
			.stream()
			.map(Products.Product::getName)
			.forEach(System.out::println);
		System.out.println("====");
	}

	private static Products getProductsInstance() {
		return ServiceLoader.load(Products.class).forEach()
			.findFirst()
			.orElseGet(() -> getDefaultProductsInstance());
	}

	private static Products getDefaultProductsInstance() {
		return new Products() {
		};
	}
}