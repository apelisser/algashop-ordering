package com.apelisser.algashop.ordering.core.application.customer;

import com.apelisser.algashop.ordering.core.ports.in.customer.CustomerOutput;
import com.apelisser.algashop.ordering.core.ports.in.customer.ForConfirmCustomerRegistration;
import com.apelisser.algashop.ordering.core.ports.out.customer.ForNotifyingCustomers;
import com.apelisser.algashop.ordering.core.ports.out.customer.ForObtainingCustomers;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerRegistrationConfirmationApplicationService implements ForConfirmCustomerRegistration {

    private final ForNotifyingCustomers forNotifyingCustomers;
    private final ForObtainingCustomers forObtainingCustomers;

    public CustomerRegistrationConfirmationApplicationService(ForNotifyingCustomers forNotifyingCustomers,
            ForObtainingCustomers forObtainingCustomers) {
        this.forNotifyingCustomers = forNotifyingCustomers;
        this.forObtainingCustomers = forObtainingCustomers;
    }

    @Override
    public void confirm(UUID customerId) {
        CustomerOutput customerOutput = forObtainingCustomers.findById(customerId);
        var input = new ForNotifyingCustomers.NotifyNewRegistrationInput(
            customerOutput.getId(),
            customerOutput.getFirstName(),
            customerOutput.getEmail()
        );

        forNotifyingCustomers.notifyNewRegistration(input);
    }

}
