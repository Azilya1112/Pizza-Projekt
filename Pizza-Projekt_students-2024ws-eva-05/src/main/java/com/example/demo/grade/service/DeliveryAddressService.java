package com.example.demo.grade.service;

import com.example.demo.grade.domain.DeliveryAddress;
import com.example.demo.grade.domain.User;
import com.example.demo.grade.repository.DeliveryAddressRepository;
import com.example.demo.grade.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public DeliveryAddressService(DeliveryAddressRepository deliveryAddressRepository, UserService userService, UserRepository userRepository) {
        this.deliveryAddressRepository = deliveryAddressRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public List<DeliveryAddress> getAddressesForUser(Long userId) {
        return deliveryAddressRepository.findByUserId(userId);
    }

    public DeliveryAddress save(DeliveryAddress address) {
        deliveryAddressRepository.save(address);
        return address;
    }

    public List<DeliveryAddress> getAllAddresses(User user) {
        return deliveryAddressRepository.findByUserId(user.getId());
    }

    public DeliveryAddress getAddressById(Long addressId) { return deliveryAddressRepository.findById(addressId).get(); }

    @Transactional
    public void addDeliveryAddress(Long userId, String postalCode, String town, String houseNumber, String street) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with " + userId + " not found."));

        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setPostalCode(postalCode);
        deliveryAddress.setTown(town);
        deliveryAddress.setHouseNumber(houseNumber);
        deliveryAddress.setStreet(street);
        deliveryAddress.setUser(user);
        user.addDeliveryAddress(deliveryAddress);


        userRepository.save(user);
    }

    @Transactional
    public void updateDeliveryAddress(Long addressId, Long userId, String postalCode, String town, String houseNumber, String street) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Адрес с ID " + addressId + " не найден."));

        if (!deliveryAddress.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Этот адрес не принадлежит текущему пользователю.");
        }

        deliveryAddress.setPostalCode(postalCode);
        deliveryAddress.setTown(town);
        deliveryAddress.setHouseNumber(houseNumber);
        deliveryAddress.setStreet(street);

        deliveryAddressRepository.save(deliveryAddress);
    }

    @Transactional
    public void removeUserFromAddress(Long addressId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        DeliveryAddress address = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found."));

        user.getDeliveryAddresses().remove(address);

        if (address.getUser() != null && address.getUser().getId().equals(userId)) {
            address.setUser(null);
            deliveryAddressRepository.save(address);
        }
        userRepository.save(user);
    }


}