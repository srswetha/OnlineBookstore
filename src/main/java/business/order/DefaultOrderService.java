package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import business.book.BookForm;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Date;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private LineItemDao lineItemDao;
	private CustomerDao customerDao;
	private OrderDao orderDao;

	public void setLineItemDao(LineItemDao lineItemDao) {
		this.lineItemDao = lineItemDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.getCustomerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.getBookId()))
				.collect(Collectors.toList());
		return new OrderDetails(order, customer, lineItems, books);
	}

	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);

		try (Connection connection = JdbcUtils.getConnection()) {
			Date date = getDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					date, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}

		//return -1;
	}
	private Date getDate(String monthString, String yearString) {
		return new GregorianCalendar(Integer.parseInt(yearString), Integer.parseInt( monthString)-1, 1).getTime();
	}

	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, Date date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, customerOrderId,
						item.getBookId(), item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}

	private int generateConfirmationNumber() {
		Random rand = new Random();
		int min = 100000;
		int max = 999999;
		int confirmationNumber = rand.nextInt((max - min) + 1) + min;
		return confirmationNumber;
	}

	private void validateCustomer(CustomerForm customerForm) {

    	String name = customerForm.getName();

		if (name == null || name.equals("") || name.length() < 4 || name.length() > 45) {
			throw new ApiException.ValidationFailure("name","Invalid name field");
		}
		String phone = customerForm.getPhone();

		String address = customerForm.getAddress();
		if (address == null || address.equals("") || address.length() < 4 || address.length() > 45) {
			throw new ApiException.ValidationFailure("address","Invalid address field");
		}

		if (phone == null || phone.equals("") || phone.replaceAll( "\\D" ,  "").length() !=10) {
			throw new ApiException.ValidationFailure("phone","Invalid phone field");
		}

		String email = customerForm.getEmail();
		if (email == null || email.length() == 0 || doesNotLookLikeAnEmail(email)||email.endsWith(".")) {
			throw new ApiException.ValidationFailure("email","Invalid email field");
		}
		String ccNumber = customerForm.getCcNumber();
		if(ccNumber==null){
			throw new ApiException.ValidationFailure("ccNumber","Invalid ccNumber field");
		}

		String cleanCCNumber = "";
		cleanCCNumber = ccNumber.replace(" ","");
		cleanCCNumber = cleanCCNumber.replace("-","");

		if(cleanCCNumber.length() < 14 || cleanCCNumber.length() > 16){
			throw new ApiException.ValidationFailure("ccNumber","Invalid ccNumber field");
		}
		if (expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.ValidationFailure("Please enter a valid expiration date.");

		}
	}
	private static Pattern SIMPLE_EMAIL_REGEX = Pattern.compile("^\\S+@\\S+$");
	private boolean doesNotLookLikeAnEmail(String email) {
		return !SIMPLE_EMAIL_REGEX.matcher(email).matches();
	}



	private static boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) throws ApiException.ValidationFailure {
		try {
			YearMonth currentMonthYear = YearMonth.now();
			YearMonth expiryMonthYear = YearMonth.of(Integer.parseInt(ccExpiryYear), Integer.parseInt(ccExpiryMonth));
			if (expiryMonthYear.isBefore(currentMonthYear)) {
				return true;
			}
			return false;
		} catch (NumberFormatException e) {
			throw new ApiException.ValidationFailure("Invalid credit card expiry date format.");
		} catch (Exception e) {
			throw new ApiException.ValidationFailure("Unexpected error occurred while validating credit card expiry date.");
		}
	}

		private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.ValidationFailure("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.ValidationFailure("Invalid quantity");
			}
			BookForm bookRequested = item.getBookForm();
			Book databaseBook = bookDao.findByBookId(item.getBookId());

			if (bookRequested.getPrice() != databaseBook.getPrice()) {
				throw new ApiException.ValidationFailure("Invalid price");
			}
			if (bookRequested.getCategoryId() != databaseBook.getCategoryId()) {
				throw new ApiException.ValidationFailure("Invalid category");
			}


		});
	}

}
