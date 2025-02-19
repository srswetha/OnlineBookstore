package business.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingCart {

	private static final int SURCHARGE = 500;

	@JsonProperty("itemArray")
	private List<ShoppingCartItem> items;

	public ShoppingCart() {
	}

	public int getSurcharge() {
		return SURCHARGE;
	}

	public List<ShoppingCartItem> getItems() {
		return items;
	}

	public void setItems(List<ShoppingCartItem> items) {
		this.items = items;
	}

	@JsonIgnore
	public int getComputedSubtotal() {
		return items.stream()
			.mapToInt(item -> item.getQuantity() * item.getBookForm().getPrice())
			.sum();
	}

}
