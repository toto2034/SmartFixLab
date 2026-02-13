package it.unisa.smartfixlab.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import it.unisa.smartfixlab.ui.bean.Order;
import it.unisa.smartfixlab.ui.dao.FirebaseCallback;
import it.unisa.smartfixlab.ui.dao.OrderDAO;

public class OrdersViewModel extends ViewModel {
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final OrderDAO orderDAO;

    public OrdersViewModel() {
        orderDAO = new OrderDAO();
        loadOrders();
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadOrders() {
        isLoading.setValue(true);
        orderDAO.getAll(new FirebaseCallback<Order>() {
            @Override
            public void onSuccess(List<Order> result) {
                orders.setValue(result);
                isLoading.setValue(false);
                errorMessage.setValue(null);
            }

            @Override
            public void onFailure(Exception e) {
                errorMessage.setValue(e.getMessage());
                isLoading.setValue(false);
            }
        });
    }

    public void addOrder(Order order) {
        orderDAO.insert(order);
    }
}
