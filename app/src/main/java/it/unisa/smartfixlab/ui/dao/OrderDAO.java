package it.unisa.smartfixlab.ui.dao;

import com.google.firebase.firestore.CollectionReference;
import java.util.ArrayList;
import java.util.List;
import it.unisa.smartfixlab.ui.bean.Order;
import it.unisa.smartfixlab.util.FirebaseConfig;

public class OrderDAO {
    private final CollectionReference collectionRef;

    public OrderDAO() {
        this.collectionRef = FirebaseConfig.getCollection("orders");
    }

    public void insert(Order order) {
        collectionRef.document(order.getId()).set(order);
    }

    public void update(Order order) {
        collectionRef.document(order.getId()).set(order);
    }

    public void delete(String id) {
        collectionRef.document(id).delete();
    }

    public void getAll(FirebaseCallback<Order> callback) {
        collectionRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                callback.onFailure(error);
                return;
            }
            if (value != null) {
                List<Order> orders = new ArrayList<>();
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    try {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            orders.add(order);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(orders);
            }
        });
    }

    public void getById(String id, FirebaseCallback<Order> callback) {
        collectionRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Order> result = new ArrayList<>();
                com.google.firebase.firestore.DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Order order = document.toObject(Order.class);
                    if (order != null) {
                        result.add(order);
                    }
                }
                callback.onSuccess(result);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }
}
